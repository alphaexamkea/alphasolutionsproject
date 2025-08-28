# Alpha Solutions - Projektkalkulationsværktøj

KEA Datamatiker eksamensprojekt - webbaseret projektkalkulationsværktøj til tidsregistrering og økonomi.

## 🚀 Live Demo

**Live version:** [https://alphasolutionsproject-crfbagcae6f8drdc.westeurope-01.azurewebsites.net/](https://alphasolutionsproject-crfbagcae6f8drdc.westeurope-01.azurewebsites.net/)

**Login:**
- Admin: `admin` / `password123`
- User: `user` / `password123`

## ⚡ Quick Start

```bash
git clone https://github.com/alphaexamkea/alphasolutionsproject.git
cd alphasolutionsproject
mvn spring-boot:run
```

Åbn http://localhost:8080

## 🛠️ Tech Stack

- **Java 21** + **Spring Boot 3.5.4**
- **MySQL** (production) / **H2** (development) 
- **Thymeleaf** templates
- **Maven** build
- **Azure** deployment

## 🎯 Features

- **Hierarkisk struktur**: Project → Subproject → Task → TimeRegistration
- **Rollebaseret adgang**: Admin/User permissions
- **Automatisk tidsaggregering** op gennem hierarkiet
- **Status tracking**: TO_DO → IN_PROGRESS → COMPLETED
- **Responsive design**: Mobile + desktop

## 📁 Projekt Struktur

```
src/main/java/com/kea/alphasolutions/
├── controller/     # HTTP endpoints (6 controllers)
├── service/        # Business logic (6 services) 
├── repository/     # Database access (JdbcTemplate)
├── model/          # Domain entities (5 models)
├── rowmapper/      # SQL → Object mapping
└── util/           # Authentication utilities

src/main/resources/
├── templates/      # Thymeleaf HTML
├── static/         # CSS, images  
├── schema.sql      # Database schema
└── application.properties
```

## 🧪 Testing

```bash
mvn test          # Kør 47 tests
mvn clean test    # Fresh test run
```

Tests inkluderer repository integration tests, controller tests og end-to-end authentication tests.

## 🔐 Authentication

- **ADMIN**: Fuld CRUD adgang til projekter, subprojects, tasks og tidsregistreringer
- **USER**: Læse egne projekter og registrere arbejdstid

## 🚀 Deployment

Automatisk deployment til Azure via GitHub Actions når der pushes til `main` branch.

---

**KEA Datamatiker F2025 - Christian Vinther**