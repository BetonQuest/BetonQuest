---
icon: fontawesome/solid/person-through-window
---

# NPC Hiding

## Usage
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

        
    @snippet:integrations:protocollib@
=== "MythicMobs"
    !!! tip inline end "Explanation"
        The NPC `inkeeper` (which is the BetonQuestNPCID and **not** the NPCID itself) will be hidden if the player has the 
        tag `inkeeperIsTired`.
    ```YAML title="Example"
    npcs:
      innkeeper: mythicmobs UUID 60b0144d-2c55-457a-aeb8-15fbf244f3b7
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```
    @snippet:integrations:protocollib@
=== "FancyNpcs"
    !!! tip inline end "Explanation"
        The NPC `inkeeper` (which is the BetonQuestNPCID and **not** the NPCID itself) will be hidden if the player has the 
        tag `inkeeperIsTired`
    ```YAML title="Example"
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
        tag `inkeeperIsTired`
    ```YAML title="Example"
    npcs:
      innkeeper: ZNPCsPlus innkeeper
    conditions:
      hidden: tag innkeeperIsTired
    hide_npcs:
      innkeeper: hidden
    ```

??? info inline end "Additional Information"
    Information on the `npcs` section, where you define the NPCs that BetonQuest can use/reference, can be found 
    [here](../../Features/NPCs.md#provided-integrations).
    Information on the `conditions` section can be found [here](/Documentation/Scripting/About-Scripting/#conditions).


The interval in which conditions are checked can be configured with the [`npc_update_interval`](../../Configuration/Plugin-Config.md#npc-npc-settings) setting.

### Force Visibility Update
You can run the `updatevisibility` event to manually update the visibility. This is useful for performance optimizations
if used when the [npc hider interval](../../Configuration/Plugin-Config.md#npc-npc-settings) is set to high values.
