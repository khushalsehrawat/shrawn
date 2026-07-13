-- =========================================================
-- Shrawn Initial Database Schema
-- Migration: V001__create_initial_schema.sql
-- Database: PostgreSQL
-- =========================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================================================
-- USERS
-- =========================================================

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       version BIGINT NOT NULL DEFAULT 0,

                       email VARCHAR(254) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       password_hash VARCHAR(100) NOT NULL,
                       status VARCHAR(50) NOT NULL,

                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT chk_users_status CHECK (
                           status IN (
                                      'ACTIVE',
                                      'EMAIL_VERIFICATION_PENDING',
                                      'SUSPENDED',
                                      'DELETED'
                               )
                           ),
                       CONSTRAINT chk_users_email_not_blank CHECK (length(trim(email)) > 0),
                       CONSTRAINT chk_users_full_name_not_blank CHECK (length(trim(full_name)) > 0),
                       CONSTRAINT chk_users_password_hash_not_blank CHECK (length(trim(password_hash)) > 0)
);

CREATE INDEX idx_users_status ON users (status);


-- =========================================================
-- REFRESH TOKENS
-- =========================================================

CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                version BIGINT NOT NULL DEFAULT 0,

                                user_id UUID NOT NULL,
                                token_hash VARCHAR(128) NOT NULL,
                                status VARCHAR(30) NOT NULL,
                                expires_at TIMESTAMPTZ NOT NULL,
                                last_used_at TIMESTAMPTZ,

                                created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users (id)
                                        ON DELETE CASCADE,

                                CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),

                                CONSTRAINT chk_refresh_tokens_status CHECK (
                                    status IN ('ACTIVE', 'REVOKED', 'EXPIRED')
                                    ),

                                CONSTRAINT chk_refresh_tokens_token_hash_not_blank CHECK (
                                    length(trim(token_hash)) > 0
                                    )
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_status ON refresh_tokens (status);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);


-- =========================================================
-- CATEGORIES
-- =========================================================

CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            version BIGINT NOT NULL DEFAULT 0,

                            user_id UUID NOT NULL,
                            name VARCHAR(60) NOT NULL,
                            description VARCHAR(255),
                            type VARCHAR(30) NOT NULL,
                            active BOOLEAN NOT NULL DEFAULT true,

                            created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                            updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                            CONSTRAINT fk_categories_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users (id)
                                    ON DELETE CASCADE,

                            CONSTRAINT chk_categories_type CHECK (
                                type IN ('EXPENSE', 'INCOME')
                                ),

                            CONSTRAINT chk_categories_name_not_blank CHECK (
                                length(trim(name)) > 0
                                )
);

CREATE UNIQUE INDEX uk_categories_user_lower_name_type
    ON categories (user_id, lower(name), type);

CREATE INDEX idx_categories_user_id ON categories (user_id);
CREATE INDEX idx_categories_user_active ON categories (user_id, active);
CREATE INDEX idx_categories_user_type ON categories (user_id, type);


-- =========================================================
-- TAGS
-- =========================================================

CREATE TABLE tags (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      version BIGINT NOT NULL DEFAULT 0,

                      user_id UUID NOT NULL,
                      name VARCHAR(40) NOT NULL,
                      active BOOLEAN NOT NULL DEFAULT true,

                      created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                      updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                      CONSTRAINT fk_tags_user
                          FOREIGN KEY (user_id)
                              REFERENCES users (id)
                              ON DELETE CASCADE,

                      CONSTRAINT chk_tags_name_not_blank CHECK (
                          length(trim(name)) > 0
                          )
);

CREATE UNIQUE INDEX uk_tags_user_lower_name
    ON tags (user_id, lower(name));

CREATE INDEX idx_tags_user_id ON tags (user_id);
CREATE INDEX idx_tags_user_active ON tags (user_id, active);


-- =========================================================
-- EXPENSES
-- =========================================================

