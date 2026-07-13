-- =========================================================
-- TEST DATA ONLY: text@gmail.com / 12345678
-- =========================================================
--
-- Purpose:
--   Creates one test user with five individual expense dashboards.
--   Each dashboard has sample income/expense data for easy UI testing.
--
-- Login:
--   Email:    text@gmail.com
--   Password: 12345678
--
-- How to run manually from psql:
--   psql "postgresql://shrawn:shrawn@localhost:5432/shrawn" -f backend/src/main/resources/db/test-data/text_gmail_test_seed.sql
--
-- How to remove later:
--   Delete this file when you do not need it.
--   If you already ran it, run the CLEANUP block near the bottom of this file.
--
-- Important:
--   This is NOT a Flyway migration. It will not run automatically.
--   That is intentional so you can delete it later without changing app code.

BEGIN;

-- Clean previous copy of this test data so running this file again is safe.
DO $$
DECLARE
    test_user_id UUID;
BEGIN
    SELECT id INTO test_user_id
    FROM users
    WHERE email = 'text@gmail.com';

    IF test_user_id IS NOT NULL THEN
        DELETE FROM expense_tags
        WHERE expense_id IN (
            SELECT id FROM expenses WHERE user_id = test_user_id
        );

        DELETE FROM expenses WHERE user_id = test_user_id;
        DELETE FROM budgets WHERE user_id = test_user_id;
        DELETE FROM categories WHERE user_id = test_user_id;
        DELETE FROM tags WHERE user_id = test_user_id;
        DELETE FROM expense_dashboards WHERE user_id = test_user_id;
        DELETE FROM refresh_tokens WHERE user_id = test_user_id;
        DELETE FROM users WHERE id = test_user_id;
    END IF;
END $$;

-- Create test user. pgcrypto creates a BCrypt password hash for 12345678.
INSERT INTO users (
    email,
    full_name,
    password_hash,
    status
) VALUES (
    'text@gmail.com',
    'Text Test User',
    crypt('12345678', gen_salt('bf', 12)),
    'ACTIVE'
);

-- Create five individual dashboards.
INSERT INTO expense_dashboards (user_id, name, description)
SELECT id, 'Personal Daily', 'Daily personal spending and pocket money.'
FROM users
WHERE email = 'text@gmail.com';

INSERT INTO expense_dashboards (user_id, name, description)
SELECT id, 'Family Home', 'Home groceries, rent, bills, and family expenses.'
FROM users
WHERE email = 'text@gmail.com';

INSERT INTO expense_dashboards (user_id, name, description)
SELECT id, 'Office Work', 'Office travel, lunch, subscriptions, and work income.'
FROM users
WHERE email = 'text@gmail.com';

INSERT INTO expense_dashboards (user_id, name, description)
SELECT id, 'Goa Trip', 'Travel, hotel, food, and shopping expenses for a trip.'
FROM users
WHERE email = 'text@gmail.com';

INSERT INTO expense_dashboards (user_id, name, description)
SELECT id, 'Side Business', 'Freelance income, tools, ads, and client costs.'
FROM users
WHERE email = 'text@gmail.com';

-- Shared categories for this test user.
INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Food', 'Meals, snacks, groceries, and drinks.', 'EXPENSE', true
FROM users WHERE email = 'text@gmail.com';

INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Transport', 'Fuel, cab, bus, train, and commute.', 'EXPENSE', true
FROM users WHERE email = 'text@gmail.com';

INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Bills', 'Electricity, internet, phone, rent, and utilities.', 'EXPENSE', true
FROM users WHERE email = 'text@gmail.com';

INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Shopping', 'Clothes, gadgets, gifts, and personal items.', 'EXPENSE', true
FROM users WHERE email = 'text@gmail.com';

INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Travel', 'Hotel, tickets, sightseeing, and trip costs.', 'EXPENSE', true
FROM users WHERE email = 'text@gmail.com';

INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Business Tools', 'Software, ads, hosting, and work tools.', 'EXPENSE', true
FROM users WHERE email = 'text@gmail.com';

INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Salary', 'Monthly salary income.', 'INCOME', true
FROM users WHERE email = 'text@gmail.com';

INSERT INTO categories (user_id, name, description, type, active)
SELECT id, 'Freelance', 'Freelance and side income.', 'INCOME', true
FROM users WHERE email = 'text@gmail.com';

-- Tags for sample expenses.
INSERT INTO tags (user_id, name, active)
SELECT id, 'monthly', true FROM users WHERE email = 'text@gmail.com';

INSERT INTO tags (user_id, name, active)
SELECT id, 'cash', true FROM users WHERE email = 'text@gmail.com';

INSERT INTO tags (user_id, name, active)
SELECT id, 'online', true FROM users WHERE email = 'text@gmail.com';

INSERT INTO tags (user_id, name, active)
SELECT id, 'important', true FROM users WHERE email = 'text@gmail.com';

INSERT INTO tags (user_id, name, active)
SELECT id, 'trip', true FROM users WHERE email = 'text@gmail.com';

-- Personal Daily dashboard sample data.
INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 80000.00, 'Monthly salary credited', 'INCOME', 'BANK_TRANSFER', current_date - 9, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Personal Daily'
  AND c.user_id = u.id AND c.name = 'Salary' AND c.type = 'INCOME';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 280.00, 'Evening snacks and tea', 'EXPENSE', 'UPI', current_date - 8, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Personal Daily'
  AND c.user_id = u.id AND c.name = 'Food' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 1200.00, 'New headphones', 'EXPENSE', 'CARD', current_date - 6, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Personal Daily'
  AND c.user_id = u.id AND c.name = 'Shopping' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 180.00, 'Auto ride to market', 'EXPENSE', 'CASH', current_date - 3, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Personal Daily'
  AND c.user_id = u.id AND c.name = 'Transport' AND c.type = 'EXPENSE';

-- Family Home dashboard sample data.
INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 18000.00, 'House rent', 'EXPENSE', 'BANK_TRANSFER', current_date - 10, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Family Home'
  AND c.user_id = u.id AND c.name = 'Bills' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 3400.00, 'Monthly groceries', 'EXPENSE', 'UPI', current_date - 7, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Family Home'
  AND c.user_id = u.id AND c.name = 'Food' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 1450.00, 'Internet and mobile bill', 'EXPENSE', 'CARD', current_date - 5, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Family Home'
  AND c.user_id = u.id AND c.name = 'Bills' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 2100.00, 'Family dinner outside', 'EXPENSE', 'UPI', current_date - 2, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Family Home'
  AND c.user_id = u.id AND c.name = 'Food' AND c.type = 'EXPENSE';

-- Office Work dashboard sample data.
INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 12000.00, 'Office reimbursement', 'INCOME', 'BANK_TRANSFER', current_date - 12, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Office Work'
  AND c.user_id = u.id AND c.name = 'Salary' AND c.type = 'INCOME';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 950.00, 'Office cab rides', 'EXPENSE', 'UPI', current_date - 9, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Office Work'
  AND c.user_id = u.id AND c.name = 'Transport' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 620.00, 'Team lunch', 'EXPENSE', 'CARD', current_date - 4, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Office Work'
  AND c.user_id = u.id AND c.name = 'Food' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 999.00, 'Productivity app subscription', 'EXPENSE', 'CARD', current_date - 1, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Office Work'
  AND c.user_id = u.id AND c.name = 'Business Tools' AND c.type = 'EXPENSE';

