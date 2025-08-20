-- Complete database schema for Timatix Booking Services
-- This schema supports the full vehicle service management system

-- ============================================================================
-- CORE TABLES
-- ============================================================================

-- Users table (Clients, Mechanics, Admins)
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

-- ============================================================================
-- EXTENDED TABLES (Progress Tracking, Invoicing, Payments)
-- ============================================================================

-- Service progress table (Track service status updates)
CREATE TABLE IF NOT EXISTS service_progress (
    id BIGSERIAL PRIMARY KEY,
    service_request_id BIGINT NOT NULL REFERENCES service_requests(id) ON DELETE CASCADE,
    updated_by_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    phase VARCHAR(30) NOT NULL CHECK (
        phase IN ('RECEIVED', 'DIAGNOSIS', 'PARTS_ORDERED', 'REPAIR_IN_PROGRESS',
                 'QUALITY_CHECK', 'CLEANING', 'READY_FOR_COLLECTION')
    ),
    comment TEXT,
    photo_url TEXT,
    estimated_completion TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    service_request_id BIGINT NOT NULL UNIQUE REFERENCES service_requests(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    tax_amount DECIMAL(12,2),
    discount_amount DECIMAL(12,2),
    total_amount DECIMAL(12,2) NOT NULL,
    line_items_json TEXT,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' CHECK (
        payment_status IN ('UNPAID', 'PARTIAL', 'PAID', 'OVERDUE', 'CANCELLED')
    ),
    due_date TIMESTAMP,
    paid_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (
        status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED')
    ),
    gateway_reference VARCHAR(100),
    failure_reason TEXT,
    refund_reason TEXT,
    original_payment_id BIGINT REFERENCES payments(id) ON DELETE SET NULL,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- OPTIONAL EXTENDED TABLES (Future Features)
-- ============================================================================

-- Inventory table (Parts and supplies tracking)
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    part_name VARCHAR(255) NOT NULL,
    part_number VARCHAR(100) UNIQUE,
    description TEXT,
    category VARCHAR(100),
    current_stock INTEGER NOT NULL DEFAULT 0,
    minimum_stock INTEGER DEFAULT 0,
    reorder_level INTEGER DEFAULT 0,
    unit_cost DECIMAL(10,2),
    supplier VARCHAR(255),
    last_restocked TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Service history table (Detailed service records)
CREATE TABLE IF NOT EXISTS service_history (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    service_request_id BIGINT REFERENCES service_requests(id) ON DELETE SET NULL,
    service_date DATE NOT NULL,
    service_type VARCHAR(255) NOT NULL,
    description TEXT,
    mileage INTEGER,
    mechanic_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    cost DECIMAL(10,2),
    warranty_until DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customer communications table (Email/SMS logs)
CREATE TABLE IF NOT EXISTS communications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    service_request_id BIGINT REFERENCES service_requests(id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EMAIL', 'SMS', 'PUSH', 'CALL')),
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (
        status IN ('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'BOUNCED')
    ),
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- File attachments table (Photos, documents, etc.)
CREATE TABLE IF NOT EXISTS attachments (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL CHECK (
        entity_type IN ('SERVICE_REQUEST', 'VEHICLE', 'USER', 'INVOICE', 'PROGRESS')
    ),
    entity_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    description TEXT,
    uploaded_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- COMPREHENSIVE INDEXING
-- ============================================================================

-- Core table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

CREATE INDEX IF NOT EXISTS idx_vehicles_owner ON vehicles(owner_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_license_plate ON vehicles(license_plate);
CREATE INDEX IF NOT EXISTS idx_vehicles_vin ON vehicles(vin);
CREATE INDEX IF NOT EXISTS idx_vehicles_make_model ON vehicles(make, model);

CREATE INDEX IF NOT EXISTS idx_service_catalog_active ON service_catalog(is_active);
CREATE INDEX IF NOT EXISTS idx_service_catalog_name ON service_catalog(name);
CREATE INDEX IF NOT EXISTS idx_service_catalog_price ON service_catalog(base_price);

CREATE INDEX IF NOT EXISTS idx_booking_slots_date ON booking_slots(date);
CREATE INDEX IF NOT EXISTS idx_booking_slots_available ON booking_slots(is_available);
CREATE INDEX IF NOT EXISTS idx_booking_slots_date_time ON booking_slots(date, time_slot);

CREATE INDEX IF NOT EXISTS idx_service_requests_client ON service_requests(client_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_mechanic ON service_requests(assigned_mechanic_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_status ON service_requests(status);
CREATE INDEX IF NOT EXISTS idx_service_requests_date ON service_requests(preferred_date);
CREATE INDEX IF NOT EXISTS idx_service_requests_vehicle ON service_requests(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_service ON service_requests(service_id);

CREATE INDEX IF NOT EXISTS idx_service_quotes_request ON service_quotes(request_id);
CREATE INDEX IF NOT EXISTS idx_service_quotes_mechanic ON service_quotes(mechanic_id);
CREATE INDEX IF NOT EXISTS idx_service_quotes_status ON service_quotes(approval_status);
CREATE INDEX IF NOT EXISTS idx_service_quotes_valid_until ON service_quotes(valid_until);

-- Extended table indexes
CREATE INDEX IF NOT EXISTS idx_service_progress_request ON service_progress(service_request_id);
CREATE INDEX IF NOT EXISTS idx_service_progress_user ON service_progress(updated_by_user_id);
CREATE INDEX IF NOT EXISTS idx_service_progress_phase ON service_progress(phase);
CREATE INDEX IF NOT EXISTS idx_service_progress_created_at ON service_progress(created_at);

CREATE INDEX IF NOT EXISTS idx_invoices_request ON invoices(service_request_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(payment_status);
CREATE INDEX IF NOT EXISTS idx_invoices_due_date ON invoices(due_date);
CREATE INDEX IF NOT EXISTS idx_invoices_number ON invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoices_created_at ON invoices(created_at);

CREATE INDEX IF NOT EXISTS idx_payments_invoice ON payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payments_transaction ON payments(transaction_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_gateway_ref ON payments(gateway_reference);
CREATE INDEX IF NOT EXISTS idx_payments_method ON payments(payment_method);
CREATE INDEX IF NOT EXISTS idx_payments_processed_at ON payments(processed_at);

-- Optional table indexes
CREATE INDEX IF NOT EXISTS idx_inventory_part_number ON inventory(part_number);
CREATE INDEX IF NOT EXISTS idx_inventory_stock ON inventory(current_stock);
CREATE INDEX IF NOT EXISTS idx_inventory_category ON inventory(category);

CREATE INDEX IF NOT EXISTS idx_service_history_vehicle ON service_history(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_service_history_date ON service_history(service_date);
CREATE INDEX IF NOT EXISTS idx_service_history_mechanic ON service_history(mechanic_id);

CREATE INDEX IF NOT EXISTS idx_communications_user ON communications(user_id);
CREATE INDEX IF NOT EXISTS idx_communications_type ON communications(type);
CREATE INDEX IF NOT EXISTS idx_communications_status ON communications(status);
CREATE INDEX IF NOT EXISTS idx_communications_sent_at ON communications(sent_at);

CREATE INDEX IF NOT EXISTS idx_attachments_entity ON attachments(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_attachments_uploaded_by ON attachments(uploaded_by);

-- ============================================================================
-- FUNCTIONS AND TRIGGERS
-- ============================================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Function to generate invoice numbers
CREATE OR REPLACE FUNCTION generate_invoice_number()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.invoice_number IS NULL THEN
        NEW.invoice_number := 'TIM-' || TO_CHAR(CURRENT_DATE, 'YYYYMM') || '-' ||
                             LPAD((SELECT COUNT(*) + 1 FROM invoices WHERE
                             invoice_number LIKE 'TIM-' || TO_CHAR(CURRENT_DATE, 'YYYYMM') || '-%')::text, 4, '0');
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Function to update booking slot availability
CREATE OR REPLACE FUNCTION update_booking_slot_availability()
RETURNS TRIGGER AS $$
BEGIN
    NEW.is_available := (NEW.current_bookings < NEW.max_bookings);
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Function to validate service request status transitions
CREATE OR REPLACE FUNCTION validate_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    -- Allow any status change for new records
    IF OLD IS NULL THEN
        RETURN NEW;
    END IF;

    -- Prevent changing status from COMPLETED back to earlier stages
    IF OLD.status = 'COMPLETED' AND NEW.status NOT IN ('COMPLETED', 'CANCELLED') THEN
        RAISE EXCEPTION 'Cannot change status from COMPLETED to %', NEW.status;
    END IF;

    -- Prevent changing status from CANCELLED
    IF OLD.status = 'CANCELLED' AND NEW.status != 'CANCELLED' THEN
        RAISE EXCEPTION 'Cannot change status from CANCELLED';
    END IF;

    RETURN NEW;
END;
$$ language 'plpgsql';

-- ============================================================================
-- APPLY TRIGGERS
-- ============================================================================

-- Updated_at triggers for all relevant tables
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_vehicles_updated_at
    BEFORE UPDATE ON vehicles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_service_catalog_updated_at
    BEFORE UPDATE ON service_catalog
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_booking_slots_updated_at
    BEFORE UPDATE ON booking_slots
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_service_requests_updated_at
    BEFORE UPDATE ON service_requests
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_service_quotes_updated_at
    BEFORE UPDATE ON service_quotes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoices_updated_at
    BEFORE UPDATE ON invoices
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_inventory_updated_at
    BEFORE UPDATE ON inventory
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Business logic triggers
CREATE TRIGGER generate_invoice_number_trigger
    BEFORE INSERT ON invoices
    FOR EACH ROW EXECUTE FUNCTION generate_invoice_number();

CREATE TRIGGER update_booking_slot_availability_trigger
    BEFORE INSERT OR UPDATE ON booking_slots
    FOR EACH ROW EXECUTE FUNCTION update_booking_slot_availability();

CREATE TRIGGER validate_status_transition_trigger
    BEFORE UPDATE ON service_requests
    FOR EACH ROW EXECUTE FUNCTION validate_status_transition();

-- ============================================================================
-- VIEWS FOR COMMON QUERIES
-- ============================================================================

-- View for active service requests with client and vehicle details
CREATE OR REPLACE VIEW active_service_requests AS
SELECT
    sr.id,
    sr.status,
    sr.preferred_date,
    sr.preferred_time,
    sr.notes,
    sr.created_at,
    c.name as client_name,
    c.email as client_email,
    c.phone as client_phone,
    v.make || ' ' || v.model || ' (' || v.year || ')' as vehicle_info,
    v.license_plate,
    sc.name as service_name,
    sc.base_price,
    sc.estimated_duration_minutes,
    m.name as mechanic_name
FROM service_requests sr
JOIN users c ON sr.client_id = c.id
JOIN vehicles v ON sr.vehicle_id = v.id
JOIN service_catalog sc ON sr.service_id = sc.id
LEFT JOIN users m ON sr.assigned_mechanic_id = m.id
WHERE sr.status NOT IN ('COMPLETED', 'CANCELLED');

-- View for financial summary
CREATE OR REPLACE VIEW financial_summary AS
SELECT
    DATE_TRUNC('month', i.created_at) as month,
    COUNT(i.id) as total_invoices,
    SUM(i.total_amount) as total_invoiced,
    SUM(CASE WHEN i.payment_status = 'PAID' THEN i.total_amount ELSE 0 END) as total_paid,
    SUM(CASE WHEN i.payment_status IN ('UNPAID', 'OVERDUE') THEN i.total_amount ELSE 0 END) as total_outstanding
FROM invoices i
GROUP BY DATE_TRUNC('month', i.created_at)
ORDER BY month DESC;

-- View for mechanic performance
CREATE OR REPLACE VIEW mechanic_performance AS
SELECT
    m.id as mechanic_id,
    m.name as mechanic_name,
    COUNT(sr.id) as total_assigned,
    COUNT(CASE WHEN sr.status = 'COMPLETED' THEN 1 END) as completed_jobs,
    ROUND(
        COUNT(CASE WHEN sr.status = 'COMPLETED' THEN 1 END) * 100.0 /
        NULLIF(COUNT(sr.id), 0), 2
    ) as completion_rate,
    COUNT(sq.id) as quotes_created,
    COUNT(CASE WHEN sq.approval_status = 'ACCEPTED' THEN 1 END) as quotes_accepted
FROM users m
LEFT JOIN service_requests sr ON m.id = sr.assigned_mechanic_id
LEFT JOIN service_quotes sq ON m.id = sq.mechanic_id
WHERE m.role = 'MECHANIC'
GROUP BY m.id, m.name;

-- ============================================================================
-- SECURITY AND PERMISSIONS
-- ============================================================================

-- Row Level Security (RLS) policies can be added here for multi-tenant scenarios
-- Example for future implementation:
-- ALTER TABLE service_requests ENABLE ROW LEVEL SECURITY;
-- CREATE POLICY client_service_requests ON service_requests FOR ALL TO client_role USING (client_id = current_user_id());

-- ============================================================================
-- PERFORMANCE OPTIMIZATION
-- ============================================================================

-- Partial indexes for better performance on filtered queries
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_service_requests_active
    ON service_requests(client_id, status, created_at)
    WHERE status NOT IN ('COMPLETED', 'CANCELLED');

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_invoices_unpaid
    ON invoices(due_date, total_amount)
    WHERE payment_status IN ('UNPAID', 'OVERDUE');

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_payments_recent
    ON payments(created_at, status, amount)
    WHERE created_at > CURRENT_DATE - INTERVAL '90 days';

-- ============================================================================
-- SCHEMA COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE users IS 'System users: clients, mechanics, and administrators';
COMMENT ON TABLE vehicles IS 'Customer vehicles with registration and service history';
COMMENT ON TABLE service_catalog IS 'Available services with pricing and duration';
COMMENT ON TABLE booking_slots IS 'Available time slots for service appointments';
COMMENT ON TABLE service_requests IS 'Customer service requests and their status';
COMMENT ON TABLE service_quotes IS 'Mechanic quotes for service requests';
COMMENT ON TABLE service_progress IS 'Real-time progress updates for active services';
COMMENT ON TABLE invoices IS 'Generated invoices for completed services';
COMMENT ON TABLE payments IS 'Payment records and transaction history';
COMMENT ON TABLE inventory IS 'Parts and supplies inventory management';
COMMENT ON TABLE service_history IS 'Complete service history for vehicles';
COMMENT ON TABLE communications IS 'Communication logs (email, SMS, etc.)';
COMMENT ON TABLE attachments IS 'File attachments for various entities';

-- ============================================================================
-- INITIALIZATION COMPLETE
-- ============================================================================

-- Log schema creation
DO $$
BEGIN
    RAISE NOTICE 'Timatix Booking Services schema created successfully';
    RAISE NOTICE 'Schema includes: % tables, % indexes, % triggers, % views',
        (SELECT count(*) FROM information_schema.tables WHERE table_schema = current_schema()),
        (SELECT count(*) FROM pg_indexes WHERE schemaname = current_schema()),
        (SELECT count(*) FROM information_schema.triggers WHERE trigger_schema = current_schema()),
        (SELECT count(*) FROM information_schema.views WHERE table_schema = current_schema());
END $$;