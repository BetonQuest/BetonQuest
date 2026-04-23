# [AuraSkills](https://www.spigotmc.org/resources/81069/)

@snippet:versions:minimum@ _2.2.0_

## Actions

### `AuraSkillsXp`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `auraskillsxp <skill> <amount> [level]`  
__Description__: Adds experience to the players skill.

The amount is a number. The `level` argument is optional and would convert the amount to levels instead of XP points.

```YAML title="Example"
actions:
  farming5: "auraskillsxp farming 5"
  farming10: "auraskillsxp farming 10 level"
```

## Conditions

### `AuraSkillsLevel`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `auraskillslevel <name> <amount> [equal]`  
__Description__: Whether the player has the specified skill level.

The amount is a number. The player needs to be on that level or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument, then the player must match the specified level exactly.

```YAML title="Example"
conditions:
  fighting5: "auraskillslevel fighting 5"
  farming10: "auraskillslevel farming 10 equal"
```

### `AuraSkillsStatsLevel`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `auraskillsstatslevel <name> <amount> [equal]`  
__Description__: Whether the player has the specified stat level.

The amount is a number. The player needs to be on that level or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument, then the player must match the specified level exactly.

```YAML title="Example"
conditions:
  luck5: "auraskillsstatslevel luck 5"
  luck10: "auraskillsstatslevel luck 10 equal"
```
