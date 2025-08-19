-- Create database schema for Timatix Booking Services

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CLIENT', 'MECHANIC', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year VARCHAR(4) NOT NULL,
    license_plate VARCHAR(20) UNIQUE,
    vin VARCHAR(100) UNIQUE,
    color VARCHAR(50),
    photo_url TEXT,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Service catalog table
CREATE TABLE IF NOT EXISTS service_catalog (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2),
    estimated_duration_minutes INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Booking slots table
CREATE TABLE IF NOT EXISTS booking_slots (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    time_slot TIME NOT NULL,
    max_bookings INTEGER NOT NULL DEFAULT 1,
    current_bookings INTEGER NOT NULL DEFAULT 0,
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(date, time_slot)
);

-- Service requests table
CREATE TABLE IF NOT EXISTS service_requests (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    service_id BIGINT NOT NULL REFERENCES service_catalog(id) ON DELETE RESTRICT,
    assigned_mechanic_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    preferred_date DATE,
    preferred_time TIME,
    notes TEXT,
    photo_url TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_QUOTE' CHECK (
        status IN ('PENDING_QUOTE', 'QUOTE_SENT', 'QUOTE_APPROVED', 'QUOTE_DECLINED',
                  'BOOKING_CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')
    ),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Service quotes table
CREATE TABLE IF NOT EXISTS service_quotes (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE REFERENCES service_requests(id) ON DELETE CASCADE,
    mechanic_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    line_items_json TEXT,
    labour_cost DECIMAL(10,2),
    parts_cost DECIMAL(10,2),
    total_amount DECIMAL(10,2) NOT NULL,
    notes TEXT,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (
        approval_status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED')
    ),
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_vehicles_owner ON vehicles(owner_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_license_plate ON vehicles(license_plate);
CREATE INDEX IF NOT EXISTS idx_service_catalog_active ON service_catalog(is_active);
CREATE INDEX IF NOT EXISTS idx_booking_slots_date ON booking_slots(date);
CREATE INDEX IF NOT EXISTS idx_booking_slots_available ON booking_slots(is_available);
CREATE INDEX IF NOT EXISTS idx_service_requests_client ON service_requests(client_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_mechanic ON service_requests(assigned_mechanic_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_status ON service_requests(status);
CREATE INDEX IF NOT EXISTS idx_service_quotes_request ON service_quotes(request_id);
CREATE INDEX IF NOT EXISTS idx_service_quotes_mechanic ON service_quotes(mechanic_id);
CREATE INDEX IF NOT EXISTS idx_service_quotes_status ON service_quotes(approval_status);

-- Trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_vehicles_updated_at BEFORE UPDATE ON vehicles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_service_catalog_updated_at BEFORE UPDATE ON service_catalog FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_booking_slots_updated_at BEFORE UPDATE ON booking_slots FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_service_requests_updated_at BEFORE UPDATE ON service_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_service_quotes_updated_at BEFORE UPDATE ON service_quotes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();