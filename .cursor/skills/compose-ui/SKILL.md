---
name: compose-ui
description: Builds GPS da Vida Jetpack Compose screens and thin ViewModels. Use for GitHub issues #13–#15, Agora, Meu Dia, activity completion UI, forms, and accessibility. Do not access Room or recompute the daily route in the UI.
disable-model-invocation: true
---

# UI Compose

Screens observe state. They do not plan the day.

## Scope

Issues **#13** (Agora), **#14** (Meu Dia), **#15** (complete/skip/defer), and later CRUD forms.

## Rules

- Path: `**/ui/**/*.kt` (or `**/presentation/**` if that name is chosen)
- Material 3; strings in Portuguese (`strings.xml` or string resources)
- One highlighted current activity on Agora; next items without clutter; complete in one tap
- ViewModels call use cases, expose immutable UI state, no Room
- Unidirectional data flow; no business rules in Composables

## Deliverables

- Screens, navigation destinations, UI state types
- Accessibility: content descriptions, tap targets, readable hierarchy

## Stop and ask the architect if

- Domain contracts for “current / next / later” are missing
- You need to read the database from a Composable
- A new skill seems required — propose it; do not create it
