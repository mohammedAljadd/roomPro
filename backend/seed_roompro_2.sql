BEGIN;

-- RoomPro expanded seed data.
-- Note: there is no dedicated "buildings" table in this schema,
-- so building/campus details are stored in rooms.location.

-- 1) Roles (registration flow expects role_id = 3 for Employee)
WITH seed_roles (role_id, name, description) AS (
    VALUES
        (1, 'Admin', 'Admin with full access'),
        (2, 'Manager', 'Manager with room oversight permissions'),
        (3, 'Employee', 'Default role for regular users')
)
INSERT INTO roles (role_id, name, description)
SELECT sr.role_id, sr.name, sr.description
FROM seed_roles sr
WHERE NOT EXISTS (
    SELECT 1 FROM roles r WHERE r.role_id = sr.role_id
);

-- Keep role sequence aligned when sequence/identity exists
DO $$
DECLARE
    seq_name text;
BEGIN
    seq_name := pg_get_serial_sequence('roles', 'role_id');
    IF seq_name IS NOT NULL THEN
        EXECUTE format(
            'SELECT setval(%L, COALESCE((SELECT MAX(role_id) FROM roles), 1), true)',
            seq_name
        );
    END IF;
END $$;

-- 2) Users
-- Explicit user_id is used so this script still works if users.user_id has no DB default.
WITH seed_users (user_id, email, password, first_name, last_name, role_id) AS (
    VALUES
        (10001, 'admin@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'System', 'Admin', 1),
        (10002, 'manager@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Office', 'Manager', 2),
        (10003, 'employee@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Default', 'Employee', 3),
        (10004, 'employee1@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Nora', 'Ibrahim', 3),
        (10005, 'employee2@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Youssef', 'Nader', 3),
        (10006, 'analyst@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Mina', 'Farah', 3),
        (10007, 'designer@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Salma', 'Adel', 3),
        (10008, 'support@roompro.local', 'CHANGE_ME_BCRYPT_HASH', 'Rami', 'Khaled', 3)
)
INSERT INTO users (
    user_id, email, password, first_name, last_name, role_id,
    created_at, updated_at, login_count, is_active
)
SELECT
    su.user_id, su.email, su.password, su.first_name, su.last_name, su.role_id,
    NOW(), NOW(), 0, true
FROM seed_users su
WHERE NOT EXISTS (
    SELECT 1 FROM users u WHERE u.email = su.email
);

-- Keep user sequence aligned when sequence/identity exists
DO $$
DECLARE
    seq_name text;
BEGIN
    seq_name := pg_get_serial_sequence('users', 'user_id');
    IF seq_name IS NOT NULL THEN
        EXECUTE format(
            'SELECT setval(%L, COALESCE((SELECT MAX(user_id) FROM users), 1), true)',
            seq_name
        );
    END IF;
END $$;

-- 3) Rooms (building data is embedded in location)
WITH seed_rooms (name, capacity, location, description) AS (
    VALUES
        ('Aquila', 8,  'North Campus - Building A - Floor 1', 'Focus room near engineering wing'),
        ('Borealis', 10, 'North Campus - Building A - Floor 2', 'Hybrid meeting room with medium capacity'),
        ('Cedar', 6, 'North Campus - Building B - Floor 1', 'Small project sync room'),
        ('Delta', 12, 'North Campus - Building B - Floor 3', 'Team collaboration room'),
        ('Echo', 4, 'South Campus - Building C - Floor 1', 'Quick stand-up room'),
        ('Harbor', 14, 'South Campus - Building C - Floor 2', 'Operations room with VC support'),
        ('Luna', 20, 'South Campus - Building D - Floor 5', 'Training room with dual displays'),
        ('Nova', 16, 'South Campus - Building D - Floor 6', 'Strategy room with whiteboard wall'),
        ('Summit', 24, 'HQ Tower - Building E - Floor 10', 'Executive boardroom'),
        ('Vertex', 18, 'HQ Tower - Building E - Floor 11', 'Large cross-functional meeting room'),
        ('Zephyr', 8, 'HQ Tower - Building F - Floor 3', 'Interview and 1:1 room')
)
INSERT INTO rooms (name, capacity, location, description)
SELECT sr.name, sr.capacity, sr.location, sr.description
FROM seed_rooms sr
WHERE NOT EXISTS (
    SELECT 1
    FROM rooms r
    WHERE r.name = sr.name
      AND r.location = sr.location
);

