# (Re-)Moved Methods


## Registry

Registering usable implementations of BetonQuest concepts is done in the different `Registry` objects.
A `Registry` stores them and makes them accessible to the actual using (like )

The concrete Registries are divided into these which will produce objects based on the `Instruction` string
and all others things.

### Quest Types (Instruction based)

The `QuestTypeRegistries` can be accessed on the BetonQuest instance with the `#getQuestRegistries()` method.
The following Registries are part of it:
- `Condition`
- `Event`
- `Variable`

The
- `Objective` (currently with only raw class usage)
- `QuestItem` (subject to overhaul)
still need to be added to these Registries.

### Other

The `OtherFactoryRegistries` (name WIP) can be accessed on the BetonQuest instance with the `#getOtherRegistries()` method.
The following Registries are part of it:
- `ConversationIO`
- `Interceptor`
- `NotifyIO`
- `Schedule`
