# Theme package

The theme package is the single source of truth for the GPS da Vida visual language.

- `Color.kt`: semantic palette
- `Type.kt`: typography
- `Shape.kt`: shapes and spacing
- `GpsDaVidaTheme.kt`: Material 3 theme composition
- `SuperPlannerComponents.kt`: reusable editorial primitives
- `SuperPlannerLogo.kt`: official Super Planner brand mark

## Editorial primitives

Use `SuperPlannerSectionHeader` for section hierarchy instead of ad-hoc title rows. Keep supporting text optional and use the action only when it is genuinely useful.

Use `SuperPlannerProgress` for low-density progress feedback. It accepts a normalized value from `0f` to `1f`, clamps invalid values, and exposes progress semantics for accessibility.

Screens should consume `MaterialTheme.colorScheme`, `MaterialTheme.typography`, `MaterialTheme.shapes`, `GpsDaVidaSpacing` and the Super Planner primitives rather than introducing local visual constants.