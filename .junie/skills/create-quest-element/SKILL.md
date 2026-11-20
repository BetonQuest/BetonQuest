---
name: create-quest-element
description: Step-by-step process to add any new BetonQuest element (Action, Condition, Objective, Placeholder, Item, NPC, Schedule, ConversationIO, NotifyIO, ...) in any variant (online, player, playerless, nullable/combined) including implementation, factory, registration, CHANGELOG entry, documentation and tests. Use when asked to add, create or implement a new quest element.
---

# Create a Quest Element (element-agnostic)

Placeholders used below:
- `<Kind>` = element kind in PascalCase (`Action`, `Condition`, `Objective`, `Placeholder`, ...), `<kind>` = lowercase.
- `<Name>` = PascalCase element name, `<name>` = lowercase type/package name used in scripts.

Always find the closest existing element of the same kind AND the same variant, copy it, and adapt it:
`grep` for a similar `*Factory.java` under `code/core/src/main/java/org/betonquest/betonquest/quest/<kind>/`.

## Checklist (do all, in order)
1. Decide kind + variant (section 1)
2. Implementation + Factory
3. Registration
4. `package-info.java`
5. CHANGELOG
6. Documentation
7. Verify

## 1. Decide kind and variant

Kinds with the player-variant model (API in `code/api/.../api/quest/<kind>/`):

| Kind        | Online (player must be online) | Player (online or offline) | Playerless              | Nullable (player optional, one impl for both) |
|-------------|--------------------------------|----------------------------|-------------------------|-----------------------------------------------|
| Action      | `OnlineAction`                 | `PlayerAction`             | `PlayerlessAction`      | `NullableAction`                              |
| Condition   | `OnlineCondition`              | `PlayerCondition`          | `PlayerlessCondition`   | `NullableCondition`                           |
| Placeholder | `OnlinePlaceholder`            | `PlayerPlaceholder`        | `PlayerlessPlaceholder` | `NullablePlaceholder`                         |

Factories: `Player<Kind>Factory#parsePlayer(Instruction)` and `Playerless<Kind>Factory#parsePlayerless(Instruction)`.
Adapters (in the same API package) turn the simple interface into the factory return type:
- `Online<Kind>Adapter(OnlineX)` -> returns `Player<Kind>` (online players only).
- `Nullable<Kind>Adapter(NullableX)` -> usable as BOTH `Player<Kind>` and `Playerless<Kind>`.

Pick the variant:
- Needs an online player (inventory, location, ...): `Online` + `PlayerXFactory` + `Online<Kind>Adapter`.
- Needs a profile but works offline (points, tags, ...): `PlayerX` + `PlayerXFactory`.
- Needs no player at all: `PlayerlessX` + `PlayerlessXFactory`.
- Works with or without a player (profile may be `null`): implement `NullableX`, a factory implementing BOTH
  `PlayerXFactory` and `PlayerlessXFactory`, both parse methods delegate to one private method returning
  `new Nullable<Kind>Adapter(...)` (examples: `DoorActionFactory`, `GlobalPointActionFactory`).
  Nullable parameters in logic: `@Nullable Profile profile`; resolve arguments with `getValue(profile)` (profile may be
  `null`).
- Player and playerless with DIFFERENT logic: two separate implementations in one factory implementing both factory
  interfaces.

Kinds with their own model (look at an existing example in the same package, then follow steps 2-8 the same way):
- Objective: `ObjectiveFactory` + `Objective` (`api/quest/objective`), uses `ObjectiveService`; register in
  `ObjectiveTypeComponent`.
- NPC: `NpcFactory`; Item: `QuestItemFactory`; Schedule, ConversationIO, NotifyIO, Interceptor, TextParser: own
  `*Factory` + own `*TypesComponent`.

## 2. Implementation + Factory

Package: `code/core/src/main/java/org/betonquest/betonquest/quest/<kind>/<name>/`
(Plugin integrations: `code/compatibility/<plugin>/...`, registered in that integration instead of the core component.)

Files: `<Name><Kind>` (logic), `<Name><Kind>Factory`, `package-info.java`.

Factory pattern (online example; adapt interfaces/adapter to the chosen variant):
```java
/**
 * Factory for {@link <Name><Kind>}s.
 */
public class <Name><Kind>Factory implements

Player<Kind> Factory {
/**
 * Create the <name> <kind> factory.
 */
public <Name>

<Kind> Factory() {
}

@Override
public Player<Kind> parsePlayer(final Instruction instruction) throws QuestException {
  final Argument<Number> amount = instruction.number().get();
  return new Online<Kind> Adapter(new <Name> < Kind > (amount));
}
}
```
Dependencies (logger factory, storage, managers, ...) go in via constructor, documented with Javadoc `@param`.

