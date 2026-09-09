-- liquibase formatted sql

-- changeset codex:002-drop-role-column
-- preconditions onFail:MARK_RAN onError:HALT
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'app_user' AND column_name = 'role'
ALTER TABLE app_user DROP COLUMN role;
