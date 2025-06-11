BEGIN;

-- 1) Roles (registration service expects role_id = 3)
INSERT INTO roles (role_id, name, description)
VALUES
    (1, 'Admin', 'Admin with full access'),
    (2, 'Manager', 'Manager with room oversight permissions'),
    (3, 'Employee', 'Default role for regular users')
ON CONFLICT (role_id) DO NOTHING;

-- Keep sequence aligned after explicit IDs
SELECT setval(
    pg_get_serial_sequence('roles', 'role_id'),
    COALESCE((SELECT MAX(role_id) FROM roles), 1),
    true
);

-- 2) Rooms
INSERT INTO rooms (name, capacity, location, description)
SELECT 'Atlas', 8, '1st floor', 'Small focus room'
WHERE NOT EXISTS (
    SELECT 1 FROM rooms WHERE name = 'Atlas' AND location = '1st floor'
);

INSERT INTO rooms (name, capacity, location, description)
SELECT 'Orion', 16, '2nd floor', 'Medium collaboration room'
WHERE NOT EXISTS (
    SELECT 1 FROM rooms WHERE name = 'Orion' AND location = '2nd floor'
);

INSERT INTO rooms (name, capacity, location, description)
SELECT 'Nimbus', 30, '5th floor', 'Large boardroom with conference setup'
WHERE NOT EXISTS (
    SELECT 1 FROM rooms WHERE name = 'Nimbus' AND location = '5th floor'
);

-- 3) Equipment
INSERT INTO equipment (name, brand, model, description)
SELECT 'Projector', 'Epson', 'EB-2265U', 'High-lumen conference projector'
WHERE NOT EXISTS (
    SELECT 1 FROM equipment WHERE name = 'Projector' AND model = 'EB-2265U'
);

INSERT INTO equipment (name, brand, model, description)
SELECT 'Display', 'Samsung', 'QM55R', '55-inch meeting room display'
WHERE NOT EXISTS (
    SELECT 1 FROM equipment WHERE name = 'Display' AND model = 'QM55R'
);

INSERT INTO equipment (name, brand, model, description)
SELECT 'Speakerphone', 'Jabra', 'Speak 750', 'USB/Bluetooth conference speaker'
WHERE NOT EXISTS (
    SELECT 1 FROM equipment WHERE name = 'Speakerphone' AND model = 'Speak 750'
);

INSERT INTO equipment (name, brand, model, description)
SELECT 'Whiteboard', 'Nobo', 'Classic', 'Magnetic whiteboard'
WHERE NOT EXISTS (
    SELECT 1 FROM equipment WHERE name = 'Whiteboard' AND model = 'Classic'
);

-- 4) Room-Equipment mappings
INSERT INTO room_equipment_mapping (room_id, equipment_id)
SELECT r.room_id, e.equipment_id
FROM rooms r
JOIN equipment e ON e.name = 'Projector' AND e.model = 'EB-2265U'
WHERE r.name = 'Atlas'
  AND NOT EXISTS (
      SELECT 1
      FROM room_equipment_mapping rem
      WHERE rem.room_id = r.room_id AND rem.equipment_id = e.equipment_id
  );

INSERT INTO room_equipment_mapping (room_id, equipment_id)
SELECT r.room_id, e.equipment_id
FROM rooms r
JOIN equipment e ON e.name = 'Display' AND e.model = 'QM55R'
WHERE r.name = 'Orion'
  AND NOT EXISTS (
      SELECT 1
      FROM room_equipment_mapping rem
      WHERE rem.room_id = r.room_id AND rem.equipment_id = e.equipment_id
  );

INSERT INTO room_equipment_mapping (room_id, equipment_id)
SELECT r.room_id, e.equipment_id
FROM rooms r
JOIN equipment e ON e.name = 'Speakerphone' AND e.model = 'Speak 750'
WHERE r.name IN ('Orion', 'Nimbus')
  AND NOT EXISTS (
      SELECT 1
      FROM room_equipment_mapping rem
      WHERE rem.room_id = r.room_id AND rem.equipment_id = e.equipment_id
  );

