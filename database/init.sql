-- TextRover Database Initialization Script
-- Creates schema and sets up permissions for the textrover_user

-- Create the textrover schema
CREATE SCHEMA IF NOT EXISTS textrover;

-- Grant usage on schema to textrover_user
GRANT USAGE ON SCHEMA textrover TO textrover_user;

-- Grant all privileges on all tables in the schema (current and future)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA textrover TO textrover_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA textrover TO textrover_user;

-- Grant privileges for future tables and sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA textrover GRANT ALL PRIVILEGES ON TABLES TO textrover_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA textrover GRANT ALL PRIVILEGES ON SEQUENCES TO textrover_user;

-- Set default search path for textrover_user
ALTER USER textrover_user SET search_path = textrover, public;

-- Create a test connection verification
SELECT 'Database initialization completed successfully' as status;
