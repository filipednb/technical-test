-- Insert initial users
INSERT INTO "user" (id, type, name, email)
VALUES
('123e4567-e89b-12d3-a456-426614174000', 'OWNER', 'Jack Spencer', 'jack.spec@dummy.com'),
('789e0123-e89b-12d3-a456-426614174000', 'GUEST', 'Jonah Clement', 'jonah.cle@yahoo.com');

-- Insert initial properties
INSERT INTO property (id, name, owner_id, location)
VALUES
('456e7890-e89b-12d3-a456-426614174000', 'Ocean View Apartment', '123e4567-e89b-12d3-a456-426614174000', 'Miami, FL'),
('789e0123-e89b-12d3-a456-426614174000', 'Mountain Cabin', '123e4567-e89b-12d3-a456-426614174000', 'Seattle, WA');

-- Insert initial bookings
INSERT INTO booking (id, guest_id, property_id, check_in_date, check_out_date, status, created_at, updated_at)
VALUES
('001e4567-e89b-12d3-a456-426614174000', '789e0123-e89b-12d3-a456-426614174000', '456e7890-e89b-12d3-a456-426614174000', '2024-07-01', '2024-07-10', 'PENDING', now(), now()),
('002e4567-e89b-12d3-a456-426614174000', '789e0123-e89b-12d3-a456-426614174000', '789e0123-e89b-12d3-a456-426614174000', '2024-07-11', '2024-07-20', 'PENDING', now(), now());
