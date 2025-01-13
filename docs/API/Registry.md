---
icon: material/plus-box
---
## Registry

Registering usable implementations of BetonQuest concepts is done in the different `Registry` objects.
A `Registry` stores them and makes them accessible on the server by the different parts of BetonQuest.

The concrete Registries are divided into these which will produce objects based on the `Instruction` string,
like an Event, and all others things, like a ConversationIO.

### Quest Types (Instruction based)

The `QuestTypeRegistries` can be accessed on the BetonQuest instance with the `#getQuestRegistries()` method.
The following Registries are part of it:

- `Condition`
- `Event`
- `Objective` (currently only with old `Objective.Class` registering)
- `Variable`

The `QuestItem` will be added to these Registries after its overhaul.

### Other

The `OtherFactoryRegistries` (name WIP) can be accessed on the BetonQuest instance with the `#getOtherRegistries()` method.
The following Registries are part of it:
- `ConversationIO`
- `Interceptor`
- `NotifyIO`
- `Schedule`

## Writing new Quest Type Implementations

The new API for Conditions, Events and Variables is in the `org.betonquest.betonquest.api.quest` package.
The sub-packages contain the core quest types that are registered in the respective `QuestTypeRegistry`:
- `Condition`
- `Event`
- `Variable`

### Playerless

`PlayerlessQuestFactory` and `QuestTypeRegistry#register`

### Profile

`PlayerQuestFactory` and `QuestTypeRegistry#register`

### OnlineProfile / Player

`Online<Quest>Adapter` see Profile with `QuestTypeRegistry#register`

### Mixed

`Nullable<Quest>Adapter` and `QuestTypeRegistry#registerCombined` (overloading), thus allowing `Online<Quest>Adapter`

### Legacy

Currently, you can still register the legacy implementation (from `<QuestType>.class`).
