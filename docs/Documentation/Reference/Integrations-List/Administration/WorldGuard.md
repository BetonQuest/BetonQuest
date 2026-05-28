# [WorldGuard](http://dev.bukkit.org/bukkit-plugins/worldguard/)

@snippet:versions:minimum@ _7.0.9_

## Objectives

### `Region`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `region <region> [entry|exit]`  
__Description__: The player has to interact with the specified region.

| Parameter | Syntax      | Default Value          | Explanation                           |
|-----------|-------------|------------------------|---------------------------------------|
| _Region_  | Region name | :octicons-x-circle-16: | The region where the player has to be |
| _Entry_   | `entry`     | Disabled               | The player needs to enter the region  |
| _Exit_    | `exit`      | Disabled               | The player needs to leave the region  |

```YAML title="Example"
objectives:
  deathZone: "region deathZone entry actions:kill"
```

## Conditions

### `NpcRegion`

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `npcregion <npc> <region>`  
__Description__: This condition is met if the specified npc is inside the specified region.

| Parameter | Syntax      | Default Value          | Explanation                          |
|-----------|-------------|------------------------|--------------------------------------|
| _Npc_     | Npc         | :octicons-x-circle-16: | The ID of the NPC                    |
| _Region_  | Region Name | :octicons-x-circle-16: | The region where the NPC needs to be |

```YAML title="Example"
conditions:
  mayorAtSpawn: "npcregion mayor spawn"
```

### `Region`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `region <region>`  
__Description__: This condition is met if the player is inside the specified region.

| Parameter | Syntax      | Default Value          | Explanation                           |
|-----------|-------------|------------------------|---------------------------------------|
| _Region_  | Region name | :octicons-x-circle-16: | The region where the player has to be |

```YAML title="Example"
conditions:
  inCastle: "region castle"
```
