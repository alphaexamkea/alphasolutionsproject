# Alpha Solutions - Projektkalkulationsværktøj

Ett webbaseret projektkalkulationsværktøj udviklet til Alpha Solutions som KEA Datamatiker eksamensprojekt (2. semester).

## 🚀 Live Application

**Kørende version:** [https://alphasolutionsproject.azurewebsites.net](https://alphasolutionsproject.azurewebsites.net)

**GitHub Repository:** [https://github.com/alphaexamkea/alphasolutionsproject](https://github.com/alphaexamkea/alphasolutionsproject)

## 📋 Projekt Overview

Alpha Solutions projektkalkulationsværktøj erstatter manuelle Excel-baserede processer med et fuldt implementeret web-system der håndterer:

- **Hierarkisk projektstruktur**: Project → Subproject → Task → TimeRegistration
- **Tidsregistrering og aggregering** på alle niveauer
- **Rollebaseret adgangskontrol** (Admin/User)
- **Realtidsoverblik** over projektøkonomi og ressourceforbrug

## 🛠️ Teknologi Stack

### Core Technologies
- **Java 21** (LTS version)
- **Spring Boot 3.5.4** - Web framework
- **Spring Data JDBC** med JdbcTemplate
- **MySQL 8.0** - Production database
- **H2 Database** - Test environment
- **Thymeleaf 3.1** - Template engine
- **Maven 3.9.11** - Build management

### Development & Deployment
- **JUnit 5.10.2** - Testing framework
- **GitHub Actions** - CI/CD pipeline
- **Azure Web Apps** - Cloud hosting (PaaS)
- **IntelliJ IDEA Ultimate 2024.3** - IDE
- **Git 2.45** - Version control

## 💻 Software Forudsætninger

### Minimum Requirements
- **Java 21 JDK** (LTS version required)
- **Maven 3.8+** 
- **MySQL 8.0+** eller **H2** (til test)
- **Git 2.30+**

### Development Environment
- **IntelliJ IDEA Ultimate 2024.3+** (anbefalet)
- **MySQL Workbench** eller tilsvarende database client
- **Web browser** (Chrome 100+, Firefox 90+, Safari 15+, Edge 100+)

## 🚀 Installation & Setup

### 1. Clone Repository
```bash
git clone https://github.com/alphaexamkea/alphasolutionsproject.git
cd alphasolutionsproject
```

### 2. Database Setup

**Option A: MySQL (Production-like)**
```sql
-- Create database
CREATE DATABASE alphasolutions_db;

-- Run schema (automatic via Spring Boot)
-- Tables: Project, Subproject, Task, Resource, TimeRegistration
```

**Option B: H2 (Development)**
```properties
# application.properties configured for H2
# Database runs in-memory, no setup required
```

### 3. Application Properties
```properties
# Database Configuration (adjust for your environment)
spring.datasource.url=jdbc:mysql://localhost:3306/alphasolutions_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/JDBC Configuration
spring.sql.init.mode=always
spring.jpa.hibernate.ddl-auto=update
```

### 4. Build & Run
```bash
# Clean install with tests
mvn clean install

# Run application
mvn spring-boot:run

# Or run JAR directly
java -jar target/AlphaSolutions-0.0.1-SNAPSHOT.jar
```

### 5. Access Application
- **Local:** http://localhost:8080
- **Login credentials:** Se test data eller opret ny bruger

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Test Coverage
- **47 tests** fordelt på 6 testklasser
- **Repository Integration Tests** (MySQL & H2)
- **Controller Tests** med MockMvc
- **End-to-end** session og authentication tests

### Test Database
- Bruger H2 in-memory database
- Automatisk setup via `h2init.sql` med testdata
- Isolation mellem tests

## 📁 Projektstruktur

```
src/main/java/com/kea/alphasolutions/
├── controller/          # 6 controllers (Project, Task, etc.)
├── service/            # 6 services (Business logic)
├── repository/         # 6 repositories (JdbcTemplate)
├── model/              # 5 domain entities
├── rowmapper/          # 5 RowMappers (SQL→Object)
└── util/               # AuthenticationUtil (Session)

src/main/resources/
├── templates/          # Thymeleaf HTML templates
├── static/             # CSS, JS, images
├── schema.sql          # Database schema
└── application.properties

src/test/
├── java/               # Integration & unit tests
└── resources/h2init.sql # Test data
```

## 🔐 Authentication & Authorization

### Brugerroller
- **ADMIN**: Fuld adgang til alle funktioner
  - Projekt/Subproject/Task CRUD
  - Ressource administration
  - Alle tidsregistreringer
  
- **USER**: Begrænsede brugerrettigheder  
  - Læse egne projekter
  - Registrere egen arbejdstid
  - Ingen administrative funktioner

### Standard Login
```
Admin bruger:
Username: admin
Password: password123

Regular bruger:
Username: user  
Password: password123
```

⚠️ **Security Note:** Passwords gemmes i plaintext (kendt technical debt)

## 🌐 Deployment

### Azure Web Apps
- **Production URL:** https://alphasolutionsproject.azurewebsites.net
- **Environment:** Azure Web Apps (Java 21)
- **Database:** Azure Database for MySQL
- **Automatic deployment** via GitHub Actions

### CI/CD Pipeline
```yaml
# .github/workflows/main_alphasolutionsproject.yml
- Build with Maven
- Run all tests  
- Deploy to Azure Web Apps
- Database migration (automatic)
```

## 📊 System Features

### Hierarkisk Tidsaggregering
- Timer registreres på **Task**-niveau
- Automatisk summering op gennem **Subproject** → **Project**
- Sammenligning af estimerede vs. faktiske timer

### Automatisk Status Updates
- Tasks opdateres fra "TO_DO" → "IN_PROGRESS" ved første tidsregistrering
- Status tracking gennem hele hierarkiet

### Responsive Design
- Mobile-first approach
- Fungerer på skærmstørrelser 320px - 1920px

## 📈 Performance & Reliability

- **Response time:** < 2 sekunder (normal network)
- **Database queries:** Optimeret med JdbcTemplate
- **Session management:** Server-side sessions
- **Error handling:** Custom error pages (403, 404, 500)

## 🤝 Contributing

Se [CONTRIBUTE.md](CONTRIBUTE.md) for detaljerede instruktioner om hvordan du bidrager til projektet.

## 📄 Documentation

### Rapport & Dokumentation
- **Eksamenrapport**: 35-40 sider systemdokumentation
- **User Stories**: Detaljerede acceptance criteria 
- **Test dokumentation**: Omfattende test coverage
- **Arkitektur**: 3-lags MVC med Repository pattern

### Links
- **GitHub Issues**: [Project Backlog](https://github.com/alphaexamkea/alphasolutionsproject/issues)
- **GitHub Actions**: Automatisk CI/CD workflow
- **Live Application**: https://alphasolutionsproject.azurewebsites.net

## ⚡ Quick Start

```bash
# Get started in 3 steps:
1. git clone https://github.com/alphaexamkea/alphasolutionsproject.git
2. mvn spring-boot:run  
3. Open http://localhost:8080
```

---

**KEA Datamatiker Eksamensprojekt (2. semester)**  
**Udviklet af:** Christian Vinther  
**Semester:** F2025  
**Teknologi:** Spring Boot 3.5.4 + Java 21 + MySQL 8.0