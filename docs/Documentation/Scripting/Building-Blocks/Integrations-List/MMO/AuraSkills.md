# [AuraSkills](https://www.spigotmc.org/resources/81069/)

### Conditions

#### Skill level: `auraskillslevel`

Checks if the player has the specified skill level. The amount is a number.
The player needs to be on that level or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument, then the player must match the specified level exactly.

```YAML title="Example"
conditions:
  fighting5: "auraskillslevel fighting 5"
  farming10: "auraskillslevel farming 10 equal"
```

#### Stat level: `auraskillsstatslevel`

Checks if the player has the specified stat level. The amount is a number.
The player needs to be on that level or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument, then the player must match the specified level exactly.

```YAML title="Example"
conditions:
  luck5: "auraskillsstatslevel luck 5"
  luck10: "auraskillsstatslevel luck 10 equal"
```

### Actions

#### Give Skill Xp : `auraskillsxp`

Adds experience to the players skill. The amount is a number.
The `level` argument is optional and would convert the amount to levels instead of XP points.

```YAML title="Example"
actions:
  farming5: "auraskillsxp farming 5"
  farming10: "auraskillsxp farming 10 level"
```
