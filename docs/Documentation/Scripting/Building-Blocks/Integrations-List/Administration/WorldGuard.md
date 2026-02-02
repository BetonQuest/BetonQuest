## WorldGuard[](http://dev.bukkit.org/bukkit-plugins/worldguard/)

### Conditions

#### NPC region: `npcregion`

**persistent**, **static**

This condition is met a NPC is inside a region.

| Parameter | Syntax      | Default Value          | Explanation                          |
|-----------|-------------|------------------------|--------------------------------------|
| _Npc_     | Npc         | :octicons-x-circle-16: | The ID of the NPC                    |
| _Region_  | Region Name | :octicons-x-circle-16: | The region where the NPC needs to be |

```YAML title="Example"
conditions:
  mayorAtSpawn: "npcregion mayor spawn"
```

#### Inside Region: `region`

This condition is met when the player is inside the specified region.

| Parameter | Syntax      | Default Value          | Explanation                           |
|-----------|-------------|------------------------|---------------------------------------|
| _Region_  | Region name | :octicons-x-circle-16: | The region where the player has to be |

```YAML title="Example"
conditions:
  inCastle: "region castle"
```

### Objectives

#### Enter Region: `region`

To complete this objective you need to be in a WorldGuard region with specified name.

| Parameter | Syntax      | Default Value          | Explanation                           |
|-----------|-------------|------------------------|---------------------------------------|
| _Region_  | Region name | :octicons-x-circle-16: | The region where the player has to be |
| _Entry_   | `entry`     | Disabled               | The player needs to enter the region  |
| _Exit_    | `exit`      | Disabled               | The player needs to leave the region  |

```YAML title="Example"
objectives:
  deathZone: "region deathZone entry actions:kill"
```
