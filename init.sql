-- Create UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create Tenant table
CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    domain_name VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, email)
);

-- Create Notification Templates table
CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    event_type VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, event_type, channel)
);

-- Create Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID NOT NULL REFERENCES users(id),
    event_id VARCHAR(255),
    event_type VARCHAR(100),
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create User Preferences table
CREATE TABLE IF NOT EXISTS user_preferences (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID NOT NULL REFERENCES users(id),
    channel VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    frequency VARCHAR(50) NOT NULL DEFAULT 'IMMEDIATE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, user_id, channel)
);

-- Create indexes
CREATE INDEX idx_notifications_tenant_user ON notifications(tenant_id, user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_templates_tenant ON notification_templates(tenant_id);
CREATE INDEX idx_preferences_tenant_user ON user_preferences(tenant_id, user_id);

-- Insert sample tenant
INSERT INTO tenants (id, name, domain_name) VALUES 
('550e8400-e29b-41d4-a716-446655440001', 'StormGate Enterprise', 'stormgate.io')
ON CONFLICT DO NOTHING;

-- Insert sample users
INSERT INTO users (id, tenant_id, email, name) VALUES 
('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'user1@example.com', 'John Doe'),
('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'user2@example.com', 'Jane Smith')
ON CONFLICT DO NOTHING;
