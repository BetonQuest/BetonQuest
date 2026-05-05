# [mcMMO](https://www.spigotmc.org/resources/64348/)

@snippet:versions:minimum@ _2.1.227_

## Actions

### `mcMmoExp`

__Context__: @snippet:action-meta:online@  
__Syntax__: `mcmmoexp <name> <amount>`  
__Description__: Add experience points in a specified skill.

The first argument is the name of the skill, second one is the amount of experience to add.

```YAML title="Example"
actions:
  swords1500: "mcmmoexp swords 1500"
```

## Conditions

### `mcMmoLevel`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `mcmmolevel <name> <level>`  
__Description__: Whether the player has high enough level in the specified skill.

The first argument is the name of the skill, second one is the minimum level the player needs to have to pass this condition.

```YAML title="Example"
conditions:
  woodcutting50: "mcmmolevel woodcutting 50"
```
