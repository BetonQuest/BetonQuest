# Null Safety (Critical)
- **NullAway:** This project uses `NullAway` for static analysis.
- **Annotations:** Use `@Nullable` from `org.jetbrains.annotations`.
- **Requirements:**
  - Every parameter and return type that can be `null` MUST be explicitly marked with `@Nullable`.
  - Assume non-null by default.
