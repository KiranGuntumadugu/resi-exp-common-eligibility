# Residential Experience Oneview Common Layer Template

Spring Boot microservice template for Residential Experience Oneview Common Layer. Provides APIs and utilities to support Domain Services

---


### Key Features
- Utility classes for JSON and String operations
- Spring Boot, Feign, and Caching support
- Designed for scalability and maintainability

## Project Structure
- **api/**: Main application code, controllers, configuration, and utilities
- **common/**: Shared logic and dependencies
- **client/**: Client interfaces for external service communication

## Dependencies
### Local Development
- Java 21 or higher (ensure JAVA_HOME is set)
- Gradle (use the version specified by the Gradle wrapper, or latest if not specified)
- Cassandra (for local testing, optional)
- Artifactory access for dependency resolution

## Configuration
Configuration is managed via `application.yml` in `api/src/main/resources`. Update service endpoints, Cassandra, and other properties as needed for your environment.

## Build & Run
```sh
# Build the project
./gradlew clean build

# Run the application
./gradlew :api:bootRun
```

_Last updated: February 13, 2026_
