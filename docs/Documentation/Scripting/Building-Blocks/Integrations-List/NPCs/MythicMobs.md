# [MythicMobs](http://dev.bukkit.org/bukkit-plugins/mythicmobs/)

!!! info ""
    **Required MythicMobs version: _5.3.5_ or above**

!!! info
    MythicMobs integration supports all [BetonQuest NPC](index.md) features.

### Items

Mythic(Mobs) items are integrated to the [BetonQuest Items](../Items/index.md) system.

In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  crown: "mythic KingsCrown"
  sword: "mythic SkeletonKingSword quest-item"
conditions:
  hasCrown: "armor crown"
actions:
  giveSword: "give sword"
```

### Objectives

#### MobKill: `mmobkill`

You need to kill the specified amount of MythicMobs to complete this objective.
You can add a `notify` keyword if you want to send a notification to players whenever the objective progresses.

| Parameter                      | Syntax                              | Default Value          | Explanation                                                                                                                                             |
|--------------------------------|-------------------------------------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| _identifier_                   | strings                             | :octicons-x-circle-16: | Identifiers for mobs that must be killed, based on `mode`. Multiple mob identifiers must be comma separated.                                            |
| _mode_                         | mode:mode                           | INTERNAL_NAME          | What of the mob should be checked. Either `INTERNAL_NAME` of the mob (as defined in the config) or `FACTION`.                                           |
| _amount_                       | amount:number                       | 1                      | Amount of mobs required to kill.                                                                                                                        |
| _minLevel_                     | minLevel:number                     | Disabled               | Minimal level of mob to kill.                                                                                                                           |
| _maxLevel_                     | maxLevel:number                     | Disabled               | Maximal level of mob to kill.                                                                                                                           |
| _neutralDeathRadiusAllPlayers_ | neutralDeathRadiusAllPlayers:number | Disabled               | Radius to count objective progress for each nearby player when the mob is killed by any non-player source.                                              |
| _deathRadiusAllPlayers_        | deathRadiusAllPlayers:number        | Disabled               | Radius to count objective progress for each nearby player, no matter if it was killed by a non-player source or not. Disables the neutral death radius. |
| _marked_                       | marked:text                         | None                   | Check for mark on mobs as used in `mspawnmob` action.                                                                                                   |

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of mythic mobs already killed,
`left` is the amount of mythic mobs still needed to kill and `total` is the amount of mythic mobs initially required.
`mode` gives the identification type.

```YAML title="Example"
objectives:
  killKnight: mmobkill SkeletalKnight amount:2 actions:reward
  killSnails: mmobkill SnekBoss,SnailBoss,SunBoss amount:10 actions:reward
  snailFaction: mmobkill snail mode:faction amount:10 actions:reward
  killBoss: mmobkill SnekBoss amount:2 minlevel:4 maxlevel:6 actions:reward marked:DungeonBoss3
  killDevil: mmobkill dungeonDevil deathRadiusAllPlayers:30 actions:reward
  bandits: mmobkill bandit deathRadiusAllPlayers:30 mode:FACTION actions:spawnTrader
```

### Conditions

#### MythicMob distance: `mythicmobdistance`

Check whether the player is near a specific MythicMobs entity. The first argument is the internal name of the mob (the one defined in MythicMobs' configuration). The second argument is the distance to check, measured in block lengths in a circular radius.

```YAML title="Example"
conditions:
  nearKnight: "mythicmobdistance SkeletalKnight 7"
```

### Actions

#### :material-skull: Spawn MythicMob: `mspawnmob`

**static**

| Parameter  | Syntax                                                     | Default Value          | Explanation                                                                                                                             |
|------------|------------------------------------------------------------|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| _location_ | [ULF](../../../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to spawn the mob at.                                                                                                       |
| _name_     | name:level                                                 | :octicons-x-circle-16: | MythicMobs mob name. A level must be specified after a colon.                                                                           |
| _amount_   | Positive Number                                            | :octicons-x-circle-16: | Amount of mobs to spawn.                                                                                                                |
| _target_   | Keyword                                                    | False                  | Will make the mob target the player.                                                                                                    |
| _private_  | Keyword                                                    | Disabled               | Will hide the mob from all other players until restart. This does not hide particles or block sound from the mob. Also see notes below. |
| _marked_   | marked:text                                                | None                   | Marks the mob. You can check for marked mobs in mmobkill objective.                                                                     |

```YAML title="Example"
actions:
  spawnBoss: "mspawnmob 100;200;300;world MegaBoss:1 1 target"
  spawnKnights: "mspawnmob 100;200;300;world SkeletalKnight:3 5"
  spawnPrivateDevil: "mspawnmob 100;200;300;world Mephisto:1 5 target private marked:DungeonBoss3"
```

!!! warning "Private Argument"
    The `private` argument requires some MythicMob setup for optimal use.
    It's best to use the `private` argument in combination with the `target` argument so the mob does not attack
    players that cannot see it.
    Additionally, the mob should be configured to never change its AI target using MythicMobs.

!!! info "Private & Target Arguments"
    The `private` and `target` arguments are ignored when the action is used in a static context like [Schedules](../../../Schedules.md).

#### Cast Mythic Skill: `mcast`

| Parameter | Syntax | Default Value          | Explanation            |
|-----------|--------|------------------------|------------------------|
| _name_    | Name   | :octicons-x-circle-16: | Name of Skill to cast. |

```YAML title="Example"
actions:
  castPoison: "mcast AngrySludgePoison"
```
