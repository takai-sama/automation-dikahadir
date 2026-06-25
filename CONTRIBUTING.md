# Contributing Guide

Thank you for contributing to this project. This document covers Git workflow, branching strategy, commit conventions, and best practices.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Git Workflow](#git-workflow)
3. [Branching Strategy](#branching-strategy)
4. [Commit Message Convention](#commit-message-convention)
5. [Pull Request Process](#pull-request-process)
6. [Code Review Guidelines](#code-review-guidelines)
7. [Best Practices](#best-practices)
8. [Troubleshooting](#troubleshooting)

---

## Getting Started

### 1. Clone or Fork

```bash
git clone https://github.com/SQA-JuaraCoding28/automation-dikahadir.git
cd automation-dikahadir
```

---

## Git Workflow

### Daily Workflow

```bash
# 1. Sync with main
git checkout main
git pull origin main

# 2. Create a feature branch
git checkout -b feature/inventory-page-tests

# 3. Work, commit regularly
git add .
git commit -m "feat(pages): add InventoryPage with sort methods"

# 4. Push
git push -u origin feature/inventory-page-tests

# 5. Open a Pull Request on GitHub

# 6. After merge, clean up
git checkout main
git pull origin main
git branch -d feature/inventory-page-tests
```

---

## Branching Strategy

| Prefix | Purpose | Example |
|--------|---------|---------|
| `main` | Always deployable | `main` |
| `feature/*` | New features | `feature/cart-page-tests` |
| `bugfix/*` | Bug fixes | `bugfix/login-flaky-assertion` |
| `hotfix/*` | Critical fixes | `hotfix/driver-quit-nullpointer` |
| `docs/*` | Documentation | `docs/update-contributing` |
| `refactor/*` | Restructuring | `refactor/basepage-wait-methods` |
| `test/*` | Test additions | `test/add-checkout-scenarios` |

### Rules

- Lowercase with hyphens only
- Max 50 characters
- Be descriptive: `bugfix/#42-login-error-message` not `fix-bug`

---

## Commit Message Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/).

### Format

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Formatting, no logic change |
| `refactor` | Restructuring, no feature/fix |
| `perf` | Performance improvement |
| `test` | Adding or fixing tests |
| `chore` | Build, deps, tooling |
| `ci` | CI/CD config changes |

### Scopes

`login`, `pages`, `steps`, `config`, `deps`, `ci`, `hooks`

### Examples

```bash
feat(login): add visual_user login scenario
fix(pages): resolve stale element in BasePage.getText
docs: add NPE troubleshooting to README
test(login): add case-sensitivity scenarios
refactor(steps): use lazy page initialization pattern
chore(deps): bump selenium to 4.44.0
```

---

## WIP Tag

Tag any in-progress scenario with `@wip` — it will be automatically excluded
from CI runs by the `not @wip` filter in TestRunner.

```gherkin
@wip
Scenario: Checkout flow (in progress)
  ...
```

Remove `@wip` when the scenario is complete and the step definitions are implemented.

---

## Pull Request Process

### Before Opening a PR

```bash
# Rebase onto latest main
git fetch origin
git rebase origin/main

# Run full suite
mvn clean test

# Check nothing unintended is staged
git status
git diff --cached
```

### PR Checklist

- [ ] All tests pass locally (`mvn clean test`)
- [ ] No credentials, passwords, or API keys in any file
- [ ] No raw passwords in `.feature` files — use role-based steps
- [ ] Commit messages follow Conventional Commits
- [ ] Branch is rebased onto latest `main`
- [ ] `@wip` removed from completed scenarios
- [ ] `test-data.properties` is the only place test data values live
- [ ] README updated if behavior changed

---

## Code Review Guidelines

### As an Author

- Keep PRs small and focused (under 400 lines changed)
- Respond to all comments
- Don't take feedback personally — it's about the code

### As a Reviewer

Use these prefixes to set expectations:

- `must:` — blocking, must be fixed before merge
- `should:` — strong recommendation
- `nit:` — optional polish
- `question:` — clarification, not necessarily a change

Review within 24 hours. Don't hold PRs hostage for minor issues.

---

## Best Practices

### Configuration

- Runtime config (browser, timeouts, base URL) → `config.properties`
- Test data (usernames, error messages, form values) → `test-data.properties`
- Secrets (passwords, API keys) → environment variables or a vault; never committed
- Do not duplicate values across Java constants and properties files

### Page Objects

- Every page has one class in `pages/`
- Locators stored as `private final By` fields
- Methods return `this` for fluent chaining
- No assertions inside page objects — those belong in step definitions
- Never use `Thread.sleep()` — use `WebDriverWait` with `ExpectedConditions`

### Step Definitions

- Create page objects inside step methods, not in the step definition constructor (prevents NPE before `@Before` fires)
- One assertion per `@Then` step where possible
- Step text should describe behavior, not implementation: "user logs in with valid credentials", not "user enters standard_user into the username field"

### Gherkin

- Declarative over imperative: describe what, not how
- No raw passwords or secrets inline in `.feature` files
- Use `Background` for shared preconditions
- Use `Scenario Outline` for data-driven tests
- Tag with `@smoke`, `@positive`, `@negative`, `@regression` for selective execution
- Tag work in progress with `@wip`

---

## Troubleshooting

### Committed to the wrong branch

```bash
git stash
git checkout correct-branch
git stash pop
git add .
git commit -m "..."
```

### Undo the last commit (keep changes)

```bash
git reset --soft HEAD~1
```

### Branch diverged from main

```bash
git fetch origin
git rebase origin/main
```

### Merge conflicts during rebase

```bash
# Edit conflicting files, then:
git add <resolved-files>
git rebase --continue

# To abort entirely:
git rebase --abort
```

---

## Questions?

Open an issue with the `question` label, or reach out to the maintainers.
