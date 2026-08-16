# Theme package

The theme package is the single source of truth for the GPS da Vida visual language.

- `Color.kt`: semantic palette
- `Type.kt`: typography
- `Shape.kt`: shapes and spacing
- `GpsDaVidaTheme.kt`: Material 3 theme composition

Screens should consume `MaterialTheme.colorScheme`, `MaterialTheme.typography`, `MaterialTheme.shapes` and `GpsDaVidaSpacing` rather than introducing local visual constants.