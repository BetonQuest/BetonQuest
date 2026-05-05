# [MMOLib](https://www.spigotmc.org/resources/90306/)

@snippet:versions:minimum@ _1.7.1-SNAPSHOT_

## Objectives

### `MmoSkill`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `mmoskill <skill> [trigger]`  
__Description__: Requires the player to activate a MythicLib skill (e.g. with MMOItems or MMOCore).

| Parameter | Syntax     | Default Value          | Explanation                                                                                                                                                                          |
|-----------|------------|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _skill_   | SKILL_ID   | :octicons-x-circle-16: | The ID of the skill.                                                                                                                                                                 |
| _trigger_ | name:level | All trigger types.     | The [types of triggers](https://gitlab.com/phoenix-dvpmt/mythiclib/-/wikis/Skills#trigger-types) that can be used to activate the skill. If not specified, all triggers are allowed. |

```YAML title="Example"
objectives:
  triggerSkill: "mmoskill LIFE_ENDER actions:updateStatistics"
  castSkillWithMMOCore: "mmoskill DEEP_WOUND trigger:CAST actions:completeTutorial"
  itemSkill: "mmoskill DEEP_WOUND trigger:RIGHT_CLICK,LEFT_CLICK actions:giveReward"
```

## Conditions

### `MmoStat`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `mmostat <stat> <value> [equal]`  
__Description__: Whether the player has a certain stat.

Checks [stats](https://gitlab.com/phoenix-dvpmt/mythiclib/-/blob/master/plugin/src/main/java/io/lumine/mythic/lib/api/stat/SharedStat.java)
that combine all sorts of stats from MMOCore and MMOItems.
You can disable this behaviour by adding the `equal` argument.

```YAML title="Example"
conditions:
  damageReduction3: "mmostat DAMAGE_REDUCTION 3"
```
