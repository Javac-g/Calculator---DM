# Roadmap

This roadmap tracks planned improvements for Denysov Calculator after the stable v1.0.x release line.

## v1.1.0 — Planned

### Resizable frameless window

Add full free-form resizing while preserving the custom transparent/frameless UI.

Planned behavior:

- resize from all window edges and corners
- preserve custom minimize and close controls
- define sensible minimum width and height
- keep the calculator controls readable at smaller supported sizes
- allow the history panel to consume additional horizontal space
- allow the display and history list to grow vertically
- preserve the existing rounded visual design
- keep the scientific button grid aligned while resizing

Because the application uses a transparent undecorated stage, resizing will require custom edge/corner hit zones and manual stage dimension updates.

### General n-th root support

Extend root functionality beyond square root.

Current support:

- square root: sqrt(x)

Planned support:

- cube root
- fourth root
- arbitrary n-th root

Target mathematical form:

- n√x
- root(n, x)

Examples:

- 3√27 = 3
- 4√16 = 2
- 5√32 = 2

Implementation goals:

- add native parser support instead of treating roots as display-only shortcuts
- preserve meaningful expression history
- validate invalid domains explicitly
- define behavior for negative radicands with odd integer roots
- reject unsupported real-domain cases cleanly
- add automated precedence and domain tests
- add a UI control that does not overcrowd the existing scientific layout

## Later candidates

These are not committed to v1.1.0 yet:

- contextual percentage behavior
- additional inverse trigonometric functions
- optional persistent history
- application settings such as sound enable/disable
- installer icons and additional release polish