-- 4) Equipment
WITH seed_equipment (name, brand, model, description) AS (
    VALUES
        ('Projector', 'Epson', 'EB-2265U', 'High-lumen conference projector'),
        ('Display', 'Samsung', 'QM55R', '55-inch meeting room display'),
        ('Speakerphone', 'Jabra', 'Speak 750', 'USB/Bluetooth conference speaker'),
        ('Whiteboard', 'Nobo', 'Classic', 'Magnetic whiteboard'),
        ('Conference Camera', 'Logitech', 'Rally Bar', '4K conference camera bar'),
        ('Video Bar', 'Poly', 'Studio X50', 'All-in-one video collaboration bar'),
        ('Wireless Presentation', 'Barco', 'ClickShare C-10', 'Wireless content sharing hub'),
        ('Room Scheduler', 'Cisco', 'Room Navigator', 'Door-mounted room scheduling tablet'),
        ('Smart Board', 'Samsung', 'Flip Pro WM65B', 'Interactive smart board'),
        ('Air Purifier', 'Dyson', 'BP04', 'Air purification for enclosed rooms')
)
INSERT INTO equipment (name, brand, model, description)
SELECT se.name, se.brand, se.model, se.description
FROM seed_equipment se
WHERE NOT EXISTS (
    SELECT 1
    FROM equipment e
    WHERE e.name = se.name
      AND e.model = se.model
);

-- 5) Room-Equipment mappings
WITH seed_mapping (room_name, room_location, equipment_name, equipment_model) AS (
    VALUES
        ('Aquila', 'North Campus - Building A - Floor 1', 'Display', 'QM55R'),
        ('Aquila', 'North Campus - Building A - Floor 1', 'Speakerphone', 'Speak 750'),
        ('Aquila', 'North Campus - Building A - Floor 1', 'Whiteboard', 'Classic'),

        ('Borealis', 'North Campus - Building A - Floor 2', 'Display', 'QM55R'),
        ('Borealis', 'North Campus - Building A - Floor 2', 'Wireless Presentation', 'ClickShare C-10'),
        ('Borealis', 'North Campus - Building A - Floor 2', 'Speakerphone', 'Speak 750'),
        ('Borealis', 'North Campus - Building A - Floor 2', 'Room Scheduler', 'Room Navigator'),

        ('Cedar', 'North Campus - Building B - Floor 1', 'Display', 'QM55R'),
        ('Cedar', 'North Campus - Building B - Floor 1', 'Whiteboard', 'Classic'),

        ('Delta', 'North Campus - Building B - Floor 3', 'Projector', 'EB-2265U'),
        ('Delta', 'North Campus - Building B - Floor 3', 'Speakerphone', 'Speak 750'),
        ('Delta', 'North Campus - Building B - Floor 3', 'Whiteboard', 'Classic'),
        ('Delta', 'North Campus - Building B - Floor 3', 'Room Scheduler', 'Room Navigator'),

        ('Echo', 'South Campus - Building C - Floor 1', 'Display', 'QM55R'),
        ('Echo', 'South Campus - Building C - Floor 1', 'Whiteboard', 'Classic'),

        ('Harbor', 'South Campus - Building C - Floor 2', 'Video Bar', 'Studio X50'),
        ('Harbor', 'South Campus - Building C - Floor 2', 'Wireless Presentation', 'ClickShare C-10'),
        ('Harbor', 'South Campus - Building C - Floor 2', 'Room Scheduler', 'Room Navigator'),
        ('Harbor', 'South Campus - Building C - Floor 2', 'Air Purifier', 'BP04'),

        ('Luna', 'South Campus - Building D - Floor 5', 'Projector', 'EB-2265U'),
        ('Luna', 'South Campus - Building D - Floor 5', 'Conference Camera', 'Rally Bar'),
        ('Luna', 'South Campus - Building D - Floor 5', 'Wireless Presentation', 'ClickShare C-10'),
        ('Luna', 'South Campus - Building D - Floor 5', 'Smart Board', 'Flip Pro WM65B'),

        ('Nova', 'South Campus - Building D - Floor 6', 'Display', 'QM55R'),
        ('Nova', 'South Campus - Building D - Floor 6', 'Video Bar', 'Studio X50'),
        ('Nova', 'South Campus - Building D - Floor 6', 'Whiteboard', 'Classic'),
        ('Nova', 'South Campus - Building D - Floor 6', 'Air Purifier', 'BP04'),

        ('Summit', 'HQ Tower - Building E - Floor 10', 'Projector', 'EB-2265U'),
        ('Summit', 'HQ Tower - Building E - Floor 10', 'Conference Camera', 'Rally Bar'),
        ('Summit', 'HQ Tower - Building E - Floor 10', 'Video Bar', 'Studio X50'),
        ('Summit', 'HQ Tower - Building E - Floor 10', 'Smart Board', 'Flip Pro WM65B'),
        ('Summit', 'HQ Tower - Building E - Floor 10', 'Room Scheduler', 'Room Navigator'),

        ('Vertex', 'HQ Tower - Building E - Floor 11', 'Display', 'QM55R'),
        ('Vertex', 'HQ Tower - Building E - Floor 11', 'Conference Camera', 'Rally Bar'),
        ('Vertex', 'HQ Tower - Building E - Floor 11', 'Wireless Presentation', 'ClickShare C-10'),
        ('Vertex', 'HQ Tower - Building E - Floor 11', 'Whiteboard', 'Classic'),

        ('Zephyr', 'HQ Tower - Building F - Floor 3', 'Display', 'QM55R'),
        ('Zephyr', 'HQ Tower - Building F - Floor 3', 'Speakerphone', 'Speak 750'),
        ('Zephyr', 'HQ Tower - Building F - Floor 3', 'Room Scheduler', 'Room Navigator')
)
INSERT INTO room_equipment_mapping (room_id, equipment_id)
SELECT r.room_id, e.equipment_id
FROM seed_mapping sm
JOIN rooms r
  ON r.name = sm.room_name
 AND r.location = sm.room_location
