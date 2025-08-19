-- Insert sample data for Timatix Booking Services

-- Sample users
INSERT INTO users (name, email, password, phone, address, role) VALUES
('John Admin', 'admin@timatix.com', 'admin123', '+27123456789', '123 Main St, Cape Town', 'ADMIN'),
('Mike Mechanic', 'mike@timatix.com', 'mechanic123', '+27123456790', '456 Workshop Ave, Cape Town', 'MECHANIC'),
('Sarah Mechanic', 'sarah@timatix.com', 'mechanic123', '+27123456791', '789 Repair Rd, Cape Town', 'MECHANIC'),
('David Client', 'david@email.com', 'client123', '+27123456792', '321 Client St, Cape Town', 'CLIENT'),
('Emma Client', 'emma@email.com', 'client123', '+27123456793', '654 Customer Ave, Cape Town', 'CLIENT'),
('Alex Client', 'alex@email.com', 'client123', '+27123456794', '987 User Blvd, Cape Town', 'CLIENT')
ON CONFLICT (email) DO NOTHING;

-- Sample service catalog
INSERT INTO service_catalog (name, description, base_price, estimated_duration_minutes, is_active) VALUES
('Oil Change', 'Standard oil and filter change service', 450.00, 30, true),
('Brake Service', 'Complete brake inspection and service', 1200.00, 60, true),
('Tire Rotation', 'Rotate tires for even wear', 250.00, 20, true),
('Engine Diagnostic', 'Computer diagnostic scan of engine systems', 350.00, 45, true),
('Battery Test', 'Battery load test and replacement if needed', 180.00, 15, true),
('Transmission Service', 'Transmission fluid change and inspection', 800.00, 90, true),
('Air Filter Replacement', 'Replace engine air filter', 120.00, 10, true),
('Wheel Alignment', '4-wheel alignment service', 650.00, 45, true),
('Coolant Flush', 'Complete cooling system flush and refill', 400.00, 40, true),
('Spark Plug Replacement', 'Replace spark plugs and ignition components', 320.00, 30, true)
ON CONFLICT DO NOTHING;

-- Sample vehicles (using user IDs from above)
INSERT INTO vehicles (make, model, year, license_plate, vin, color, owner_id) VALUES
('Toyota', 'Camry', '2020', 'CA123GP', '1HGBH41JXMN109186', 'Silver', 4),
('Honda', 'Civic', '2019', 'CA456GP', '2HGBH41JXMN109187', 'Blue', 4),
('Ford', 'Focus', '2021', 'CA789GP', '3HGBH41JXMN109188', 'Red', 5),
('BMW', '320i', '2022', 'CA321GP', '4HGBH41JXMN109189', 'Black', 5),
('Volkswagen', 'Golf', '2020', 'CA654GP', '5HGBH41JXMN109190', 'White', 6),
('Audi', 'A3', '2021', 'CA987GP', '6HGBH41JXMN109191', 'Grey', 6)
ON CONFLICT (license_plate) DO NOTHING;

-- Sample booking slots for the next 30 days
INSERT INTO booking_slots (date, time_slot, max_bookings, current_bookings, is_available)
SELECT
    date_series::date,
    time_slot,
    2 as max_bookings,
    0 as current_bookings,
    true as is_available
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', '1 day') AS date_series,
    unnest(ARRAY['08:00:00'::time, '09:00:00'::time, '10:00:00'::time, '11:00:00'::time,
                 '13:00:00'::time, '14:00:00'::time, '15:00:00'::time, '16:00:00'::time]) AS time_slot
WHERE EXTRACT(dow FROM date_series) BETWEEN 1 AND 5  -- Monday to Friday only
ON CONFLICT (date, time_slot) DO NOTHING;

-- Sample service requests
INSERT INTO service_requests (client_id, vehicle_id, service_id, assigned_mechanic_id, preferred_date, preferred_time, notes, status) VALUES
(4, 1, 1, 2, CURRENT_DATE + INTERVAL '3 days', '09:00:00', 'Car is making unusual noises', 'PENDING_QUOTE'),
(4, 2, 4, NULL, CURRENT_DATE + INTERVAL '5 days', '10:00:00', 'Check engine light is on', 'PENDING_QUOTE'),
(5, 3, 2, 2, CURRENT_DATE + INTERVAL '7 days', '14:00:00', 'Brakes feel spongy', 'QUOTE_SENT'),
(5, 4, 8, 3, CURRENT_DATE + INTERVAL '10 days', '11:00:00', 'Car pulls to the right', 'PENDING_QUOTE'),
(6, 5, 6, NULL, CURRENT_DATE + INTERVAL '14 days', '15:00:00', 'Transmission slipping', 'PENDING_QUOTE')
ON CONFLICT DO NOTHING;

-- Sample service quotes
INSERT INTO service_quotes (request_id, mechanic_id, labour_cost, parts_cost, total_amount, notes, approval_status, valid_until) VALUES
(3, 2, 800.00, 400.00, 1200.00, 'Brake pads and rotors need replacement', 'PENDING', CURRENT_TIMESTAMP + INTERVAL '7 days')
ON CONFLICT (request_id) DO NOTHING;