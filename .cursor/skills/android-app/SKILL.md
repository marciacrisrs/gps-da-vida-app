---
name: android-app
description: Wires the GPS da Vida Android app (Gradle, Hilt, Room, Navigation, Application). Use for GitHub issue #18, persistence, DAOs, mappers, DI, and connecting domain use cases to the app without putting planning rules in the UI.
disable-model-invocation: true
---

# App Android

You own scaffolding and persistence wiring. Planning rules stay in domain.

## Scope

GitHub **#18** (base structure) and data layer for #2–#8, #15 (persisted completion).

## Rules

- Packages: `domain` / `data` / `ui` under one `:app` until the architect says otherwise
- Room maps to/from domain models; DAOs do not leak into Compose
- Hilt provides use cases, repositories, `Clock`
- Navigation Compose for Agora, Meu Dia, and CRUD later
- No priority/agenda recalculation in ViewModels beyond calling use cases

## Deliverables

- App that starts, DI graph, Room DB, navigation host
- Mappers and repositories implementing domain ports

## Stop and ask the architect if

- You would diverge from [docs/adr/001-arquitetura-inicial.md](../../../docs/adr/001-arquitetura-inicial.md)
- You want a second Gradle module, DataStore vs Room split, or WorkManager
- A new skill seems required — propose it; do not create it
