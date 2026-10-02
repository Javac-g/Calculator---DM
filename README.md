# Denysov Calculator

A modern desktop scientific calculator built with Java 21 and JavaFX.

Denysov Calculator combines a custom expression parser with a focused desktop UI, scientific functions, calculation history, editable tags, export support, keyboard input, and subtle audio feedback.

## Features

- Standard arithmetic with precedence and nested parentheses
- Right-associative exponentiation
- Integer and fractional powers
- Square root, reciprocal, and square
- sin, cos, and tan
- DEG and RAD angle modes
- Constants pi and e
- Natural logarithm and base-10 logarithm
- Absolute value and factorial
- Calculation history with editable tags
- History export to TXT, LOG, and DAT
- Keyboard input
- Custom frameless JavaFX interface
- Subtle UI click and equals sounds
- Automated expression-engine tests
- GitHub Actions CI
- Native Windows and Ubuntu packaging

## Technology

- Java 21
- JavaFX 21
- Gradle 8.14.2
- JUnit 6
- GitHub Actions
- jpackage

## Project Structure

    src/
    ├── main/
    │   ├── java/com/denysov/calculator/
    │   └── resources/
    └── test/
        └── java/com/denysov/calculator/

## Run Locally

Requirements: JDK 21 and Gradle 8.14.2 or compatible Gradle 8.x.

    gradle run

## Tests

    gradle clean test

## Build

    gradle clean build

## Native Packages

Native packages are produced by GitHub Actions when a version tag such as v1.0.0 is pushed.

Release artifacts:

- Windows x64 EXE installer
- Ubuntu/Linux x64 DEB package

The application is packaged with jpackage, so end users do not need to install a separate Java runtime.

## Release Process

1. Ensure main passes CI.
2. Update CHANGELOG.md.
3. Update the Gradle version if required.
4. Create and push an annotated version tag:

    git tag -a v1.0.0 -m "Denysov Calculator v1.0.0"
    git push origin v1.0.0

5. GitHub Actions builds the native packages and publishes the GitHub Release.

## Security

See SECURITY.md.

## Contributing

See CONTRIBUTING.md.

## License

MIT. See LICENSE.
