---
name: android-quality
description: Sets lint, Detekt, and CI quality gates for GPS da Vida. Use at the end of a work batch, for GitHub Actions, release hygiene, or static analysis. Do not change product behavior.
disable-model-invocation: true
---

# Qualidade e CI

Gates only. No product behavior changes.

## When

After a vertical slice (architecture/base, then later engine/UI). Not before **#18** has a real Gradle project.

## Rules

- Android Lint + Detekt (or ktlint if architect already chose)
- CI: compile, unit tests, lint on `main` / PRs
- Do not “fix” architecture by moving code across layers; report and hand back
- No Play Console / signing work unless Márcia asks

## Deliverables

- Gradle quality tasks, GitHub Actions workflow, documented local commands

## Stop and ask the architect if

- Tooling would force a module split or new plugin stack
- A new skill seems required — propose it; do not create it
