---
name: android-test
description: Writes tests that prove GPS da Vida acceptance criteria, especially the planning engine and time-dependent behavior. Use when adding JVM unit tests, fake Clock, or targeted Compose/UI tests. Do not add tests that only compile the app.
disable-model-invocation: true
---

# Testes

Prove acceptance criteria. Prefer JVM tests on domain.

## Scope

Every issue with checkboxes. Highest value: #9–#12, #16 (engine), then #15 (completion persistence).

## Rules

- Fake/fixed `Clock` for delay and reschedule
- Domain tests on JVM; no Android emulator required for engine rules
- UI tests only for critical paths (Agora one-tap complete, Meu Dia timeline)
- Name tests after the criterion they lock
- Do not ship tests whose only assertion is `assertNotNull(activity)` or “compiles”

## Deliverables

- Unit tests next to or under `src/test` mirroring domain packages
- Fakes for repositories when testing use cases

## Stop and ask the architect if

- Time or randomness is not injectable
- You would add a heavy instrumentation suite before #18 exists
- A new skill seems required — propose it; do not create it
