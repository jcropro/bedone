# Contributing to Ember Audio Player

Welcome to Ember Audio Player! This document outlines our development process and quality standards.

## 🎯 One Milestone Only Rule

**CRITICAL**: Each implementation session focuses on **one milestone only**. Do not claim completion until the **Acceptance Checklist** is met for that specific milestone.

### Why This Rule Exists
- Ensures focused, high-quality implementations
- Prevents scope creep and incomplete features
- Maintains consistent quality standards
- Enables proper testing and validation

### What This Means
- Complete ALL acceptance criteria before moving to the next milestone
- Do not start new features until current milestone is fully validated
- Each milestone must pass all quality gates (lint, tests, visual verification)

## 📋 Commit Message Format

We use **Conventional Commits** for clear, consistent commit messages:

```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process, tooling, or maintenance tasks

### Examples
```
feat(onboarding): add permission flow with privacy rationale
fix(media3): resolve UnsafeOptInUsageError in SplashActivity
docs(readme): add golden blueprint link
chore(build): add ktlint and detekt configuration
```

## 🚨 Quality Gates

All commits must pass these quality gates:

### 1. Code Quality
```bash
./gradlew ktlintCheck detekt :app:lintDebug
```
- **ktlint**: Kotlin code formatting
- **detekt**: Static code analysis
- **Android Lint**: Android-specific checks

### 2. Media3 Compliance
- **UnsafeOptInUsageError** is treated as ERROR (not baselined)
- Use adapter pattern for unstable APIs (see Golden Blueprint)
- No file-level `@OptIn` annotations

### 3. Testing
- Unit tests for business logic
- Instrumentation tests for UI flows
- All tests must pass

## 🔧 Development Setup

### Pre-commit Hook
Install the pre-commit hook to automatically run quality gates:

```bash
# Copy the hook to git hooks directory
cp tools/hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

If `.git/hooks/` is not available (cloud environments), the hook content is available in `tools/hooks/pre-commit` with instructions.

### Running Quality Gates Manually
```bash
# Run all quality gates
./gradlew ktlintCheck detekt :app:lintDebug

# Run individual checks
./gradlew ktlintCheck
./gradlew detekt
./gradlew :app:lintDebug
```

## 📖 Golden Blueprint

**CRITICAL**: Always read and follow the [Golden Blueprint](EMBER_GOLDEN_BLUEPRINT.md) before making changes. This document is the single source of truth for:

- Brand system and visual standards
- Motion system and micro-interactions
- Architecture and technical requirements
- Quality standards and acceptance criteria

## 🎨 Visual Standards

Ember must meet or exceed top-tier app standards (Spotify/Apple Music):

- **Brand Consistency**: Ember Orange → Ember Red gradient throughout
- **Motion Quality**: 60fps, no jank, intentional animations
- **Visual Polish**: No rudimentary or placeholder elements
- **Accessibility**: TalkBack, contrast AA+, dynamic type support

## 🚫 What Not to Do

- ❌ Don't use Windows absolute paths
- ❌ Don't baseline UnsafeOptInUsageError
- ❌ Don't claim completion without meeting acceptance criteria
- ❌ Don't implement multiple milestones in one session
- ❌ Don't skip quality gates
- ❌ Don't ignore the Golden Blueprint

## ✅ What to Do

- ✅ Focus on one milestone at a time
- ✅ Follow Conventional Commits format
- ✅ Run quality gates before every commit
- ✅ Read the Golden Blueprint first
- ✅ Use relative paths only
- ✅ Fix Media3 opt-in violations properly
- ✅ Meet all acceptance criteria before claiming completion

## 🆘 Getting Help

- Check the [Golden Blueprint](EMBER_GOLDEN_BLUEPRINT.md) for technical specifications
- Review existing code for patterns and conventions
- Ensure all quality gates pass before asking for help

---

**Remember**: Quality over speed. One milestone done perfectly is better than multiple milestones done poorly.
