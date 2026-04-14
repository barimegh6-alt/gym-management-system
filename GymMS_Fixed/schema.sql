-- ============================================================
-- FitPro Gym Management System — MySQL Schema
-- Run this entire file in MySQL Workbench or terminal
-- ============================================================

CREATE DATABASE IF NOT EXISTS gym_management;
USE gym_management;

-- ── admins ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS admins (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    phone       VARCHAR(15),
    password    VARCHAR(255)  NOT NULL,
    admin_level VARCHAR(20)   DEFAULT 'Standard',
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- ── members ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS members (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    phone           VARCHAR(15)   NOT NULL,
    password        VARCHAR(255)  NOT NULL,
    membership_type VARCHAR(30)   NOT NULL DEFAULT 'Basic',
    join_date       DATE          NOT NULL,
    expiry_date     DATE          NOT NULL,
    address         VARCHAR(255),
    age             INT           NOT NULL,
    gender          VARCHAR(10)   NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'Active',
    created_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- ── trainers ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS trainers (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100)  NOT NULL,
    email            VARCHAR(150)  NOT NULL UNIQUE,
    phone            VARCHAR(15)   NOT NULL,
    password         VARCHAR(255)  NOT NULL,
    specialization   VARCHAR(100)  NOT NULL,
    experience_years INT           NOT NULL DEFAULT 0,
    qualification    VARCHAR(100),
    salary           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    availability     VARCHAR(20)   DEFAULT 'Full-Time',
    status           VARCHAR(20)   DEFAULT 'Active',
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- ── workouts ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS workouts (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    plan_name        VARCHAR(150)  NOT NULL,
    description      TEXT,
    category         VARCHAR(50)   NOT NULL,
    difficulty       VARCHAR(20)   NOT NULL DEFAULT 'Beginner',
    duration_minutes INT           NOT NULL DEFAULT 60,
    member_id        INT           NOT NULL,
    trainer_id       INT           NOT NULL,
    assigned_date    VARCHAR(30)   NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'Active',
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id)  REFERENCES members(id)  ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE
);

-- ── schedule ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS schedule (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    member_id        INT           NOT NULL,
    trainer_id       INT           NOT NULL,
    session_date     VARCHAR(30)   NOT NULL,
    session_time     VARCHAR(20)   NOT NULL,
    session_type     VARCHAR(100)  NOT NULL,
    duration_minutes INT           NOT NULL DEFAULT 60,
    notes            TEXT,
    status           VARCHAR(20)   NOT NULL DEFAULT 'Scheduled',
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id)  REFERENCES members(id)  ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Admins  (password: admin123)
INSERT INTO admins (name, email, phone, password, admin_level) VALUES
('Super Admin', 'admin@fitpro.com',   '9999999999', 'admin123',   'Super'),
('Gym Manager', 'manager@fitpro.com', '9888888888', 'manager123', 'Standard');

-- Trainers  (password: trainer123)
INSERT INTO trainers (name, email, phone, password, specialization, experience_years, qualification, salary, availability, status) VALUES
('Arjun Sharma', 'arjun@fitpro.com',  '9876543210', 'trainer123', 'Weight Training & Powerlifting', 7, 'CSCS, CPT',          55000.00, 'Full-Time', 'Active'),
('Priya Mehta',  'priya@fitpro.com',  '9765432109', 'trainer123', 'Yoga & Pilates',                5, 'RYT-200, B.P.Ed',    48000.00, 'Full-Time', 'Active'),
('Rahul Verma',  'rahul@fitpro.com',  '9654321098', 'trainer123', 'CrossFit & HIIT',              8, 'CF-L2, NASM-CPT',     62000.00, 'Full-Time', 'Active'),
('Sneha Kapoor', 'sneha@fitpro.com',  '9543210987', 'trainer123', 'Cardio & Zumba',               4, 'ACE-CPT',             42000.00, 'Part-Time', 'Active'),
('Vikram Nair',  'vikram@fitpro.com', '9432109876', 'trainer123', 'Boxing & MMA',                 6, 'AIBA Coach Lvl 1',   52000.00, 'Full-Time', 'Active');

-- Members  (password: member123)
INSERT INTO members (name, email, phone, password, membership_type, join_date, expiry_date, address, age, gender, status) VALUES
('Ananya Reddy',   'ananya@gmail.com',    '9811112222', 'member123', 'Premium',  '2024-01-15', '2025-01-15', 'Bandra, Mumbai',    27, 'Female', 'Active'),
('Rohan Gupta',    'rohan@gmail.com',     '9822223333', 'member123', 'Standard', '2024-02-01', '2025-02-01', 'Powai, Mumbai',     31, 'Male',   'Active'),
('Kavya Iyer',     'kavya@gmail.com',     '9833334444', 'member123', 'Basic',    '2024-03-10', '2024-09-10', 'Andheri, Mumbai',   24, 'Female', 'Expired'),
('Aditya Joshi',   'aditya@gmail.com',    '9844445555', 'member123', 'Premium',  '2024-01-20', '2025-01-20', 'Thane, Mumbai',     35, 'Male',   'Active'),
('Meera Pillai',   'meera@gmail.com',     '9855556666', 'member123', 'Standard', '2024-04-05', '2025-04-05', 'Juhu, Mumbai',      28, 'Female', 'Active'),
('Siddharth Jain', 'siddharth@gmail.com', '9866667777', 'member123', 'Basic',    '2024-05-12', '2025-05-12', 'Dadar, Mumbai',     22, 'Male',   'Active'),
('Pooja Sharma',   'pooja@gmail.com',     '9877778888', 'member123', 'Premium',  '2024-06-01', '2025-06-01', 'Colaba, Mumbai',    30, 'Female', 'Active'),
('Karan Malhotra', 'karan@gmail.com',     '9888889999', 'member123', 'Standard', '2024-07-18', '2025-07-18', 'Navi Mumbai',       26, 'Male',   'Active');

-- Workouts
INSERT INTO workouts (plan_name, description, category, difficulty, duration_minutes, member_id, trainer_id, assigned_date, status) VALUES
('Full Body Blast',    'Compound movements for total body strength',  'Strength',    'Intermediate', 75, 1, 1, '2024-12-01', 'Active'),
('Morning Yoga Flow',  'Sun salutations + flexibility sequence',      'Flexibility', 'Beginner',     60, 2, 2, '2024-12-02', 'Active'),
('HIIT Cardio Shred',  'High intensity 20-10 tabata intervals',       'HIIT',        'Advanced',     45, 4, 3, '2024-12-03', 'Completed'),
('Zumba Fat Burn',     'Dance-based cardio with Latin rhythms',       'Cardio',      'Beginner',     50, 5, 4, '2024-12-05', 'Active'),
('Boxing Fundamentals','Jab-cross combos, footwork, core',            'Mixed',       'Intermediate', 60, 7, 5, '2024-12-07', 'Active'),
('Power Lifting Prep', 'Progressive overload squat/deadlift/bench',   'Strength',    'Advanced',     90, 8, 1, '2024-12-10', 'Active'),
('Core & Flexibility', 'Pilates-inspired core + deep stretching',     'Flexibility', 'Beginner',     45, 6, 2, '2024-12-11', 'Active');

-- Schedule
INSERT INTO schedule (member_id, trainer_id, session_date, session_time, session_type, duration_minutes, notes, status) VALUES
(1, 1, '2025-05-15', '07:00 AM', 'Weight Training',  60, 'Focus on legs today',          'Scheduled'),
(2, 2, '2025-05-15', '08:00 AM', 'Yoga',             60, 'Morning restorative session',  'Scheduled'),
(4, 3, '2025-05-15', '09:00 AM', 'HIIT',             45, 'Bring energy drink',           'Scheduled'),
(5, 4, '2025-05-16', '10:00 AM', 'Cardio',           50, 'New playlist prepared',        'Scheduled'),
(7, 5, '2025-05-16', '06:00 PM', 'Boxing',           60, 'Sparring session',             'Scheduled'),
(1, 1, '2025-04-13', '07:00 AM', 'Weight Training',  60, 'Chest and back day',           'Completed'),
(8, 1, '2025-04-14', '08:00 AM', 'Weight Training',  75, 'Deadlift PR attempt',          'Completed'),
(6, 2, '2025-04-10', '09:00 AM', 'Yoga',             60, 'Cancelled by member',          'Cancelled');
