-- ============================================================================
-- DATABASE INDEXES FOR TIMATIX BOOKING SERVICES
-- Run this after the main schema.sql is successfully applied
-- These indexes improve query performance
-- ============================================================================

-- Foreign key indexes for better join performance
CREATE INDEX IF NOT EXISTS idx_customers_user_id ON customers(user_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_customer_id ON vehicles(customer_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_customer_id ON service_requests(customer_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_vehicle_id ON service_requests(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_service_type_id ON service_requests(service_type_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_booking_slot_id ON service_requests(booking_slot_id);
CREATE INDEX IF NOT EXISTS idx_invoices_service_request_id ON invoices(service_request_id);
CREATE INDEX IF NOT EXISTS idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_service_request_id ON notifications(service_request_id);
CREATE INDEX IF NOT EXISTS idx_activity_logs_user_id ON activity_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_activity_logs_service_request_id ON activity_logs(service_request_id);

-- Status and search indexes
CREATE INDEX IF NOT EXISTS idx_service_requests_status ON service_requests(status);
CREATE INDEX IF NOT EXISTS idx_service_requests_created_at ON service_requests(created_at);
CREATE INDEX IF NOT EXISTS idx_booking_slots_date ON booking_slots(slot_date);
CREATE INDEX IF NOT EXISTS idx_booking_slots_available ON booking_slots(is_available);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_vehicles_license_plate ON vehicles(license_plate);
CREATE INDEX IF NOT EXISTS idx_vehicles_vin ON vehicles(vin);
CREATE INDEX IF NOT EXISTS idx_invoices_invoice_number ON invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(is_read);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_service_requests_customer_status ON service_requests(customer_id, status);
CREATE INDEX IF NOT EXISTS idx_service_requests_date_status ON service_requests(created_at, status);
CREATE INDEX IF NOT EXISTS idx_booking_slots_date_available ON booking_slots(slot_date, is_available);
CREATE INDEX IF NOT EXISTS idx_notifications_user_read ON notifications(user_id, is_read);

-- Partial indexes for specific conditions
CREATE INDEX IF NOT EXISTS idx_users_active_customers ON users(id) WHERE role = 'CUSTOMER' AND is_active = true;
CREATE INDEX IF NOT EXISTS idx_service_requests_pending ON service_requests(created_at) WHERE status = 'PENDING';
CREATE INDEX IF NOT EXISTS idx_invoices_unpaid ON invoices(due_date) WHERE id NOT IN (
    SELECT DISTINCT invoice_id FROM payments WHERE status = 'COMPLETED'
);