CREATE TABLE IF NOT EXISTS Project (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE
);

CREATE TABLE IF NOT EXISTS Subproject (
    subproject_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE IF NOT EXISTS Task (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    subproject_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    estimated_hours DECIMAL(5,2),
    deadline DATE,
    status ENUM('TO_DO', 'IN_PROGRESS', 'DONE') DEFAULT 'TO_DO',
    FOREIGN KEY (subproject_id) REFERENCES Subproject(subproject_id)
);

CREATE TABLE IF NOT EXISTS Resource (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    system_role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS TimeRegistration (
    time_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    resource_id INT NOT NULL,
    date DATE NOT NULL,
    hours DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (task_id) REFERENCES Task(task_id),
    FOREIGN KEY (resource_id) REFERENCES Resource(resource_id)
);