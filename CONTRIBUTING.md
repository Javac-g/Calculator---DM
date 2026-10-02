# Contributing

Contributions are welcome.

## Development Requirements

- JDK 21
- Gradle 8.14.2 or compatible Gradle 8.x

## Workflow

1. Fork the repository.
2. Create a focused feature or fix branch.
3. Keep changes scoped and readable.
4. Add or update tests when calculator behavior changes.
5. Run the full test suite with: gradle clean test
6. Open a pull request against main.

## Code Expectations

- Preserve the existing package structure.
- Keep calculation logic out of the JavaFX view layer.
- Prefer clear, explicit Java over clever abstractions.
- Preserve BigDecimal for exact arithmetic where practical.
- Use tolerance-based tests for transcendental calculations.
- Do not commit build outputs, IDE metadata, or local runtime files.

## Pull Requests

Explain what changed, why it changed, how it was tested, and any UI or behavior changes.
