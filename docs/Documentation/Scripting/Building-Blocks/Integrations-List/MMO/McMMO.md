# [McMMO](https://www.spigotmc.org/resources/64348/)

### Conditions

#### McMMO Level: `mcmmolevel`

This conditions checks if the player has high enough level in the specified skill. The first argument is the name of the skill, second one is the minimum level the player needs to have to pass this condition.

```YAML title="Example"
conditions:
  woodcutting50: "mcmmolevel woodcutting 50"
```

### Actions

#### Add MCMMO Experience: `mcmmoexp`

This action adds experience points in a specified skill. The first argument is the name of the skill, second one is the amount of experience to add.

```YAML title="Example"
actions:
  swords1500: "mcmmoexp swords 1500"
```