-- Goa Trip dashboard sample data.
INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 15000.00, 'Trip budget added', 'INCOME', 'BANK_TRANSFER', current_date - 15, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Goa Trip'
  AND c.user_id = u.id AND c.name = 'Freelance' AND c.type = 'INCOME';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 5200.00, 'Hotel booking', 'EXPENSE', 'CARD', current_date - 14, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Goa Trip'
  AND c.user_id = u.id AND c.name = 'Travel' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 2600.00, 'Train tickets', 'EXPENSE', 'UPI', current_date - 13, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Goa Trip'
  AND c.user_id = u.id AND c.name = 'Travel' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 1900.00, 'Beach cafe food', 'EXPENSE', 'CASH', current_date - 11, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Goa Trip'
  AND c.user_id = u.id AND c.name = 'Food' AND c.type = 'EXPENSE';

-- Side Business dashboard sample data.
INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 22000.00, 'Client website payment', 'INCOME', 'BANK_TRANSFER', current_date - 8, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Side Business'
  AND c.user_id = u.id AND c.name = 'Freelance' AND c.type = 'INCOME';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 1800.00, 'Domain and hosting renewal', 'EXPENSE', 'CARD', current_date - 7, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Side Business'
  AND c.user_id = u.id AND c.name = 'Business Tools' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 3000.00, 'Online ad campaign', 'EXPENSE', 'CARD', current_date - 4, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Side Business'
  AND c.user_id = u.id AND c.name = 'Business Tools' AND c.type = 'EXPENSE';

INSERT INTO expenses (user_id, dashboard_id, amount, description, type, payment_method, expense_date, category_id)
SELECT u.id, d.id, 750.00, 'Client meeting coffee', 'EXPENSE', 'UPI', current_date - 2, c.id
FROM users u, expense_dashboards d, categories c
WHERE u.email = 'text@gmail.com'
  AND d.user_id = u.id AND d.name = 'Side Business'
  AND c.user_id = u.id AND c.name = 'Food' AND c.type = 'EXPENSE';

-- Add a few tags to matching sample expenses.
INSERT INTO expense_tags (expense_id, tag_id)
SELECT e.id, t.id
FROM expenses e
JOIN users u ON u.id = e.user_id
JOIN tags t ON t.user_id = u.id
WHERE u.email = 'text@gmail.com'
  AND t.name = 'monthly'
  AND e.description IN ('Monthly salary credited', 'House rent', 'Internet and mobile bill');

INSERT INTO expense_tags (expense_id, tag_id)
SELECT e.id, t.id
FROM expenses e
JOIN users u ON u.id = e.user_id
JOIN tags t ON t.user_id = u.id
WHERE u.email = 'text@gmail.com'
  AND t.name = 'trip'
  AND e.dashboard_id IN (
      SELECT id FROM expense_dashboards WHERE user_id = u.id AND name = 'Goa Trip'
  );

INSERT INTO expense_tags (expense_id, tag_id)
SELECT e.id, t.id
FROM expenses e
JOIN users u ON u.id = e.user_id
JOIN tags t ON t.user_id = u.id
WHERE u.email = 'text@gmail.com'
  AND t.name = 'online'
  AND e.payment_method IN ('CARD', 'BANK_TRANSFER');

COMMIT;

-- =========================================================
-- CLEANUP BLOCK
-- =========================================================
-- Run this block manually if you want to remove only this test user and data.
--
-- BEGIN;
-- DO $$
-- DECLARE
--     test_user_id UUID;
-- BEGIN
--     SELECT id INTO test_user_id
--     FROM users
--     WHERE email = 'text@gmail.com';
--
--     IF test_user_id IS NOT NULL THEN
--         DELETE FROM expense_tags
--         WHERE expense_id IN (
--             SELECT id FROM expenses WHERE user_id = test_user_id
--         );
--
--         DELETE FROM expenses WHERE user_id = test_user_id;
--         DELETE FROM budgets WHERE user_id = test_user_id;
--         DELETE FROM categories WHERE user_id = test_user_id;
--         DELETE FROM tags WHERE user_id = test_user_id;
--         DELETE FROM expense_dashboards WHERE user_id = test_user_id;
--         DELETE FROM refresh_tokens WHERE user_id = test_user_id;
--         DELETE FROM users WHERE id = test_user_id;
--     END IF;
-- END $$;
-- COMMIT;
