-- ============================================================================
-- COMPLETE DATABASE SCHEMA FOR TIMATIX BOOKING SERVICES
-- Correct execution order: EXTENSIONS → ENUMS → TABLES → FUNCTIONS → TRIGGERS → DATA
-- Views are moved to separate script to avoid transaction issues
-- ============================================================================

-- 1. EXTENSIONS (if needed)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. ENUMS AND CUSTOM TYPES (PostgreSQL doesn't support IF NOT EXISTS for types)
DROP TYPE IF EXISTS user_role CASCADE;
CREATE TYPE user_role AS ENUM ('ADMIN', 'CUSTOMER', 'TECHNICIAN');

DROP TYPE IF EXISTS service_status CASCADE;
CREATE TYPE service_status AS ENUM ('PENDING', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');

DROP TYPE IF EXISTS payment_status CASCADE;
CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED');

DROP TYPE IF EXISTS notification_type CASCADE;
CREATE TYPE notification_type AS ENUM ('EMAIL', 'SMS', 'PUSH');

-- 3. ALL TABLES FIRST (in dependency order)
-- Users table (referenced by others)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role user_role NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    postal_code VARCHAR(10),
    country VARCHAR(50) DEFAULT 'South Africa',
    preferred_contact_method notification_type DEFAULT 'EMAIL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 1),
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    vin VARCHAR(17) UNIQUE,
    color VARCHAR(30),
    mileage INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Service types table
CREATE TABLE IF NOT EXISTS service_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    estimated_duration_minutes INTEGER NOT NULL DEFAULT 60,
    base_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Booking slots table
CREATE TABLE IF NOT EXISTS booking_slots (
    id BIGSERIAL PRIMARY KEY,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_bookings INTEGER NOT NULL DEFAULT 1,
    current_bookings INTEGER DEFAULT 0,
    is_available BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(slot_date, start_time),
    CHECK (start_time < end_time),
    CHECK (current_bookings >= 0),
    CHECK (current_bookings <= max_bookings)
);

-- Service requests table
CREATE TABLE IF NOT EXISTS service_requests (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    vehicle_id BIGINT REFERENCES vehicles(id) ON DELETE CASCADE,
    service_type_id BIGINT REFERENCES service_types(id),
    booking_slot_id BIGINT REFERENCES booking_slots(id),
    status service_status DEFAULT 'PENDING',
    description TEXT,
    special_instructions TEXT,
    estimated_completion TIMESTAMP,
    actual_completion TIMESTAMP,
    technician_notes TEXT,
    customer_rating INTEGER CHECK (customer_rating >= 1 AND customer_rating <= 5),
    customer_feedback TEXT,
    total_cost DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    service_request_id BIGINT REFERENCES service_requests(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) UNIQUE,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    due_date DATE,
    payment_terms VARCHAR(50) DEFAULT 'Net 30',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT REFERENCES invoices(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100),
    status payment_status DEFAULT 'PENDING',
    payment_date TIMESTAMP,
    reference_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    service_request_id BIGINT REFERENCES service_requests(id) ON DELETE CASCADE,
    type notification_type NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT false,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Activity logs table
CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    service_request_id BIGINT REFERENCES service_requests(id) ON DELETE CASCADE,
    action VARCHAR(100) NOT NULL,
    description TEXT,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. FUNCTIONS (after all tables exist)
CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS 'BEGIN NEW.updated_at = CURRENT_TIMESTAMP; RETURN NEW; END;' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_invoice_number() RETURNS TRIGGER AS 'BEGIN IF NEW.invoice_number IS NULL THEN NEW.invoice_number := ''TIM-'' || TO_CHAR(CURRENT_DATE, ''YYYYMM'') || ''-'' || LPAD((SELECT COUNT(*) + 1 FROM invoices WHERE invoice_number LIKE ''TIM-'' || TO_CHAR(CURRENT_DATE, ''YYYYMM'') || ''-%'')::text, 4, ''0''); END IF; RETURN NEW; END;' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_booking_slot_availability() RETURNS TRIGGER AS 'BEGIN NEW.is_available := (NEW.current_bookings < NEW.max_bookings); RETURN NEW; END;' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION validate_status_transition() RETURNS TRIGGER AS 'BEGIN IF OLD IS NULL THEN RETURN NEW; END IF; IF OLD.status = ''COMPLETED'' AND NEW.status NOT IN (''COMPLETED'', ''CANCELLED'') THEN RAISE EXCEPTION ''Cannot change status from COMPLETED to %'', NEW.status; END IF; IF OLD.status = ''CANCELLED'' AND NEW.status != ''CANCELLED'' THEN RAISE EXCEPTION ''Cannot change status from CANCELLED''; END IF; RETURN NEW; END;' LANGUAGE plpgsql;

-- 5. TRIGGERS (after all tables and functions exist)
-- Updated_at triggers
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_customers_updated_at ON customers;
CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_vehicles_updated_at ON vehicles;
CREATE TRIGGER update_vehicles_updated_at BEFORE UPDATE ON vehicles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_service_requests_updated_at ON service_requests;
CREATE TRIGGER update_service_requests_updated_at BEFORE UPDATE ON service_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_invoices_updated_at ON invoices;
CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE ON invoices FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_payments_updated_at ON payments;
CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_booking_slots_updated_at ON booking_slots;
CREATE TRIGGER update_booking_slots_updated_at BEFORE UPDATE ON booking_slots FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_notifications_updated_at ON notifications;
CREATE TRIGGER update_notifications_updated_at BEFORE UPDATE ON notifications FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Business logic triggers
DROP TRIGGER IF EXISTS generate_invoice_number_trigger ON invoices;
CREATE TRIGGER generate_invoice_number_trigger BEFORE INSERT ON invoices FOR EACH ROW EXECUTE FUNCTION generate_invoice_number();

DROP TRIGGER IF EXISTS update_availability_trigger ON booking_slots;
CREATE TRIGGER update_availability_trigger BEFORE INSERT OR UPDATE ON booking_slots FOR EACH ROW EXECUTE FUNCTION update_booking_slot_availability();

DROP TRIGGER IF EXISTS validate_status_transition_trigger ON service_requests;
CREATE TRIGGER validate_status_transition_trigger BEFORE UPDATE ON service_requests FOR EACH ROW EXECUTE FUNCTION validate_status_transition();

-- 6. Insert default service types
INSERT INTO service_types (name, description, estimated_duration_minutes, base_price) VALUES
('Basic Service', 'Oil change, filter replacement, basic inspection', 60, 150.00),
('Full Service', 'Comprehensive vehicle inspection and maintenance', 120, 300.00),
('Brake Service', 'Brake pad replacement and brake system inspection', 90, 250.00),
('Tire Service', 'Tire rotation, balancing, and pressure check', 45, 100.00),
('Engine Diagnostic', 'Computer diagnostic and engine performance check', 30, 120.00)
ON CONFLICT DO NOTHING;