JOIN equipment e
  ON e.name = sm.equipment_name
 AND e.model = sm.equipment_model
WHERE NOT EXISTS (
    SELECT 1
    FROM room_equipment_mapping rem
    WHERE rem.room_id = r.room_id
      AND rem.equipment_id = e.equipment_id
);

-- 6) Cleaning types
WITH seed_cleaning_types (type_name, description) AS (
    VALUES
        ('AFTER_USE', 'Clean room after a booking completes'),
        ('WEEKLY', 'Weekly scheduled cleaning'),
        ('ON_REQUEST', 'Cleaning requested by users')
)
INSERT INTO cleaning_types (type_name, description)
SELECT sct.type_name, sct.description
FROM seed_cleaning_types sct
WHERE NOT EXISTS (
    SELECT 1
    FROM cleaning_types ct
    WHERE ct.type_name = sct.type_name
);

-- 7) Assign all cleaning types to seeded rooms
WITH seeded_rooms (room_id) AS (
    SELECT r.room_id
    FROM rooms r
    JOIN (
        VALUES
            ('Aquila', 'North Campus - Building A - Floor 1'),
            ('Borealis', 'North Campus - Building A - Floor 2'),
            ('Cedar', 'North Campus - Building B - Floor 1'),
            ('Delta', 'North Campus - Building B - Floor 3'),
            ('Echo', 'South Campus - Building C - Floor 1'),
            ('Harbor', 'South Campus - Building C - Floor 2'),
            ('Luna', 'South Campus - Building D - Floor 5'),
            ('Nova', 'South Campus - Building D - Floor 6'),
            ('Summit', 'HQ Tower - Building E - Floor 10'),
            ('Vertex', 'HQ Tower - Building E - Floor 11'),
            ('Zephyr', 'HQ Tower - Building F - Floor 3')
    ) AS v(name, location)
      ON r.name = v.name
     AND r.location = v.location
),
needed_types (cleaning_id) AS (
    SELECT ct.cleaning_id
    FROM cleaning_types ct
    WHERE ct.type_name IN ('AFTER_USE', 'WEEKLY', 'ON_REQUEST')
)
INSERT INTO room_cleaning_assignments (room_id, cleaning_type_id)
SELECT sr.room_id, nt.cleaning_id
FROM seeded_rooms sr
CROSS JOIN needed_types nt
WHERE NOT EXISTS (
    SELECT 1
    FROM room_cleaning_assignments rca
    WHERE rca.room_id = sr.room_id
      AND rca.cleaning_type_id = nt.cleaning_id
);

-- 8) Weekly cleaning slots (non-overlapping per room)
WITH seed_weekly (room_name, room_location, cleaning_day, start_time, end_time) AS (
    VALUES
        ('Aquila', 'North Campus - Building A - Floor 1', 'MONDAY',    '18:30:00'::time, '19:00:00'::time),
        ('Borealis', 'North Campus - Building A - Floor 2', 'MONDAY',  '19:00:00'::time, '19:30:00'::time),
        ('Cedar', 'North Campus - Building B - Floor 1', 'TUESDAY',    '18:00:00'::time, '18:30:00'::time),
        ('Delta', 'North Campus - Building B - Floor 3', 'TUESDAY',    '18:30:00'::time, '19:00:00'::time),
        ('Echo', 'South Campus - Building C - Floor 1', 'WEDNESDAY',   '18:00:00'::time, '18:20:00'::time),
        ('Harbor', 'South Campus - Building C - Floor 2', 'WEDNESDAY', '18:30:00'::time, '19:00:00'::time),
        ('Luna', 'South Campus - Building D - Floor 5', 'THURSDAY',    '18:00:00'::time, '18:40:00'::time),
        ('Nova', 'South Campus - Building D - Floor 6', 'THURSDAY',    '18:45:00'::time, '19:15:00'::time),
        ('Summit', 'HQ Tower - Building E - Floor 10', 'FRIDAY',       '18:00:00'::time, '18:45:00'::time),
        ('Vertex', 'HQ Tower - Building E - Floor 11', 'FRIDAY',       '19:00:00'::time, '19:30:00'::time),
        ('Zephyr', 'HQ Tower - Building F - Floor 3', 'MONDAY',        '17:45:00'::time, '18:15:00'::time)
)
INSERT INTO cleaning_weekly (room_id, starttime, endtime, cleaning_day, set_date)
SELECT
    r.room_id,
    sw.start_time,
    sw.end_time,
    sw.cleaning_day,
    NOW()
