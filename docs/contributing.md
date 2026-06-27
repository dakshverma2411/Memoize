---
layout: default
title: Contributing
nav_order: 10
---

# Contributing

Contributions to Memoize are welcome. Please read the full [Contributing Guide](https://github.com/dakshverma2411/memoize/blob/main/CONTRIBUTING.md) before submitting a pull request.

## Quick Summary

### Prerequisites

- Java 17 or later
- Maven 3.9+
- Git

### Build and Test

```bash
git clone https://github.com/dakshverma2411/memoize.git
cd memoize
mvn clean verify
```

### Development Workflow

1. Fork the repository
2. Create a feature branch from `main` (`feature/your-feature`, `fix/your-fix`, etc.)
3. Make your changes and write tests
4. Ensure all tests pass: `mvn clean verify`
5. Commit with a clear message
6. Push to your fork and open a Pull Request

### PR Checklist

- Tests added or updated
- Javadoc added for public APIs
- No warnings introduced
- `mvn clean verify` passes locally

### Coding Standards

- 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- All public classes and methods must have Javadoc
- JUnit 5 and Mockito for testing

## Reporting Issues

Use [GitHub Issues](https://github.com/dakshverma2411/memoize/issues) for bug reports and feature requests. Check existing issues before creating a new one.

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](https://github.com/dakshverma2411/memoize/blob/main/CODE_OF_CONDUCT.md).

## License

By contributing to Memoize, you agree that your contributions will be licensed under the [Apache License 2.0](https://github.com/dakshverma2411/memoize/blob/main/LICENSE).
