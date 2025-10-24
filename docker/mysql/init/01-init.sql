-- Database initialization script for OAuth Opaque Token application

-- Create database if not exists (already created by docker-compose environment variable)
-- CREATE DATABASE IF NOT EXISTS oauth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE oauth_db;

-- Grant privileges to oauth_user
GRANT ALL PRIVILEGES ON oauth_db.* TO 'oauth_user'@'%';
FLUSH PRIVILEGES;

-- Set timezone
SET time_zone = '+09:00';

-- Log initialization
SELECT 'Database initialized successfully' AS status;
