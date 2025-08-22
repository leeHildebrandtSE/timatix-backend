-- ============================================================================
-- DATABASE VIEWS FOR TIMATIX BOOKING SERVICES
-- Run this after the main schema.sql is successfully applied
-- ============================================================================

-- Service request summary view
CREATE OR REPLACE VIEW service_request_summary AS
SELECT
    sr.id,
    sr.status,
    c.user_id,
    u.first_name || ' ' || u.last_name as customer_name,
    v.make || ' ' || v.model as vehicle_info,
    st.name as service_type,
    sr.total_cost,
    sr.created_at,
    sr.estimated_completion
FROM service_requests sr
JOIN customers c ON sr.customer_id = c.id
JOIN users u ON c.user_id = u.id
JOIN vehicles v ON sr.vehicle_id = v.id
LEFT JOIN service_types st ON sr.service_type_id = st.id;

-- Customer details view
CREATE OR REPLACE VIEW customer_details AS
SELECT
    c.id,
    u.username,
    u.email,
    u.phone,
    u.first_name,
    u.last_name,
    c.company_name,
    c.address,
    c.city,
    c.postal_code,
    c.country,
    c.preferred_contact_method,
    c.created_at
FROM customers c
JOIN users u ON c.user_id = u.id
WHERE u.is_active = true;

-- Vehicle overview view
CREATE OR REPLACE VIEW vehicle_overview AS
SELECT
    v.id,
    v.make,
    v.model,
    v.year,
    v.license_plate,
    v.vin,
    v.color,
    v.mileage,
    u.first_name || ' ' || u.last_name as owner_name,
    u.email as owner_email,
    v.created_at
FROM vehicles v
JOIN customers c ON v.customer_id = c.id
JOIN users u ON c.user_id = u.id;

-- Invoice summary view
CREATE OR REPLACE VIEW invoice_summary AS
SELECT
    i.id,
    i.invoice_number,
    i.subtotal,
    i.tax_amount,
    i.total_amount,
    i.due_date,
    i.payment_terms,
    sr.id as service_request_id,
    u.first_name || ' ' || u.last_name as customer_name,
    u.email as customer_email,
    COALESCE(SUM(p.amount), 0) as total_paid,
    i.total_amount - COALESCE(SUM(p.amount), 0) as amount_due,
    CASE
        WHEN COALESCE(SUM(p.amount), 0) >= i.total_amount THEN 'PAID'
        WHEN COALESCE(SUM(p.amount), 0) > 0 THEN 'PARTIALLY_PAID'
        ELSE 'UNPAID'
    END as payment_status,
    i.created_at
FROM invoices i
JOIN service_requests sr ON i.service_request_id = sr.id
JOIN customers c ON sr.customer_id = c.id
JOIN users u ON c.user_id = u.id
LEFT JOIN payments p ON i.id = p.invoice_id AND p.status = 'COMPLETED'
GROUP BY i.id, i.invoice_number, i.subtotal, i.tax_amount, i.total_amount,
         i.due_date, i.payment_terms, sr.id, u.first_name, u.last_name,
         u.email, i.created_at;