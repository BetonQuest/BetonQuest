---
icon: material/account-group
tags:
  - Npcs
---

NPCs are an essential part of every RPG for player ingame interactions.
In BetonQuest, NPCs can be used to start conversations or act as a means for player interactions in various ways,
as shown in the [`Scripting`](../../../../Scripting/About-Scripting.md) and [`Visual Effects`](../../../../Visual-Effects/NPC-Effects/NPC-Hiding.md)
section of the documentation.

## Provided Integrations

BetonQuest provides integrations for the following NPC plugins:

- [Citizens](../../../../Scripting/Building-Blocks/Integrations-List/NPCs/Citizens.md)
- [MythicMobs](../../../../Scripting/Building-Blocks/Integrations-List/NPCs/MythicMobs.md)
- [FancyNpcs](../../../../Scripting/Building-Blocks/Integrations-List/NPCs/FancyNpcs.md)
- [ZNPCsPlus](../../../../Scripting/Building-Blocks/Integrations-List/NPCs/ZNPCsPlus.md)

## Referring an NPC

NPCs are defined in the `npcs` section.

```YAML title="NPC Referencing Syntax"
betonQuestNPCID: NPCSelector NPCID
```
You would then use the `betonQuestNPCID` for all NPC references within BetonQuest.

!!! note ""
    === "Citizens"
        ```YAML title="Example NPCs section"
        npcs:
          innkeeper: citizens 0
          mayorHans: citizens 4
          guard: citizens Guard byName
        ```
       
        Use `citizens` for the NPC selector argument.
        To acquire the NPC's ID, select the NPC using `/npc select`, then run `/npc id`.
     
        You can also get an NPC by its name with the `byName` argument.
        This is useful when you have multiple NPCs with the same name who should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
            
    === "MythicMobs"
        ```YAML title="Example NPCs section"
        npcs:
          mayorHans: mythicmobs UUID b18af0c3-5db7-4878-9693-05fe1b2c5a2f
          innkeeper: mythicmobs MYTHIC_MOB inkeeper
          guard: mythicmobs FACTION guards
        ```

        Use `mythicmobs` for the NPC selector argument.
        Use `Entity UUIDs` for the NPC ID argument.
        To acquire the NPCs UUID, use the `/mm listactive` command and copy the `UUID` from the NPC info.
        Alternatively, you can look at the NPC and use the `/data get entity ` command to auto complete the UUID.
     
        You can also get an NPC by its MythicMob [`Internal_Name`](https://git.mythiccraft.
        io/mythiccraft/MythicMobs/-/wikis/Mobs/Mobs#internal_name) or [`Faction`](https://git.mythiccraft.io/mythiccraft/MythicMobs/-/wikis/Mobs/Mobs#faction).
        This is useful when you have multiple NPCs who should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
            
    === "FancyNpcs"
        ```YAML title="Example NPCs section"
        npcs:
          innkeeper: FancyNpcs dc8f2889-ed79-455e-944b-115dae978737
          mayorHans: FancyNpcs 72910823-c0c3-499d-adcc-d31cb75963c0
          guard: FancyNpcs Guard byName
        ```
        
        Use`FancyNpcs` for the NPC selector argument.
        To acquire the NPCs ID, use the `/npc nearby` command and copy the `UUID` from the NPC info.
        
        You can also get an NPC by its name with the `byName` argument.
        This is useful when you have multiple NPCs who should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
            
    === "ZNPCsPlus"
        ```YAML title="Example NPCs section"
        npcs:
          bernhard: ZNPCsPlus bernhard
          guard: ZNPCsPlus Guard10
        ```
        
        Use `ZNPCsPlus` for the NPC selector argument.
        To acquire the NPCs ID, use the `/npc near 5` command and copy the `ID` from the NPC info.

!!! warning "Multiple NPCs with the same name"
    If there is more than one NPC with the same name and you select multiple NPCs by name (such as when using 
    Citizens `byName` option), certain actions like `npcteleport` or objectives like `npcrange` might throw an exception.
## Conversations

You can start [Conversations](../../../../Features/Conversations.md) through NPC interactions from players by assigning them in the
[`npc_conversations` section](../../../../Features/Conversations.md#binding-conversations-to-npcs) of a quest package.

## NPC Hiding: `hide_npcs`

Hide NPCs if specified conditions are met!
You can do that by adding a `hide_npcs` section in your package. 
```YAML title="Syntax"
hide_npcs:
  betonQuest_NPCID: conditionID(s)
```
It allows you to assign conditions to specific BQ NPCIDs like so:
        
=== "Citizens"
    !!! tip inline end "Explanation"
        The NPC `inkeeper` (which is the BetonQuestNPCID and **not** the NPCID itself) will be hidden if the player has the 
        tag `inkeeperIsTired`. 
    ```YAML title="NPC Hider Example"
    npcs:
      innkeeper: citizens 0
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
=== "MythicMobs"
    !!! tip inline end "Explanation"
        The NPC `inkeeper` (which is the BetonQuestNPCID and **not** the NPCID itself) will be hidden if the player has the 
        tag `inkeeperIsTired`.
    ```YAML title="NPC Hider Example"
    npcs:
      innkeeper: mythicmobs UUID 60b0144d-2c55-457a-aeb8-15fbf244f3b7
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
=== "FancyNpcs"
    !!! tip inline end "Explanation"
        The NPC `inkeeper` (which is the BetonQuestNPCID and **not** the NPCID itself) will be hidden if the player has the 
        tag `inkeeperIsTired`.
    ```YAML title="NPC Hider Example"
    npcs:
      innkeeper: FancyNpcs dc8f2889-ed79-455e-944b-115dae978737
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
=== "ZNPCsPlus"
    !!! tip inline end "Explanation"
        The NPC `inkeeper` (which is the BetonQuestNPCID and **not** the NPCID itself) will be hidden if the player has the 
        tag `inkeeperIsTired`.
    ```YAML title="NPC Hider Example"
    npcs:
      innkeeper: ZNPCsPlus innkeeper
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```

??? info inline end "Additional Information"
    Information on the `npcs` section, where you define the NPCs that BetonQuest can use/reference, can be found 
    [here](../../../../Scripting/Building-Blocks/Integrations-List/NPCs/index.md#provided-integrations).
    Information on the `conditions` section can be found [here](../../../../Scripting/About-Scripting.md#conditions).


The interval in which conditions are checked can be configured with the [`npc_update_interval`](../../../../Configuration/Plugin-Config.md#npc-npc-settings) setting.

### Force Visibility Update
You can run the `updatevisibility` action to manually update the visibility. This is useful for performance optimizations
if used when the [npc hider interval](../../../../Configuration/Plugin-Config.md#npc-npc-settings) is set to high values.