FROM seed_weekly sw
JOIN rooms r
  ON r.name = sw.room_name
 AND r.location = sw.room_location
WHERE NOT EXISTS (
    SELECT 1
    FROM cleaning_weekly cw
    WHERE cw.room_id = r.room_id
      AND cw.cleaning_day = sw.cleaning_day
      AND cw.starttime = sw.start_time
      AND cw.endtime = sw.end_time
);

-- 9) Reservations / bookings
-- These slots are daytime and avoid clashes with the weekly cleaning times above.
WITH seed_reservations (user_email, room_name, room_location, day_offset, start_clock, end_clock) AS (
    VALUES
        ('employee@roompro.local',  'Aquila',   'North Campus - Building A - Floor 1', 2, '09:00:00', '10:00:00'),
        ('manager@roompro.local',   'Delta',    'North Campus - Building B - Floor 3', 2, '10:00:00', '11:00:00'),
        ('employee1@roompro.local', 'Zephyr',   'HQ Tower - Building F - Floor 3',     2, '11:00:00', '12:00:00'),
        ('analyst@roompro.local',   'Summit',   'HQ Tower - Building E - Floor 10',    2, '14:00:00', '15:00:00'),

        ('employee2@roompro.local', 'Cedar',    'North Campus - Building B - Floor 1', 3, '09:00:00', '09:45:00'),
        ('designer@roompro.local',  'Harbor',   'South Campus - Building C - Floor 2', 3, '10:00:00', '11:00:00'),
        ('support@roompro.local',   'Luna',     'South Campus - Building D - Floor 5', 3, '11:30:00', '12:30:00'),
        ('manager@roompro.local',   'Vertex',   'HQ Tower - Building E - Floor 11',    3, '15:00:00', '16:00:00'),

        ('employee1@roompro.local', 'Nova',     'South Campus - Building D - Floor 6', 4, '09:30:00', '10:30:00'),
        ('employee@roompro.local',  'Borealis', 'North Campus - Building A - Floor 2', 4, '10:45:00', '11:45:00'),
        ('analyst@roompro.local',   'Echo',     'South Campus - Building C - Floor 1', 4, '13:00:00', '13:30:00'),
        ('support@roompro.local',   'Summit',   'HQ Tower - Building E - Floor 10',    4, '16:00:00', '17:00:00'),

        ('employee2@roompro.local', 'Aquila',   'North Campus - Building A - Floor 1', 5, '09:00:00', '10:00:00'),
        ('designer@roompro.local',  'Delta',    'North Campus - Building B - Floor 3', 5, '10:15:00', '11:15:00'),
        ('manager@roompro.local',   'Harbor',   'South Campus - Building C - Floor 2', 5, '14:00:00', '15:00:00'),
        ('admin@roompro.local',     'Luna',     'South Campus - Building D - Floor 5', 5, '15:30:00', '16:30:00'),

        ('employee@roompro.local',  'Zephyr',   'HQ Tower - Building F - Floor 3',     6, '09:00:00', '10:00:00'),
        ('support@roompro.local',   'Vertex',   'HQ Tower - Building E - Floor 11',    6, '11:00:00', '12:00:00')
),
resolved_reservations AS (
    SELECT
        u.user_id,
        r.room_id,
        date_trunc('day', NOW()) + (sr.day_offset || ' days')::interval + sr.start_clock::interval AS start_time,
        date_trunc('day', NOW()) + (sr.day_offset || ' days')::interval + sr.end_clock::interval AS end_time
    FROM seed_reservations sr
    JOIN users u
      ON u.email = sr.user_email
    JOIN rooms r
      ON r.name = sr.room_name
     AND r.location = sr.room_location
)
INSERT INTO bookings (user_id, room_id, start_time, end_time, is_canceled)
SELECT
    rr.user_id,
    rr.room_id,
    rr.start_time,
    rr.end_time,
    false
FROM resolved_reservations rr
WHERE NOT EXISTS (
    SELECT 1
    FROM bookings b
    WHERE b.user_id = rr.user_id
      AND b.room_id = rr.room_id
      AND b.start_time = rr.start_time
      AND b.end_time = rr.end_time
);

COMMIT;
