---
icon: material/message-text
tags:
  - Npcs
---

Npcs are an essential part of every RPG for player ingame interaction.
In BetonQuest Npcs can be used to start conversations or interact with them otherwise,
as shown in the `Scripting` and `Visual Effects` section of the documentation.

!!! info
    This Npc is not related to the NPC/Quester in [Conversations](Conversations.md)

## Provided Integrations

BetonQuest provides Integrations for the following Npc plugins:

- [Citizens](../Scripting/Building-Blocks/Integration-List.md#citizens)
- [FancyNpcs](../Scripting/Building-Blocks/Integration-List.md#fancynpcs)
- [ZNPCsPlus](../Scripting/Building-Blocks/Integration-List.md#znpcsplus)

!!! warning
    Citizens needs [ProtocolLib plugin](https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/artifact/build/libs/ProtocolLib.jar) for all functions to work correctly!!!

## Referring an Npc

Npcs are defined in the `npcs` section.
### Example
=== "Citizens"
    !!! example
        ```YAML
        npcs:
          innkeeper: citizens 0
          mayorHans: citizens 4
          guard: citizens Guard byName
        ```
       
       
        You simply use the Citizens NPC id as argument.
        To acquire the NPCs ID select the NPC using `/npc select`, then run `/npc id`.
     
        You can also get a NPC by its name with the `byName` argument.
        That is useful when you have many NPCs with the same name which should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
        
        !!! warning
            When more than one npc with that name exists, it will give an exception when used in like `npcteleport` events
            or `npcrange` objective.

=== "FancyNpcs"
    !!! example
        ```YAML
        npcs:
          innkeeper: FancyNpcs dc8f2889-ed79-455e-944b-115dae978737
          mayorHans: FancyNpcs 72910823-c0c3-499d-adcc-d31cb75963c0
          guard: FancyNpcs Guard byName
        ```
        
        You simply use the FancyNpcs Npc id as argument.
        To acquire the Npcs ID use the `/npc nearby` command and copy the `UUID` from the Npc info.
        
        You can also get a Npc by its name with the `byName` argument.
        That is useful when you have many Npcs with the same name which should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
        
        !!! warning
            When more than one Npc with that name exists, it will give an exception when used in like `npcteleport` events
            or `npcrange` objective.
=== "ZNPCsPlus"
    !!! example
        ```YAML
        npcs:
          bernhard: ZNPCsPlus bernhard
          guard: ZNPCsPlus Guard10
        ```
        You simply use the ZNPCsPlus Npc id as argument.
        To acquire the Npcs ID use the `/npc near 5` command and copy the `ID` from the Npc info.

## Conversations

You can start [Conversations](Conversations.md) with Npc interaction by assigning them in the
[`npc_conversations` section](Conversations.md#binding-conversations-to-npcs) of a quest package.

### Npc Hiding: `hide_npcs`
[You can find information about it here](../../Visual-Effects/NPC-Effects/NPC-Hiding.md)
@snippet:integrations:protocollib@

## Events

### Move Npc: `npcmove`

This event will make the NPC move to a specified location. It will not return on its own,
so you have to set a single path point with _/npc path_ command - it will then return to that point every time.
If you make it move too far away, it will teleport or break, so beware.
You can change maximum pathfinding range in Citizens configuration files.

Move event can fail if the NPC is already moving for another player.

| Parameter   | Syntax                                                     | Default Value          | Explanation                                                                  |
|-------------|------------------------------------------------------------|------------------------|------------------------------------------------------------------------------|
| _NpcID_     | npcId                                                      | :octicons-x-circle-16: | The NpcId.                                                                   |
| _Locations_ | [Locations](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The locations where the Npc will move to.                                    |
| _block_     | Keyword (`block`)                                          | Disabled               | Blocks the Npc so interaction won't start a conversation while it is moving. |
| _wait_      | wait:number                                                | 0                      | Number of ticks the Npc will wait at its destination before firing events.   |
| _done_      | done:events                                                | Disabled               | List of events fired after reaching the destination.                         |
| _fail_      | fail:events                                                | Disabled               | List of events fired if this event fails.                                    |

```YAML title="Example"
movenpc innkeeper 100;200;300;world,105;200;280;world block wait:20 done:msg_were_here,give_reward fail:msg_cant_go,give_reward
```

### Stop moving Npc: `npcstop`

This will stop all current move tasks for the Npc.

| Parameter | Syntax | Default Value          | Explanation |
|-----------|--------|------------------------|-------------|
| _NpcID_   | npcId  | :octicons-x-circle-16: | The NpcId.  |

```YAML title="Example"
stopnpc guard
```

## Objectives

### Npc Kill: `npckill`

The Npc Kill objective requires the player to kill a Npc. 

| Parameter | Syntax        | Default Value          | Explanation                                                                                                       |
|-----------|---------------|------------------------|-------------------------------------------------------------------------------------------------------------------|
| _NpcID_   | npcId         | :octicons-x-circle-16: | The NpcId.                                                                                                        |
| _amount_  | amount:number | 1                      | The time the Npc needs to be killed.                                                                              |
| _notify_  | notify        | Disabled               | Display a message to the player each time they kill a npc. Optionally with the notification interval after colon. |

<h5> Variable Properties </h5> 

| Name     | Example Output | Explanation                                                                               |
|----------|----------------|-------------------------------------------------------------------------------------------|
| _amount_ | 6              | Shows the amount of times already killed the npc.                                         |
| _left_   | 4              | Shows the amount of times still needed to kill the Npc for the objective to be completed. |
| _total_  | 10             | Shows the initial amount of times that the Npc needed to be killed.                       |

```YAML title="Example"
npckill thief amount:3 events:reward notify
```
