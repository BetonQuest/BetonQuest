## 1. Project Essence
- **Core Purpose:** Advanced RPG Quest Scripting Plugin for Minecraft.
- **Key Tech Stack:** Java (Maven), Paper API, CI/CD (GitHub Actions), MkDocs (Documentation).
- **Additional Verification:** Checkstyle, PMD, NullAway, Qodana, SpotBugs, EditorConfig.

## 2. Module Map
- `code/api`: Stable interfaces for third-party developers.
- `code/core`: Main logic, event handling, and data management.
- `code/lib`: Shared utilities and internal abstractions. Default implementations for api interfaces.
- `code/compatibility`: Multiple integrations with other plugins.
- `code/mc_*`: Version-specific implementations for different Minecraft versions.
- `code/build`: The parent module to build the project.

### 2.1. Module Dependencies
- `code/lib` depends on `code/api`.
- `code/core` depends on `code/lib`.
- `code/compatibility` depends on `code/core`.
- `code/mc_*` depends on `code/core`.
- `code/build` depends on all other code modules.

## 3. Entry Points

### 3.1. Implementation
- `BetonQuest.java` (`code/core`): The main entry point for the plugin.
- `QuestManager.java` (`code/core`): Responsible for loading and managing quest packages.
- `BetonQuestComponents.java` (`code/core`): Provides access to a component list of the plugin.
- `IntegrationManager.java` (`code/core`): Manages integration with other plugins.

### 3.2. API
- `BetonQuestApi.java` (`code/api`): The public API for third-party developers.
- `CoreComponentLoader.java` (`code/api`): Provides a component system for the plugin.
- `BetonQuestLogger.java` (`code/api`): Provides logging functionality.
- `CoreQuestRegistry.java` (`code/api`): Fundamental registry for quest elements.
- `ProfileProvider.java` (`code/api`): Provides access to profiles.
- `Instruction.java` (`code/api`): Essential interface for all script instructions.

### 3.3. Documentation & Misc
- `docs/`: The entire documentation and reference material.
- `docs/Documentation/Reference/`: The reference documentation.
- `docs/Participate/Process/Docs/Style-Definitions.md`: The style guide for documentation.
- `docs/Participate/Process`: How to contribute to the project.
- `docs/Participate/Process/AI-Usage.md`: Guidelines for AI usage.
- `mkdocs.yml`: The configuration file for MkDocs.
- `.github/`: GitHub Actions workflows and configuration.
- `CHANGELOG.md`: A user-facing log of changes to the project.
- `API-CHANGELOG.md`: A developer-facing log of changes to the public API.

### 4. Testing & Quality
- **Frameworks:** JUnit 5 (Unit Tests), Mockito (Mocking).
- **Conventions:** Test methods are named using `snake_case`. Integration tests are suffixed with `IT`.
- **Utilities:**
  - `BetonQuestLoggerExtension`: Used for testing log outputs.
  - `BukkitSchedulerMock`: Simulates server ticks and asynchronous tasks.
  - `@VisibleForTesting` (JetBrains): Marks package-private elements that are accessible for testing purposes only.
- **Verification:** Make sure the project passes all checks before committing.
  - Code changes can be verified locally using the maven verify goal.
  - Documentation changes can be verified using the mkdocs build command.

## 5. Don'ts
- **DO NOT** add comments to code. Either use Javadocs where applicable or just don't.
- **DO NOT** add dependencies to the project.
- **DO NOT** use the singleton pattern, similar static access methods, and other antipatterns.
- **DO NOT** use features from newer Java versions than the target version.

## 6. Glossary
- **Quest Package:** The primary organizational unit. A folder containing a `package.yml` in `QuestPackages/`. It
  encapsulates all related script files and elements. *Reference:* `docs/Documentation/Basics/Packages-&-Templates.md`
- **Identifier:** A unique name for an element. Format: `package>identifier` for global access or `identifier` for local
  access. Supports relative pathing. *Reference:* `docs/Documentation/Reference/Definition-Encyclopedia.md#identifiers`
- **Element:** A generic term for any definable scripting component, such as Actions, Conditions, Objectives, Items, or
  Conversations. *Reference:* `docs/Documentation/Reference/Definition-Encyclopedia.md#elements`
- **Instruction:** A single-line string defining the logic of an element. It is the core functional unit of the BQ
  scripting language. *Reference:* `docs/Documentation/Reference/Definition-Encyclopedia.md#syntax`
- **Objective:** A goal a player must complete. Objectives must be explicitly started, track progress, and can trigger
  actions upon completion. *Reference:* `docs/Documentation/Basics/Scripting-Elements.md#objectives`
- **Action:** A command that executes an effect. Note: Historically called "Events" in older versions. *Reference:*
  `docs/Documentation/Basics/Scripting-Elements.md#actions`
- **Condition:** A logic check that returns a boolean (`true`/`false`) result to control flow. *Reference:*
  `docs/Documentation/Basics/Scripting-Elements.md#conditions`
- **Point:** A numeric player variable used for counters in general. Can be per profile or global. *Reference:*
  `docs/Documentation/Basics/Scripting-Elements.md#points`
- **Tag:** A persistent string marker to track quest status. Bound to a specific package, personal or global.
  *Reference:* `docs/Documentation/Basics/Scripting-Elements.md#tags`
- **Context:** Defines the execution scope of an element: `online` (player must be logged in), `offline` (player can be
  away), or `independent` (no player required). *Reference:*
  `docs/Documentation/Reference/Definition-Encyclopedia.md#context`
- **Placeholder:** A variable that can be used in an instruction. Note: Historically called "Variables" in older
  versions. *Reference:* `docs/Documentation/Reference/Placeholders-List.md`

## 7. Disclosure
- Always disclose AI usage in pull requests.
