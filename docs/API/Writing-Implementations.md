---
icon: material/plus-box
---
@snippet:api-state:draft@
## Summary

This page covers the creation of new Quest Types (e.g., Event) and Soon™ Features (e.g., Conversation IO) 
implementations and how they are [registered](#registry) in BetonQuest so that they can be used on the server.

## Writing New Quest Type Implementations

The following concepts are defined as "Quest Types", as they build the core of BetonQuest.

- `Condition`
- `Event`
- `Objective`
- `Variable`

The API is located in the `org.betonquest.betonquest.api.quest` package.
From the list above the `Objective` is still part of the [LegacyAPI](Legacy-API.md#writing-objectives).
The sub-packages contain the core quest types that are registered in their respective `QuestTypeRegistry` using
the `register` method.

For an easy event implementation see the
[Burn Event Package](https://github.com/BetonQuest/BetonQuest/tree/main/src/main/java/org/betonquest/betonquest/quest/event/burn).

### Factory Pattern

To create Quest Type instances, the **Factory Pattern** is used.
This allows the constructing factory to provide any required objects that the specific implementation needs to function
and enables more advanced compositions. For example in [Online](#onlineprofile-player), [Mixed](#mixed),
and [Main Thread](#executing-on-bukkit-main-thread).

In this context, you register not the actual implementation, but a factory that will create it.
Into the factory you inject the dependencies that are required for creating the implementation.
The `Instruction`, providing the "configuration", will be parsed in the factory's `parse` method,
where the implementation is actually constructed.
With that the implementation usually does not need the `Instruction`, allowing much cleaner object orientation 
implementations.

### Playerless

To create a quest type that works without any player or profile reference, you can use the `Playerless` variant,
where the resolving does not take a profile parameter (e.g., real-time references).

You simply implement the respective `PlayerlessQuestFactory` and register the instance.

### Profile

To create a quest type that works with a profile (documentation required) reference, you can use the `Profile` variant
where the resolving takes the player profile as a parameter (e.g., points).

You simply implement the respective `PlayerQuestFactory` and register the instance.

### OnlineProfile / Player

To create a quest type that works with a player object reference, you can use the `OnlineProfile` variant,
where the resolving requires an `OnlineProfile` as a parameter (e.g., inventory access).

You can obtain the `Player` from the `OnlineProfile` using the `getPlayer()` method.

`Online<Quest>Adapter` serves as a facade for the [Profile](#profile), allowing the same methods to register and use.

To create the instance to register, simply implement the respective `PlayerQuestFactory` and wrap the implementation
created in the `parse` method with the `Online<Quest>Adapter`.

### Mixed

You can also create a quest type that allows both a `Profile` and `null` as arguments using the `Nullable<Quest>`
interface. The primary difference from the [Profile](#profile) variant is the nullability of the parameter.

This is commonly used when the type does not require a profile but uses variables that accept a profile for resolution
Typically, the `Instruction` is parsed in a method that creates the `Nullable<Type>`, which is then wrapped in the
overridden methods with their `Nullable<Type>Adapter`.

Additionally, you can create two independent types returned by different `parse` methods by the factory,
for example, to utilize the `Online<Quest>Adapter` for the profile part.

You simply implement the corresponding `PlayerQuestFactory` and `PlayerlessQuestFactory`.
However, due to Java's Runtime Type Erasure, the register method for this is `registerCombined`.

### Executing on (Bukkit) Main Thread

BetonQuest attempts to run heavy operations asynchronously to avoid impacting the server's tick rate.
To ensure your code runs on the Bukkit main thread (e.g., when interacting with world state), you can wrap it
with a `PrimaryServerThread<Type>` (located in the `org.betonquest.betonquest.quest.<type>` package).
Simply provide the Type to sync and a `PrimaryServerThreadData` (in the `org.betonquest.betonquest.quest` package).

### Legacy

Currently, you can still register the legacy implementation (from `<QuestType>.class`).

## Registry

Registering implementations is accomplished through various `Registry` objects,
which store them and make them accessible on the server by different parts of BetonQuest.

The separation is as follows:

- **QuestTypeRegistries**, which provide instruction-based object creation:
  - `Condition`
  - `Event`
  - `Objective`
  - `Variable`
- **FeatureRegistries**, which cover more complex and varied creation patterns:
  - `ConversationIO`
  - `Interceptor`
  - `NotifyIO`
  - `Schedule`

These can be accessed through the `getQuestRegistries()` and `getFeatureRegistries()` methods on the plugin.

The `QuestItem` will be added to the `QuestTypeRegistries` after its overhaul.

For writing new features, you currently need to reference existing code.
See also [Legacy creating ConversationIO](Legacy-API.md#creating-additional-conversation-inputoutput-methods).
