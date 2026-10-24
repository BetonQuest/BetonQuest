# BetonQuest AI guide
This file is intended for AI agents.
It is the entry point for project-specific guidance.
Read only the linked documents relevant to the current task.

## Prerequisites & Environment
BetonQuest is a Minecraft plugin for servers implementing the PaperMC-API. It is written in Java using
a multi-module Maven project.

Java: Java 25 (minimum Java 17/21 bytecode target compatibility across modules, but development and CI target Java 25 LTS).
Build Tool: Apache Maven Wrapper (./mvnw on Unix/macOS, .\mvnw.cmd on Windows).
Operating System / Shell: Cross-platform (the commands below use Maven wrapper).

## Multi-Module Project Structure

- code/api: Public API definitions, events, and interfaces.
- code/lib: Shared core utility classes, config management, and parsers.
- code/core: Main BetonQuest logic, command handling, quests, menus, and scheduling.
- code/mc_1_20_6, code/mc_1_21_4, code/mc_1_21_8: Version-specific Minecraft / Paper server adapters.
- code/compatibility: Third-party plugin integrations (e.g., AuraSkills, MythicMobs, Vault, etc.).
- code/build: Shading, packaging, and producing the final BetonQuest.jar.
- docs/: User and developer documentation

## Before changing code

- Read and follow the relevant instructions in docs/Participate/Process/Code

- Inspect neighboring production code and its tests.
- Follow existing code conventions instead of inventing new patterns.
- Search for all implementations and callers of changed contracts.
- Do not introduce dependencies that violate module boundaries.
- Preserve compatibility across supported Minecraft versions.
- Prompt the user before changing API contracts.

- Check docs/Participate/Process/Code/Writing-JUnit-Tests.md to check whether tests are required for these changes.

## Before writing code tests

- See docs/Participate/Process/Code/Writing-JUnit-Tests.md for detailed testing guidelines.

Used Test Frameworks & Libraries:

- JUnit 5 (Jupiter): Standard test framework (org.junit.jupiter.api.*).
- Mockito: Mocking framework for interfaces and classes.
- MockBukkit / BukkitSchedulerMock: Custom and Bukkit mocking helpers for tick-based scheduler testing.
- BetonQuest Logger Extensions: BetonQuestLoggerExtension and LogValidator for verifying logged messages.

## Validation of code changes

- You must use docs/Participate/Process/Code/Checking-Requirements.md to validate any code changes.

### Interpreting validation output

- Do not diagnose a failed validation using only the final lines of Maven output.
- Preserve and inspect the complete command output.
- Locate the first relevant `[ERROR]`, `FAILURE`, compilation error, test
  failure, or plugin violation, then read enough surrounding context to
  understand its cause.
- Inspect generated reports such as `target/surefire-reports` when console
  output does not contain the complete failure.
- A short `tail` excerpt may confirm a successful build or show the reactor
  summary, but it must not be the sole evidence used to diagnose failure.
- Report the failing module, Maven goal, affected file or test, and primary
  diagnostic—not merely `BUILD FAILURE`.

## Before changing docs

- Read and follow the relevant resources in docs/Participate/Process/Docs

## Validation of docs changes

- Check if there is a virtual python environment in the project root folder (commonly a directory named .venv).
  - If present, enter the virtual python environment.
- Run `mkdocs build` and check the output for any errors or warnings.
- Additionally, make sure all requirements in docs/Participate/Process/Docs were followed.
