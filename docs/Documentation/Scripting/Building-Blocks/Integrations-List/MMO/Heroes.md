# [Heroes](https://www.spigotmc.org/resources/24734/)

@snippet:versions:minimum@ _7.3.0_

When you install Heroes, all kills done via this plugin's skills will be counted in MobKill objectives.

## Actions

### `HeroeseXp`

__Context__: @snippet:action-meta:online@  
__Syntax__: `heroesexp <primary|secondary> <amount>`  
__Description__: Gives the player the specified amount of Heroes experience.

The first argument is either `primary` or `secondary` and it 
means player's class. Second one is the amount of experience to add.

```YAML title="Example"
actions:
  primary1000: "heroesexp primary 1000"
```

## Conditions

### `HeroesAttribute`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `heroesattribute <strength|constitution|endurance|dexterity|intellect|wisdom|charisma> <level>`  
__Description__: Whether the player has the specified level of attribute.

The first argument must be `strength`, `constitution`, `endurance`, `dexterity`, `intellect`, `wisdom`, or `charisma`.
Second argument is the required level of the attribute. Must be greater than or equal the specified number.

```YAML title="Example"
conditions:
  strength5: "heroesattribute strength 5"
```

### `HeroesClass`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `heroesclass <primary|secondary|mastered> <class> [level]`  
__Description__: Whether the player has the specified class. 

The first argument must be `primary`, `secondary` or `mastered`. Second is the name of a class or `any`.
You can optionally specify `level:` argument followed by the required level of the player.

```YAML title="Example"
conditions:
  masteredWarrior: "heroesclass mastered warrior"
```

### `HeroesSkill`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `heroesskill <skillName>`  
__Description__: Whether the player can use the specified skill.

The first argument is the name of the skill.

```YAML title="Example"
conditions:
  charge: "heroesskill charge"
```
