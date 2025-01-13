---
icon: material/plus-box
---
## Summary

This page covers the creation of new Quest Type (for example Events) and Features (for example Conversation IO)
implementations and how they are [registered](#registry) in BetonQuest so they can be used on the Server.

## Writing new Quest Type Implementations 
- `Condition`
- `Event`
- `Variable`

Quest Types are the core of BetonQuest.
The new API for `Condition`, `Event` and `Variable` is located in the `org.betonquest.betonquest.api.quest` package
(for the `Objective` see the [LegacyAPI](Legacy-API.md#writing-objectives)).
The sub-packages contain the core quest types that are registered in their respective `QuestTypeRegistry`
with the `register` method:

### Factory Pattern

To create Quest Type instances the **Factory Pattern** is used.
With this the constructing factory can provide any required objects the specific implementation needs to run
and allows the more advanced compositions as in [Online](#onlineprofile--player), [Mixed](#mixed) and
[Main Thread](#executing-on-bukkit-main-thread).

In the following you register not the actual implementation, but a factory that will create it from the objects
stored in the factory which are required for it and the `Instruction` changing its "configuration".
The `Instruction` will be parsed in the factory's `parse` method which will construct the implementation without any
reference to the `Instruction` object used to create it.

### Playerless

To create a quest type that works without any player or profile reference you can use the `Playerless` variant,
where the resolving does not take any parameter (for example real-time references).

You simply implement the respective `PlayerlessQuestFactory` and register the instance.

### Profile

To create a quest type that works with a profile (documentation required) reference you can use the `Profile` variant,
where the resolving does take the player profile as parameter (for example points).

You simply implement the respective `PlayerQuestFactory` and register the instance.

### OnlineProfile / Player

To create a quest type that works with a player object reference you can use the `OnlineProfile` variant,
where the resolving requires an `OnlineProfile` as parameter (for example inventory access).

You get the `Player` from the `OnlineProfile` with the `getPlayer()` method.

`Online<Quest>Adapter` is a facade for the [Profile](#profile), thus allowing the same methods to register and use.

To create the instance to register you simply implement the respective `PlayerQuestFactory` and wrap the implementation
created in the `parse` method in the `Online<Quest>Adapter`.

### Mixed

You can also create a quest type that allows both a `Profile` and `null` as argument with the `Nullable<Quest>` 
interface. The only difference to the [Profile](#profile) variant is the nullability of the parameter.

This is commonly used when the type not require a profile, but used variables accepts a profile for resolving.
Mostly the `Instruction` is parsed in a method creating the `Nullable<Type>` which then is just wrapped in the
overridden methods with their `Nullable<Type>Adapter`.

Also, you can create two independent types that are returned by the different `parse` methods by the factory,
for example to utilize the `Online<Quest>Adapter` for the profile part.

The register method for this, caused by Java's Runtime Type Erasure, is `registerCombined`.

### Executing on (Bukkit) Main Thread

BetonQuest tries to run heavy operations async to avoid impact on the server's tick rate.
To ensure your code runs on the Bukkit main thread, for example when interacting with world state, you can wrap it
with a `PrimaryServerThread<Type>` (in the `org.betonquest.betonquest.quest.<type>` package).
Simply provide the Type to sync and a `PrimaryServerThreadData` (in the `org.betonquest.betonquest.quest` package).

### Legacy

Currently, you can still register the legacy implementation (from `<QuestType>.class`).

## Registry

Registering implementations is done in the different `Registry` objects,
which stores them and makes them accessible on the server by the different parts of BetonQuest.

The separation is made into following:

- `QuestTypeRegistries`, which provide Instruction based object creation
  - `Condition`
  - `Event`
  - `Objective` (currently only with old `Objective.Class`)
  - `Variable`
- `FeatureRegistries`, which covers more complex and different creation patterns
  - `ConversationIO`
  - `Interceptor`
  - `NotifyIO`
  - `Schedule`

They are accessed through the `getQuestRegistries()` and `getFeatureRegistries()` methods on the plugin.

The `QuestItem` will be added to the `QuestTypeRegistries` after its overhaul.

For Writing new Features you currently have to reference existing code,
see also [Legacy creating ConversationIO](Legacy-API.md#creating-additional-conversation-inputoutput-methods).