INSERT INTO room_equipment_mapping (room_id, equipment_id)
SELECT r.room_id, e.equipment_id
FROM rooms r
JOIN equipment e ON e.name = 'Whiteboard' AND e.model = 'Classic'
WHERE r.name IN ('Atlas', 'Nimbus')
  AND NOT EXISTS (
      SELECT 1
      FROM room_equipment_mapping rem
      WHERE rem.room_id = r.room_id AND rem.equipment_id = e.equipment_id
  );

-- 5) Cleaning types
INSERT INTO cleaning_types (type_name, description)
SELECT 'AFTER_USE', 'Clean room after a booking completes'
WHERE NOT EXISTS (
    SELECT 1 FROM cleaning_types WHERE type_name = 'AFTER_USE'
);

INSERT INTO cleaning_types (type_name, description)
SELECT 'WEEKLY', 'Weekly scheduled cleaning'
WHERE NOT EXISTS (
    SELECT 1 FROM cleaning_types WHERE type_name = 'WEEKLY'
);

INSERT INTO cleaning_types (type_name, description)
SELECT 'ON_REQUEST', 'Cleaning requested by users'
WHERE NOT EXISTS (
    SELECT 1 FROM cleaning_types WHERE type_name = 'ON_REQUEST'
);

-- 6) Room-Cleaning assignments
INSERT INTO room_cleaning_assignments (room_id, cleaning_type_id)
SELECT r.room_id, ct.cleaning_id
FROM rooms r
JOIN cleaning_types ct ON ct.type_name IN ('AFTER_USE', 'WEEKLY', 'ON_REQUEST')
WHERE NOT EXISTS (
    SELECT 1
    FROM room_cleaning_assignments rca
    WHERE rca.room_id = r.room_id
      AND rca.cleaning_type_id = ct.cleaning_id
);

-- 7) Users
-- Passwords below are placeholders and are NOT BCrypt hashes.
-- For login with Spring Security BCrypt, create users through /roompro/register
-- or replace these values with BCrypt hashes.
INSERT INTO users (
    email, password, first_name, last_name, role_id,
    created_at, updated_at, login_count, is_active
)
SELECT
    'admin@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'System', 'Admin', 1,
    NOW(), NOW(), 0, true
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@roompro.local'
);

INSERT INTO users (
    email, password, first_name, last_name, role_id,
    created_at, updated_at, login_count, is_active
)
SELECT
    'manager@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Office', 'Manager', 2,
    NOW(), NOW(), 0, true
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'manager@roompro.local'
);

INSERT INTO users (
    email, password, first_name, last_name, role_id,
    created_at, updated_at, login_count, is_active
)
SELECT
    'employee@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Default', 'Employee', 3,
    NOW(), NOW(), 0, true
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'employee@roompro.local'
);

-- 8) Bookings
INSERT INTO bookings (user_id, room_id, start_time, end_time, is_canceled)
SELECT
    u.user_id,
    r.room_id,
    NOW() + INTERVAL '1 day' + INTERVAL '09:00',
    NOW() + INTERVAL '1 day' + INTERVAL '10:00',
    false
FROM users u
JOIN rooms r ON r.name = 'Atlas'
WHERE u.email = 'employee@roompro.local'
  AND NOT EXISTS (
      SELECT 1
      FROM bookings b
      WHERE b.user_id = u.user_id
        AND b.room_id = r.room_id
        AND b.start_time = NOW() + INTERVAL '1 day' + INTERVAL '09:00'
        AND b.end_time = NOW() + INTERVAL '1 day' + INTERVAL '10:00'
  );

INSERT INTO bookings (user_id, room_id, start_time, end_time, is_canceled)
SELECT
    u.user_id,
    r.room_id,
    NOW() + INTERVAL '1 day' + INTERVAL '14:00',
    NOW() + INTERVAL '1 day' + INTERVAL '15:30',
    false
FROM users u
JOIN rooms r ON r.name = 'Orion'
WHERE u.email = 'manager@roompro.local'
  AND NOT EXISTS (
      SELECT 1
      FROM bookings b
      WHERE b.user_id = u.user_id
        AND b.room_id = r.room_id
        AND b.start_time = NOW() + INTERVAL '1 day' + INTERVAL '14:00'
        AND b.end_time = NOW() + INTERVAL '1 day' + INTERVAL '15:30'
  );

COMMIT;
