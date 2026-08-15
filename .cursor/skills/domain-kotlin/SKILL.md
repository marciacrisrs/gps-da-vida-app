---
name: domain-kotlin
description: Implements GPS da Vida domain model and planning engine in pure Kotlin. Use for GitHub issues #1, #9–#12, #16, entities, use cases, priorities, duration, schedule generation, or rescheduling rules.
disable-model-invocation: true
---

# Domínio Kotlin

Pure Kotlin only. No Android, Compose, Room, Resources, or Context.

## Scope

Issues **#1** (model), **#9–#12** (planning rules, day agenda, “what now”), **#16** (delay/reschedule). Also domain types behind #2–#8 (event, task, habit, routine, availability, priority, duration).

## Rules

- Domain folder: `**/domain/**/*.kt` (once the project exists)
- Time via injected `Clock` / `Instant` — never `System.currentTimeMillis()` or `LocalDate.now()` without a clock
- Fixed events preserved; flexible items may move; required items are not auto-dropped
- Planned vs actual duration is first-class
- Use cases are the only entry for UI/data

## Deliverables

- Entities, value objects, use cases
- JVM unit tests for every acceptance criterion you implement

## Stop and ask the architect if

- You need Android types, Room, or UI state
- The change would contradict [docs/adr/001-arquitetura-inicial.md](../../../docs/adr/001-arquitetura-inicial.md)
- A new skill seems required — propose it; do not create it
