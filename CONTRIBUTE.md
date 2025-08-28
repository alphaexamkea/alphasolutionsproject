# Contributing Guide

Simpel guide til udvikling på Alpha Solutions projektet.

## Quick Setup

```bash
# Clone og start
git clone https://github.com/alphaexamkea/alphasolutionsproject.git
cd alphasolutionsproject
mvn spring-boot:run
```

## Struktur

```
src/main/java/com/kea/alphasolutions/
├── controller/     # HTTP endpoints
├── service/        # Business logic  
├── repository/     # Database access
├── model/          # Data entities
└── rowmapper/      # SQL mapping
```

## Kodestandarder

### Repository (Database)
```java
@Repository
public class ProjectRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public List<Project> findAll() { /* SQL query */ }
    public void save(Project project) { /* Insert/Update */ }
}
```

### Service (Business Logic)
```java
@Service
public class ProjectService {
    private final ProjectRepository repository;
    
    public void createProject(Project project) {
        repository.save(project);
    }
}
```

### Controller (Web)
```java
@Controller
public class ProjectController {
    @GetMapping("/projects")
    public String listProjects(Model model) {
        model.addAttribute("projects", service.getAllProjects());
        return "project/list";
    }
}
```

## Testing

```bash
mvn test              # Kør alle tests
mvn spring-boot:run   # Start applikation
```

## Git Workflow

```bash
# Ny feature
git checkout -b feature/min-feature
# Commit med beskrivende besked
git commit -m "[US01] Add project creation"
# Push og create pull request
```

## Login til test
- **Admin:** username: `admin`, password: `password123`
- **User:** username: `user`, password: `password123`