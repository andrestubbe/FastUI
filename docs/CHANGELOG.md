# Changelog

All notable changes to this project will be documented in this file.

## [0.1.1] - 2026-07-06

### Added
- Doubly-linked tree architecture (`firstChild`, `lastChild`, `nextSibling`, `prevSibling`) for allocation-free z-order traversal.
- Stateless layout primitives engine (`LinearLayout`, `SplitLayout`, `StackLayout`, `NoneLayout`).
- Generic layout parameters: `layoutA` (divider), `layoutB` (gap/spacing), and `layoutFlags` (orientation/axes/anchors).

### Removed
- Obsolete layout wrappers: `WorkspaceLayout`
- Obsolete custom layout wrappers: `TopPanelLayout`, `TextPanelLayout`
- Obsolete classes: `Stage`, `Spatial`

## [0.1.0] - 2026-05-23

### Added
- Initial release
- Standardized FastJava ecosystem module