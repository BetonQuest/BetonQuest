# [MMOLib](https://www.spigotmc.org/resources/90306/)

### MythicLib stat: `mmostat`

Checks [these](https://gitlab.com/phoenix-dvpmt/mythiclib/-/blob/master/plugin/src/main/java/io/lumine/mythic/lib/api/stat/SharedStat.java)
stats that combine all sorts of stats from MMOCore and MMOItems.
The player needs to be on the specified level or higher in order to meet this condition.
You can disable this behaviour by adding the `equal` argument.

```YAML title="Example"
conditions:
  damageReduction3: "mmostat DAMAGE_REDUCTION 3"
```

### Activate MythicLib skill: `mmoskill`

This objective requires the player to activate a MythicLib skill (e.g. with MMOItems or MMOCore).

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
