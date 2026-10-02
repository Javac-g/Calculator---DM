# Changelog

All notable changes to this project are documented in this file.

The project follows Semantic Versioning.

## [1.0.1] - 2026-10-02

### Fixed

- Fixed native packaged launchers exiting immediately on Windows by introducing a dedicated non-JavaFX bootstrap class for jpackage.
- Updated native Windows and Ubuntu packaging to launch through the bootstrap class.

## [1.0.0] - 2026-10-02

### Added

- Java 21 and JavaFX desktop application
- Custom frameless calculator interface
- Expression parser with precedence and nested parentheses
- Right-associative exponentiation
- Integer and fractional powers
- Square root, reciprocal, and square operations
- DEG/RAD trigonometric modes
- sin, cos, and tan
- pi and e constants
- Natural and base-10 logarithms
- Absolute value and factorial
- Calculation history and editable tags
- TXT, LOG, and DAT history export
- Keyboard support
- UI sound feedback
- Expression-engine automated tests
- GitHub Actions CI
- Native Windows and Ubuntu release packaging
