-- Complete sample data for Timatix Booking Services
-- This file populates the database with comprehensive test data

-- ============================================================================
-- USERS (Clients, Mechanics, and Admins)
-- ============================================================================

INSERT INTO users (name, email, password, phone, address, role, created_at, updated_at) VALUES
-- Admin users
('John Admin', 'admin@timatix.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456789', '123 Main St, Cape Town', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sarah Manager', 'manager@timatix.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456788', '456 Admin Ave, Cape Town', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Mechanic users
('Mike Mechanic', 'mike@timatix.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456790', '456 Workshop Ave, Cape Town', 'MECHANIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sarah Johnson', 'sarah@timatix.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456791', '789 Repair Rd, Cape Town', 'MECHANIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('David Wilson', 'david.mechanic@timatix.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456792', '321 Service St, Cape Town', 'MECHANIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Lisa Thompson', 'lisa@timatix.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456793', '654 Auto Ln, Cape Town', 'MECHANIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Client users
('John Doe', 'john.doe@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456794', '321 Client St, Cape Town', 'CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Emma Brown', 'emma.brown@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456795', '654 Customer Ave, Cape Town', 'CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alex Taylor', 'alex.taylor@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456796', '987 User Blvd, Cape Town', 'CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Michael Smith', 'michael.smith@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456797', '147 Patron Pl, Cape Town', 'CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jessica Wilson', 'jessica.wilson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456798', '258 Buyer Blvd, Cape Town', 'CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Robert Davis', 'robert.davis@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+27123456799', '369 Client Circle, Cape Town', 'CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- SERVICE CATALOG (Available Services)
-- ============================================================================

INSERT INTO service_catalog (name, description, base_price, estimated_duration_minutes, is_active, created_at, updated_at) VALUES
-- Basic Services
('Oil Change', 'Standard oil and filter change service with quality oil', 450.00, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Basic Service', 'Basic vehicle inspection and minor maintenance', 350.00, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Air Filter Replacement', 'Replace engine air filter for better performance', 120.00, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Battery Test & Replacement', 'Battery load test and replacement if needed', 180.00, 20, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Brake Services
('Brake Service', 'Complete brake inspection and service', 1200.00, 90, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Brake Pad Replacement', 'Replace worn brake pads', 800.00, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Brake Fluid Change', 'Replace brake fluid for optimal braking', 250.00, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Engine Services
('Engine Diagnostic', 'Computer diagnostic scan of engine systems', 350.00, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Spark Plug Replacement', 'Replace spark plugs and ignition components', 320.00, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Coolant Flush', 'Complete cooling system flush and refill', 400.00, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Timing Belt Replacement', 'Replace timing belt and related components', 1800.00, 180, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Transmission & Drivetrain
('Transmission Service', 'Transmission fluid change and inspection', 800.00, 90, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Clutch Repair', 'Clutch inspection and replacement', 2500.00, 240, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Differential Service', 'Differential oil change and inspection', 350.00, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Tire & Suspension
('Tire Rotation', 'Rotate tires for even wear patterns', 250.00, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Wheel Alignment', '4-wheel alignment service', 650.00, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tire Replacement', 'Replace worn or damaged tires', 1200.00, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Suspension Check', 'Complete suspension system inspection', 300.00, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Electrical & AC
('AC Service', 'Air conditioning system service and regas', 550.00, 75, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Electrical Diagnostic', 'Diagnose electrical system issues', 400.00, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alternator Replacement', 'Replace faulty alternator', 1500.00, 120, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Comprehensive Services
('Full Service', 'Comprehensive vehicle service and inspection', 850.00, 150, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pre-Purchase Inspection', 'Thorough inspection for vehicle purchase', 500.00, 90, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Roadworthy Certificate', 'Official roadworthy certificate inspection', 450.00, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- ============================================================================
-- VEHICLES (Customer Vehicles)
-- ============================================================================

INSERT INTO vehicles (make, model, year, license_plate, vin, color, owner_id, created_at, updated_at) VALUES
-- John Doe's vehicles
('Toyota', 'Camry', '2020', 'CA123GP', '1HGBH41JXMN109186', 'Silver', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Honda', 'Civic', '2019', 'CA456GP', '2HGBH41JXMN109187', 'Blue', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Emma Brown's vehicles
('Ford', 'Focus', '2021', 'CA789GP', '3HGBH41JXMN109188', 'Red', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BMW', '320i', '2022', 'CA321GP', '4HGBH41JXMN109189', 'Black', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Alex Taylor's vehicles
('Volkswagen', 'Golf', '2020', 'CA654GP', '5HGBH41JXMN109190', 'White', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Audi', 'A3', '2021', 'CA987GP', '6HGBH41JXMN109191', 'Grey', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Michael Smith's vehicles
('Nissan', 'Sentra', '2019', 'CA147GP', '7HGBH41JXMN109192', 'Blue', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mazda', 'CX-5', '2022', 'CA258GP', '8HGBH41JXMN109193', 'Silver', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Jessica Wilson's vehicles
('Hyundai', 'Elantra', '2020', 'CA369GP', '9HGBH41JXMN109194', 'White', 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Robert Davis's vehicles
('Kia', 'Cerato', '2021', 'CA741GP', 'AHGBH41JXMN109195', 'Black', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Subaru', 'Impreza', '2018', 'CA852GP', 'BHGBH41JXMN109196', 'Blue', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (license_plate) DO NOTHING;

-- ============================================================================
-- BOOKING SLOTS (Available Time Slots)
-- ============================================================================

-- Generate booking slots for the next 60 days (weekdays only)
INSERT INTO booking_slots (date, time_slot, max_bookings, current_bookings, is_available, created_at, updated_at)
SELECT
    date_series::date,
    time_slot,
    CASE
        WHEN time_slot IN ('08:00:00'::time, '09:00:00'::time, '13:00:00'::time, '14:00:00'::time) THEN 3
        ELSE 2
    END as max_bookings,
    CASE
        WHEN date_series::date = CURRENT_DATE + INTERVAL '1 day' AND time_slot = '09:00:00'::time THEN 1
        WHEN date_series::date = CURRENT_DATE + INTERVAL '3 days' AND time_slot = '14:00:00'::time THEN 1
        ELSE 0
    END as current_bookings,
    CASE
        WHEN date_series::date < CURRENT_DATE THEN false
        WHEN date_series::date = CURRENT_DATE + INTERVAL '1 day' AND time_slot = '09:00:00'::time THEN true
        WHEN date_series::date = CURRENT_DATE + INTERVAL '3 days' AND time_slot = '14:00:00'::time THEN true
        ELSE true
    END as is_available,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', '1 day') AS date_series,
    unnest(ARRAY[
        '08:00:00'::time, '09:00:00'::time, '10:00:00'::time, '11:00:00'::time,
        '13:00:00'::time, '14:00:00'::time, '15:00:00'::time, '16:00:00'::time, '17:00:00'::time
    ]) AS time_slot
WHERE EXTRACT(dow FROM date_series) BETWEEN 1 AND 5  -- Monday to Friday only
ON CONFLICT (date, time_slot) DO NOTHING;

-- ============================================================================
-- SERVICE REQUESTS (Customer Service Requests)
-- ============================================================================

INSERT INTO service_requests (client_id, vehicle_id, service_id, assigned_mechanic_id, preferred_date, preferred_time, notes, status, created_at, updated_at) VALUES
-- Recent completed services
(7, 1, 1, 3, CURRENT_DATE - INTERVAL '5 days', '09:00:00', 'Regular oil change service', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(8, 3, 5, 4, CURRENT_DATE - INTERVAL '10 days', '14:00:00', 'Brakes feel spongy, need inspection', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(9, 5, 8, 3, CURRENT_DATE - INTERVAL '15 days', '10:00:00', 'Check engine light is on', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),

-- Current active requests
(7, 2, 4, 4, CURRENT_DATE + INTERVAL '2 days', '11:00:00', 'Battery seems weak, please test', 'QUOTE_APPROVED', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(8, 4, 16, 5, CURRENT_DATE + INTERVAL '5 days', '15:00:00', 'Steering wheel vibrates at high speed', 'QUOTE_SENT', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(10, 7, 5, 3, CURRENT_DATE + INTERVAL '7 days', '09:00:00', 'Brake pedal feels soft', 'PENDING_QUOTE', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours'),

-- Future requests
(11, 9, 20, NULL, CURRENT_DATE + INTERVAL '10 days', '13:00:00', 'Annual full service', 'PENDING_QUOTE', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(12, 10, 12, NULL, CURRENT_DATE + INTERVAL '14 days', '10:00:00', 'Transmission making noise', 'PENDING_QUOTE', CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(9, 6, 18, 4, CURRENT_DATE + INTERVAL '20 days', '14:00:00', 'AC not cooling properly', 'PENDING_QUOTE', CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),

-- Historical requests for testing
(7, 1, 20, 3, CURRENT_DATE - INTERVAL '90 days', '10:00:00', 'Full service', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '90 days', CURRENT_TIMESTAMP - INTERVAL '87 days'),
(8, 3, 15, 4, CURRENT_DATE - INTERVAL '60 days', '14:00:00', 'Tire replacement needed', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '58 days')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- SERVICE QUOTES (Mechanic Quotes for Requests)
-- ============================================================================

INSERT INTO service_quotes (request_id, mechanic_id, labour_cost, parts_cost, total_amount, notes, approval_status, valid_until, created_at, updated_at, approved_at) VALUES
-- Approved quotes
(4, 4, 120.00, 80.00, 200.00, 'Battery replacement with 2-year warranty', 'ACCEPTED', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),

-- Pending quotes
(5, 5, 400.00, 250.00, 650.00, 'Wheel alignment and balancing required', 'PENDING', CURRENT_TIMESTAMP + INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour', NULL),

-- Historical quotes
(1, 3, 300.00, 150.00, 450.00, 'Standard oil change with premium filter', 'ACCEPTED', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 4, 800.00, 400.00, 1200.00, 'Brake pads and rotors replacement', 'ACCEPTED', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '9 days'),
(3, 3, 250.00, 100.00, 350.00, 'Engine diagnostic completed', 'ACCEPTED', CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '14 days')
ON CONFLICT (request_id) DO NOTHING;

-- ============================================================================
-- INVOICES (Generated Invoices)
-- ============================================================================

INSERT INTO invoices (service_request_id, invoice_number, subtotal, tax_amount, discount_amount, total_amount, line_items_json, payment_status, due_date, paid_date, created_at, updated_at) VALUES
-- Paid invoices
(1, 'TIM-202408-0001', 450.00, 67.50, 0.00, 517.50,
'[{"description":"Oil Change Service","quantity":1,"unitPrice":300.00,"total":300.00},{"description":"Premium Oil Filter","quantity":1,"unitPrice":150.00,"total":150.00}]',
'PAID', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),

(2, 'TIM-202408-0002', 1200.00, 180.00, 0.00, 1380.00,
'[{"description":"Brake Service","quantity":1,"unitPrice":800.00,"total":800.00},{"description":"Brake Pads & Rotors","quantity":1,"unitPrice":400.00,"total":400.00}]',
'PAID', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),

(3, 'TIM-202408-0003', 350.00, 52.50, 0.00, 402.50,
'[{"description":"Engine Diagnostic","quantity":1,"unitPrice":250.00,"total":250.00},{"description":"Diagnostic Tools","quantity":1,"unitPrice":100.00,"total":100.00}]',
'PAID', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),

-- Pending invoice
(4, 'TIM-202408-0004', 200.00, 30.00, 0.00, 230.00,
'[{"description":"Battery Test & Replacement","quantity":1,"unitPrice":120.00,"total":120.00},{"description":"Premium Battery","quantity":1,"unitPrice":80.00,"total":80.00}]',
'UNPAID', CURRENT_TIMESTAMP + INTERVAL '28 days', NULL, CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours')
ON CONFLICT (service_request_id) DO NOTHING;

-- ============================================================================
-- PAYMENTS (Payment Records)
-- ============================================================================

INSERT INTO payments (invoice_id, transaction_id, amount, payment_method, status, gateway_reference, failure_reason, refund_reason, original_payment_id, processed_at, created_at, updated_at) VALUES
-- Successful payments
(1, 'TIM_1724155200_abc12345', 517.50, 'Credit Card', 'COMPLETED', 'TXN_gateway123456', NULL, NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(2, 'TIM_1724241600_def67890', 1380.00, 'Bank Transfer', 'COMPLETED', 'TXN_gateway789012', NULL, NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(3, 'TIM_1724328000_ghi34567', 402.50, 'Credit Card', 'COMPLETED', 'TXN_gateway345678', NULL, NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),

-- Failed payment example
(4, 'TIM_1724414400_jkl90123', 230.00, 'Credit Card', 'FAILED', NULL, 'Insufficient funds', NULL, NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour'),

-- Pending payment example
(4, 'TIM_1724414500_mno45678', 230.00, 'Bank Transfer', 'PENDING', 'TXN_pending901234', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '30 minutes')
ON CONFLICT (transaction_id) DO NOTHING;

-- ============================================================================
-- SERVICE PROGRESS (Service Status Updates)
-- ============================================================================

INSERT INTO service_progress (service_request_id, updated_by_user_id, phase, comment, photo_url, estimated_completion, created_at) VALUES
-- Completed service progress
(1, 3, 'RECEIVED', 'Vehicle received and checked in', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '5 days 8 hours'),
(1, 3, 'DIAGNOSIS', 'Vehicle inspection completed, oil change required', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '5 days 7 hours'),
(1, 3, 'REPAIR_IN_PROGRESS', 'Oil change in progress', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '5 days 6 hours'),
(1, 3, 'QUALITY_CHECK', 'Service completed, quality check passed', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '5 days 5 hours'),
(1, 3, 'READY_FOR_COLLECTION', 'Vehicle ready for collection', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '5 days 4 hours'),

(2, 4, 'RECEIVED', 'Vehicle received for brake service', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '10 days 8 hours'),
(2, 4, 'DIAGNOSIS', 'Brake inspection completed, pads and rotors need replacement', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '10 days 6 hours'),
(2, 4, 'PARTS_ORDERED', 'Brake parts ordered, expected delivery tomorrow', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '10 days 4 hours'),
(2, 4, 'REPAIR_IN_PROGRESS', 'Brake service in progress', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '9 days 8 hours'),
(2, 4, 'QUALITY_CHECK', 'Brake service completed, road test successful', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '9 days 6 hours'),
(2, 4, 'READY_FOR_COLLECTION', 'Vehicle ready for collection', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '9 days 4 hours'),

(3, 3, 'RECEIVED', 'Vehicle received for engine diagnostic', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '15 days 6 hours'),
(3, 3, 'DIAGNOSIS', 'Engine diagnostic completed, issue identified', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '15 days 4 hours'),
(3, 3, 'REPAIR_IN_PROGRESS', 'Repair completed', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '15 days 2 hours'),
(3, 3, 'READY_FOR_COLLECTION', 'Vehicle ready for collection', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '15 days 1 hour'),

-- Current service in progress
(4, 4, 'RECEIVED', 'Vehicle received for battery service', NULL, CURRENT_TIMESTAMP + INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(4, 4, 'DIAGNOSIS', 'Battery tested, replacement required', NULL, CURRENT_TIMESTAMP + INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- UPDATE SEQUENCES (Ensure proper ID generation)
-- ============================================================================

-- Update sequences to prevent conflicts
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('vehicles_id_seq', (SELECT MAX(id) FROM vehicles));
SELECT setval('service_catalog_id_seq', (SELECT MAX(id) FROM service_catalog));
SELECT setval('booking_slots_id_seq', (SELECT MAX(id) FROM booking_slots));
SELECT setval('service_requests_id_seq', (SELECT MAX(id) FROM service_requests));
SELECT setval('service_quotes_id_seq', (SELECT MAX(id) FROM service_quotes));
SELECT setval('invoices_id_seq', (SELECT MAX(id) FROM invoices));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
SELECT setval('service_progress_id_seq', (SELECT MAX(id) FROM service_progress));

-- ============================================================================
-- VERIFICATION QUERIES (Optional - for testing)
-- ============================================================================

-- These can be uncommented for verification during development

-- SELECT 'Users created' as info, role, COUNT(*) as count FROM users GROUP BY role;
-- SELECT 'Services created' as info, COUNT(*) as count FROM service_catalog WHERE is_active = true;
-- SELECT 'Vehicles created' as info, COUNT(*) as count FROM vehicles;
-- SELECT 'Booking slots created' as info, COUNT(*) as count FROM booking_slots WHERE date >= CURRENT_DATE;
-- SELECT 'Service requests created' as info, status, COUNT(*) as count FROM service_requests GROUP BY status;
-- SELECT 'Quotes created' as info, approval_status, COUNT(*) as count FROM service_quotes GROUP BY approval_status;
-- SELECT 'Invoices created' as info, payment_status, COUNT(*) as count FROM invoices GROUP BY payment_status;
-- SELECT 'Payments created' as info, status, COUNT(*) as count FROM payments GROUP BY status;