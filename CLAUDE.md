# CLAUDE.md - Admin-API

## Project Overview

AMRIT Admin-API is a Spring Boot microservice that provides administrative management capabilities for the AMRIT healthcare platform. It handles user management, role management, permission management, provider onboarding, location masters, inventory configuration, telemedicine setup, and various master data operations.

## Tech Stack

- Java 17
- Spring Boot 3.2.2
- Spring Data JPA / Hibernate
- MySQL 8.0
- Redis (session management)
- Maven (build tool)
- Swagger/OpenAPI (API documentation)
- Lombok, MapStruct
- WAR packaging (deploys to Wildfly)

## Build and Run

```bash
# Build
mvn clean install -DENV_VAR=local

# Run locally
mvn spring-boot:run -DENV_VAR=local

# Package WAR for specific environment
mvn -B package --file pom.xml -P <profile>   # profiles: dev, local, test, ci, uat

# Run tests
mvn test
```

### Configuration

- Copy `src/main/environment/admin_example.properties` to `admin_local.properties` and edit accordingly.
- Environment is selected via `-DENV_VAR=<env>` (local, dev, test, uat).
- Swagger UI: `http://localhost:8082/swagger-ui.html`

## Package Structure

Base package: `com.iemr.admin`

| Layer | Package | Description |
|-------|---------|-------------|
| Controllers | `controller.*` | REST endpoints (40+ sub-packages) |
| Services | `service.*` | Business logic |
| Repositories | `repository.*`, `repo.*` | JPA repositories (split across two naming conventions) |
| Entities | `data.*` | JPA entity classes |
| DTOs/TOs | `to.*`, `model.*` | Transfer objects |
| Mappers | `mapper.*` | MapStruct mappers |
| Config | `config` | Swagger, interceptors |
| Utils | `utils.*` | Redis, HTTP, validation, session, exception handling |
| AOP | `aspectj.apiman` | API management aspects |

## Key Functional Domains

- **Employee/User Management**: `employeemaster`, `user` - CRUD for system users
- **Role Management**: `rolemaster` - role and permission configuration
- **Provider Onboarding**: `provideronboard` - service provider setup
- **Location Masters**: `locationmaster`, `zonemaster`, `villageMaster` - geographic hierarchy
- **Inventory Config**: `store`, `item`, `itemfacilitymapping`, `supplier`, `manufacturer`, `stockEntry`, `stockExit`, `uom`
- **Telemedicine**: `telemedicine` - video consultation and specialist mapping
- **Van/Service Points**: `vanMaster`, `servicePoint`, `parkingPlace`, `vanServicePointMapping`, `vanSpokeMapping`
- **Lab Module**: `labmodule` - procedure and component masters
- **Blocking**: `blocking` - user/provider blocking
- **Questionnaire**: `questionnaire` - configurable questionnaires

## Architecture Notes

- Entry point: `RoleMasterApplication.java`
- Standard layered architecture: Controller -> Service -> Repository -> Entity
- HTTP interceptor (`utils.http`) for auth token forwarding
- Redis-based session management (`utils.redis`)
- Centralized exception handling (`exceptionhandler`)
- Two repository package naming conventions exist: `repo.*` and `repository.*` (historical inconsistency)

## CI/CD

- GitHub Actions: `package.yml`, `build-on-pull-request.yml`, `sast.yml`, `commit-lint.yml`
- Conventional Commits enforced via Husky + commitlint
- Checkstyle configuration in `checkstyle.xml`
- Dockerfile available for containerized deployment
