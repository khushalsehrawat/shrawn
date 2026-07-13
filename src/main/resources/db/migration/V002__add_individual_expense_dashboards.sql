-- =========================================================
-- Individual Expense Dashboards
-- Migration: V002__add_individual_expense_dashboards.sql
-- =========================================================

CREATE TABLE expense_dashboards (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    version BIGINT NOT NULL DEFAULT 0,

                                    user_id UUID NOT NULL,
                                    name VARCHAR(80) NOT NULL,
                                    description VARCHAR(255),
                                    active BOOLEAN NOT NULL DEFAULT true,

                                    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                                    CONSTRAINT fk_expense_dashboards_user
                                        FOREIGN KEY (user_id)
                                            REFERENCES users (id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT chk_expense_dashboards_name_not_blank CHECK (
                                        length(trim(name)) > 0
                                        )
);

CREATE UNIQUE INDEX uk_expense_dashboards_user_lower_name
    ON expense_dashboards (user_id, lower(name));

CREATE INDEX idx_expense_dashboards_user_id ON expense_dashboards (user_id);
CREATE INDEX idx_expense_dashboards_user_active ON expense_dashboards (user_id, active);

INSERT INTO expense_dashboards (user_id, name, description)
SELECT id, 'Main Dashboard', 'Default dashboard for existing expenses.'
FROM users;

ALTER TABLE expenses
    ADD COLUMN dashboard_id UUID;

UPDATE expenses e
SET dashboard_id = d.id
FROM expense_dashboards d
WHERE d.user_id = e.user_id
  AND d.name = 'Main Dashboard';

ALTER TABLE expenses
    ALTER COLUMN dashboard_id SET NOT NULL,
    ADD CONSTRAINT fk_expenses_dashboard
        FOREIGN KEY (dashboard_id)
            REFERENCES expense_dashboards (id)
            ON DELETE RESTRICT;

CREATE INDEX idx_expenses_user_dashboard ON expenses (user_id, dashboard_id);
CREATE INDEX idx_expenses_user_dashboard_date ON expenses (user_id, dashboard_id, expense_date);