CREATE TABLE expenses (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          version BIGINT NOT NULL DEFAULT 0,

                          user_id UUID NOT NULL,
                          amount NUMERIC(19, 2) NOT NULL,
                          description VARCHAR(255) NOT NULL,
                          type VARCHAR(30) NOT NULL,
                          payment_method VARCHAR(40) NOT NULL,
                          expense_date DATE NOT NULL,
                          category_id UUID NOT NULL,

                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                          CONSTRAINT fk_expenses_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users (id)
                                  ON DELETE CASCADE,

                          CONSTRAINT fk_expenses_category
                              FOREIGN KEY (category_id)
                                  REFERENCES categories (id)
                                  ON DELETE RESTRICT,

                          CONSTRAINT chk_expenses_amount_positive CHECK (amount > 0),

                          CONSTRAINT chk_expenses_description_not_blank CHECK (
                              length(trim(description)) > 0
                              ),

                          CONSTRAINT chk_expenses_type CHECK (
                              type IN ('EXPENSE', 'INCOME')
                              ),

                          CONSTRAINT chk_expenses_payment_method CHECK (
                              payment_method IN (
                                                 'CASH',
                                                 'CARD',
                                                 'UPI',
                                                 'BANK_TRANSFER',
                                                 'WALLET',
                                                 'OTHER'
                                  )
                              )
);

CREATE INDEX idx_expenses_user_id ON expenses (user_id);
CREATE INDEX idx_expenses_user_expense_date ON expenses (user_id, expense_date);
CREATE INDEX idx_expenses_user_category ON expenses (user_id, category_id);
CREATE INDEX idx_expenses_user_type_date ON expenses (user_id, type, expense_date);
CREATE INDEX idx_expenses_user_payment_method_date ON expenses (user_id, payment_method, expense_date);


-- =========================================================
-- EXPENSE TAGS
-- =========================================================

CREATE TABLE expense_tags (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              version BIGINT NOT NULL DEFAULT 0,

                              expense_id UUID NOT NULL,
                              tag_id UUID NOT NULL,

                              CONSTRAINT fk_expense_tags_expense
                                  FOREIGN KEY (expense_id)
                                      REFERENCES expenses (id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_expense_tags_tag
                                  FOREIGN KEY (tag_id)
                                      REFERENCES tags (id)
                                      ON DELETE RESTRICT,

                              CONSTRAINT uk_expense_tags_expense_tag UNIQUE (expense_id, tag_id)
);

CREATE INDEX idx_expense_tags_expense_id ON expense_tags (expense_id);
CREATE INDEX idx_expense_tags_tag_id ON expense_tags (tag_id);


-- =========================================================
-- BUDGETS
-- =========================================================

CREATE TABLE budgets (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         version BIGINT NOT NULL DEFAULT 0,

                         user_id UUID NOT NULL,
                         name VARCHAR(80) NOT NULL,
                         limit_amount NUMERIC(19, 2) NOT NULL,
                         period_type VARCHAR(30) NOT NULL,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,
                         category_id UUID,
                         active BOOLEAN NOT NULL DEFAULT true,

                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                         CONSTRAINT fk_budgets_user
                             FOREIGN KEY (user_id)
                                 REFERENCES users (id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_budgets_category
                             FOREIGN KEY (category_id)
                                 REFERENCES categories (id)
                                 ON DELETE RESTRICT,

                         CONSTRAINT chk_budgets_name_not_blank CHECK (
                             length(trim(name)) > 0
                             ),

                         CONSTRAINT chk_budgets_limit_amount_positive CHECK (
                             limit_amount > 0
                             ),

                         CONSTRAINT chk_budgets_period_type CHECK (
                             period_type IN ('MONTHLY', 'CUSTOM')
                             ),

                         CONSTRAINT chk_budgets_valid_date_range CHECK (
                             end_date >= start_date
                             )
);

CREATE INDEX idx_budgets_user_id ON budgets (user_id);
CREATE INDEX idx_budgets_user_active ON budgets (user_id, active);
CREATE INDEX idx_budgets_user_date_range ON budgets (user_id, start_date, end_date);
CREATE INDEX idx_budgets_user_category ON budgets (user_id, category_id);