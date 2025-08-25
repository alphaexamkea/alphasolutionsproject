-- =============================================================================
-- H2 Database Initialization Script for Integration Tests
-- =============================================================================

-- Drop tables in reverse order to avoid foreign key constraints
DROP TABLE IF EXISTS TimeRegistration;
DROP TABLE IF EXISTS Task;
DROP TABLE IF EXISTS Subproject;
DROP TABLE IF EXISTS Project;
DROP TABLE IF EXISTS Resource;

-- =============================================================================
-- TABLE CREATION (matching production schema)
-- =============================================================================

CREATE TABLE Project (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE
);

CREATE TABLE Subproject (
    subproject_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE Task (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    subproject_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    estimated_hours DECIMAL(5,2),
    deadline DATE,
    status VARCHAR(20) DEFAULT 'TO_DO', -- H2 doesn't support ENUM, using VARCHAR
    FOREIGN KEY (subproject_id) REFERENCES Subproject(subproject_id)
);

CREATE TABLE Resource (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    system_role VARCHAR(10) DEFAULT 'USER', -- H2 doesn't support ENUM, using VARCHAR
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE TimeRegistration (
    time_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    resource_id INT NOT NULL,
    date DATE NOT NULL,
    hours DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (task_id) REFERENCES Task(task_id),
    FOREIGN KEY (resource_id) REFERENCES Resource(resource_id)
);

-- =============================================================================
-- TEST DATA INSERTION
-- =============================================================================

-- Insert test resources (users)
INSERT INTO Resource (name, role, system_role, username, password) VALUES
('John Developer', 'Senior Developer', 'USER', 'john', 'password123'),
('Jane Manager', 'Project Manager', 'ADMIN', 'jane', 'admin123'),
('Bob Designer', 'UI Designer', 'USER', 'bob', 'design123'),
('Alice Tester', 'QA Tester', 'USER', 'alice', 'test123');

-- Insert test projects
INSERT INTO Project (name, description, start_date, end_date) VALUES
('Alpha Solutions Website', 'Company website redesign project', '2024-01-01', '2024-06-30'),
('Mobile App Development', 'New mobile application for customer management', '2024-02-01', '2024-08-31'),
('Data Analytics Platform', 'Internal analytics and reporting system', '2024-03-01', '2024-12-31');

-- Insert test subprojects
INSERT INTO Subproject (project_id, name, description, start_date, end_date) VALUES
-- Website project subprojects
(1, 'Frontend Development', 'React-based frontend development', '2024-01-01', '2024-04-30'),
(1, 'Backend API', 'REST API development with Spring Boot', '2024-01-15', '2024-05-15'),
(1, 'Design & UX', 'User interface and experience design', '2024-01-01', '2024-03-31'),

-- Mobile app subprojects  
(2, 'iOS Development', 'Native iOS application', '2024-02-01', '2024-06-30'),
(2, 'Android Development', 'Native Android application', '2024-02-01', '2024-06-30'),
(2, 'API Integration', 'Backend integration for mobile apps', '2024-03-01', '2024-07-31'),

-- Analytics platform subprojects
(3, 'Data Pipeline', 'ETL processes and data ingestion', '2024-03-01', '2024-08-31'),
(3, 'Dashboard Development', 'Analytics dashboard with charts', '2024-04-01', '2024-10-31');

-- Insert test tasks
INSERT INTO Task (subproject_id, name, description, estimated_hours, deadline, status) VALUES
-- Frontend Development tasks
(1, 'Setup React Project', 'Initialize React project with TypeScript', 8.00, '2024-01-05', 'DONE'),
(1, 'Create Homepage', 'Design and implement homepage components', 16.00, '2024-01-15', 'DONE'),
(1, 'User Authentication', 'Implement login/logout functionality', 12.00, '2024-01-25', 'IN_PROGRESS'),
(1, 'Contact Form', 'Create contact form with validation', 10.00, '2024-02-01', 'TO_DO'),

-- Backend API tasks
(2, 'Setup Spring Boot', 'Initialize Spring Boot project structure', 6.00, '2024-01-20', 'DONE'),
(2, 'User Management API', 'CRUD operations for user management', 20.00, '2024-02-15', 'IN_PROGRESS'),
(2, 'Project API Endpoints', 'REST endpoints for project operations', 24.00, '2024-03-01', 'TO_DO'),

-- Design & UX tasks
(3, 'Wireframes', 'Create wireframes for all main pages', 12.00, '2024-01-10', 'DONE'),
(3, 'UI Mockups', 'High-fidelity mockups and style guide', 18.00, '2024-02-01', 'IN_PROGRESS'),

-- iOS Development tasks
(4, 'Project Setup', 'Setup Xcode project with dependencies', 4.00, '2024-02-05', 'DONE'),
(4, 'User Interface', 'Implement main app screens', 32.00, '2024-04-01', 'IN_PROGRESS'),

-- Analytics Dashboard tasks
(8, 'Chart Components', 'Develop reusable chart components', 16.00, '2024-05-01', 'TO_DO'),
(8, 'Data Visualization', 'Implement various chart types', 24.00, '2024-06-01', 'TO_DO');

-- Insert test time registrations
INSERT INTO TimeRegistration (task_id, resource_id, date, hours) VALUES
-- John working on React setup and homepage
(1, 1, '2024-01-02', 8.00),
(2, 1, '2024-01-08', 7.50),
(2, 1, '2024-01-09', 6.00),
(2, 1, '2024-01-10', 2.50),
(3, 1, '2024-01-22', 4.00),
(3, 1, '2024-01-23', 3.50),

-- Jane working on backend setup and project management
(5, 2, '2024-01-18', 6.00),
(6, 2, '2024-02-01', 5.00),
(6, 2, '2024-02-02', 6.50),

-- Bob working on design tasks
(8, 3, '2024-01-05', 4.00),
(8, 3, '2024-01-08', 8.00),
(9, 3, '2024-01-15', 6.00),
(9, 3, '2024-01-16', 7.00),

-- Alice working on iOS development
(10, 4, '2024-02-05', 4.00),
(11, 4, '2024-02-12', 8.00),
(11, 4, '2024-02-13', 7.50),

-- Additional cross-resource time entries for testing analytics
(1, 2, '2024-01-03', 2.00), -- Jane helping with React setup
(5, 1, '2024-01-19', 1.50), -- John consulting on backend
(8, 1, '2024-01-06', 3.00); -- John reviewing wireframes

-- =============================================================================
-- Initialization Complete
-- =============================================================================