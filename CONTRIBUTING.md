# Contributing to Memoize

Thank you for considering contributing to Memoize! This document explains how to get started.

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.9+
- Git

### Building the Project

```bash
git clone https://github.com/dakshverma2411/memoize.git
cd memoize
mvn clean verify
```

### Running Tests

```bash
mvn test
```

To run tests for a specific module:

```bash
mvn test -pl memoize-core
```

## Development Workflow

1. Fork the repository
2. Create a feature branch from `main`:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Make your changes
4. Write or update tests
5. Ensure all tests pass: `mvn clean verify`
6. Commit with a clear message
7. Push to your fork and open a Pull Request

### Branch Naming

```text
feature/description    - New features
fix/description        - Bug fixes
docs/description       - Documentation changes
refactor/description   - Code refactoring
```

## Coding Standards

### Style

- Follow existing code style in the project
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- All public classes and methods must have Javadoc

### Tests

- Write unit tests for all new functionality
- Maintain or improve code coverage
- Use JUnit 5 and Mockito for testing
- Test class naming: `{ClassName}Test.java`

### Commits

- Use clear, concise commit messages
- Reference issue numbers where applicable: `Fix #123: description`
- Keep commits focused on a single change

## Submitting a Pull Request

1. Ensure your branch is up to date with `main`
2. All CI checks must pass
3. Add a description of what your PR does and why
4. Link any related issues
5. Request a review

### PR Checklist

- [ ] Tests added/updated
- [ ] Javadoc added for public APIs
- [ ] No warnings introduced
- [ ] `mvn clean verify` passes locally

## Reporting Issues

- Use GitHub Issues for bug reports and feature requests
- Check existing issues before creating a new one
- Use the provided issue templates

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## License

By contributing to Memoize, you agree that your contributions will be licensed under the Apache License 2.0.