Instruction parsing: `instruction.number().get()` (required), `.get("key")` (optional key -> `Optional`),
`.get("key", default)`, `instruction.bool().getFlag("flag", true)`, `instruction.string()`, `instruction.location()`,
`instruction.item()`, `instruction.enumeration(X.class)`, `instruction.packageIdentifier()`, etc.
Errors: throw `QuestException`. Values are `Argument<T>`; resolve with `getValue(profile)` (`null` for
playerless/nullable without profile).

Code rules: no code comments (Javadoc only, also on constructors), all params `final`, `@Nullable` from
`org.jetbrains.annotations` on every nullable param/return (NullAway), no singletons/static access, no new dependencies,
Java target version only.

## 3. Registration

Component directory: `code/core/src/main/java/org/betonquest/betonquest/kernel/component/types/`

| Kind           | Component                      | Registry field / call                       |
|----------------|--------------------------------|---------------------------------------------|
| Action         | `ActionTypesComponent`         | `actionTypes.register(...)`                 |
| Condition      | `ConditionTypesComponent`      | `conditionTypes.register(...)`              |
| Objective      | `ObjectiveTypeComponent`       | `objectiveTypes.register(...)`              |
| Placeholder    | `PlaceholderTypeComponent`     | `placeholderTypes.register(...)`            |
| Item           | `ItemTypesComponent`           | `itemRegistry.register(...)` (+ serializer) |
| Schedule       | `ScheduleTypesComponent`       | see file                                    |
| ConversationIO | `ConversationIOTypesComponent` | `conversationIORegistry.register(...)`      |
| NotifyIO       | `NotifyIOTypesComponent`       | see file                                    |
| Interceptor    | `InterceptorTypesComponent`    | `interceptorRegistry.register(...)`         |
| TextParser     | `TextParserTypesComponent`     | see file                                    |

Rules:
- Add the line **alphabetically sorted** by type name, plus the import (sorted).
- Factory implements only Player OR only Playerless (or Online): `register("<name>", new <Name><Kind>Factory(...))`.
- Factory implements BOTH Player and Playerless (nullable/combined): `registerCombined("<name>", ...)`.
- Extra constructor dependencies are `getDependency(...)` locals/fields already present in the component; reuse them,
  add a new `getDependency` only if missing.
- Registry is the single place; nothing else (no plugin.yml/other lists) needs editing.

## 4. package-info.java
```java
/**
 * Contains classes for <name> <kind>.
 */
package org.betonquest.betonquest.quest.<kind>.<name>;
```

## 5. CHANGELOG

File: `CHANGELOG.md` (root). Under `## [Unreleased]` -> `### Added`, at the top of the list:
```
- `<name>` <kind> to <short description>
```
Style: lowercase, backticked name, no trailing period. One line per element; an element in several kinds gets one line
per kind.
Behavior changes -> `### Changed`, bug fixes -> `### Fixed`. Public API changes (`code/api`) additionally go into
`API-CHANGELOG.md`.

## 6. Documentation

Reference list per kind (insert **alphabetically** by headline), all in `docs/Documentation/Reference/`:
`Actions-List.md`, `Conditions-List.md`, `Objectives-List.md`, `Placeholders-List.md`
(other kinds: look for the matching `*-List.md` or the section where existing elements of that kind are documented).

Follow `docs/Participate/Process/Docs/Style-Definitions.md`. Template:
````markdown
## `<Name>`

__Context__: @snippet:<kind>-meta:<context>@  
__Syntax__: `<name> <required> [optional] {flag} +[...]`  
__Description__: One easy sentence.

Optional longer description.

| Parameter             | Type                   | Explanation   |
|-----------------------|------------------------|---------------|
| required <br>[Number] | Required               | What it does. |
| optional <br>[String] | Optional <br>[Null]    | What it does. |
| flag <br>[Boolean]    | Flag <br>[false, true] | What it does. |

```YAML title="Examples"
<kind>s:
  readableName: "<name> 5"
```
````
Choose `<context>` by variant (check an existing entry of the same kind for the exact snippet names, e.g.
`online-offline-independent`):

| Variant                              | Context meaning                               |
|--------------------------------------|-----------------------------------------------|
| Online                               | online only                                   |
| Player (non-online)                  | online and offline                            |
| Playerless                           | independent                                   |
| Nullable / combined                  | online, offline AND independent (all three)   |
| Player + Playerless, different logic | same as nullable: list all supported contexts |

- Syntax and parameter order must match the factory exactly; required `<>`, optional `[]`, flag `{}`.
- Examples section is mandatory, inside the matching script section (`actions:`, `conditions:`, `objectives:`, ...),
  readable names.
- Link keywords to `Definition-Encyclopedia.md` with relative paths. Simple English.
- Enum parameter: add `*[EnumType]: A, B, C` abbreviation listing all values if necessary.
- Placeholders: also document the placeholder syntax (`%<name>.<args>%`) as in existing entries.

## 8. Verify
- Code: `.\mvnw.cmd verify` (or at least `-pl code/core -am`); runs Checkstyle, PMD, NullAway, SpotBugs.
- Docs: `mkdocs build` (check links).
- Disclose AI usage in the pull request.
