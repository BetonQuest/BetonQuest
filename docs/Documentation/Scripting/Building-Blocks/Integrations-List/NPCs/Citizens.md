# [Citizens](https://www.spigotmc.org/resources/13811/)

If you have this plugin you can use it's NPCs. I highly recommend you installing it,
it's NPCs are way more immersive. Having Citizens also allows you to use NPCKill objective and to have moving NPC's
in addition to the normal NPC functionality.

!!! info
    In addition Citizens integration supports all [BetonQuest NPC](./index.md) features.

### Actions

#### Move NPC: `npcmove`

This action will make the NPC move to a specified location. It will not return on its own,
so you have to set a single path point with _/npc path_ command - it will then return to that point every time.
If you make it move too far away, it will teleport or break, so beware.
You can change maximum pathfinding range in Citizens configuration files.

Move action can fail if the NPC is already moving for another player.

| Parameter   | Syntax                                                           | Default Value          | Explanation                                                                  |
|-------------|------------------------------------------------------------------|------------------------|------------------------------------------------------------------------------|
| _NpcID_     | npcId                                                            | :octicons-x-circle-16: | The NPCId.                                                                   |
| _Locations_ | [Locations](../../../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The locations where the NPC will move to.                                    |
| _block_     | Keyword (`block`)                                                | Disabled               | Blocks the NPC so interaction won't start a conversation while it is moving. |
| _wait_      | wait:number                                                      | 0                      | Number of ticks the NPC will wait at its destination before firing actions.  |
| _done_      | done:actions                                                     | Disabled               | List of actions fired after reaching the destination.                        |
| _fail_      | fail:actions                                                     | Disabled               | List of actions fired if this action fails.                                  |

```YAML title="Example"
actions:
  showPath: "npcmove innkeeper 100;200;300;world,105;200;280;world block wait:20 done:msg_were_here,give_reward fail:msg_cant_go,give_reward"
```

#### Stop moving NPC: `npcstop`

This will stop all current move tasks for the NPC.

| Parameter | Syntax | Default Value          | Explanation |
|-----------|--------|------------------------|-------------|
| _NpcID_   | npcId  | :octicons-x-circle-16: | The NpcId.  |

```YAML title="Example"
actions:
  stopGuide: "npcstop guard"
```

### Objectives

#### NPC Kill: `npckill`

The NPC Kill objective requires the player to kill a NPC.

| Parameter | Syntax        | Default Value          | Explanation                                                                                                       |
|-----------|---------------|------------------------|-------------------------------------------------------------------------------------------------------------------|
| _NpcID_   | npcId         | :octicons-x-circle-16: | The NpcId.                                                                                                        |
| _amount_  | amount:number | 1                      | The time the NPC needs to be killed.                                                                              |
| _notify_  | notify        | Disabled               | Display a message to the player each time they kill a NPC. Optionally with the notification interval after colon. |

<h5> Placeholder Properties </h5>

| Name     | Example Output | Explanation                                                                               |
|----------|----------------|-------------------------------------------------------------------------------------------|
| _amount_ | 6              | Shows the amount of times already killed the NPC.                                         |
| _left_   | 4              | Shows the amount of times still needed to kill the NPC for the objective to be completed. |
| _total_  | 10             | Shows the initial amount of times that the NPC needed to be killed.                       |

```YAML title="Example"
objectives:
  killThief: "npckill thief amount:3 actions:reward notify"
```
