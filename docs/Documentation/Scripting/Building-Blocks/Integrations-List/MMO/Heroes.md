# [Heroes](https://www.spigotmc.org/resources/24734/)

When you install Heroes, all kills done via this plugin's skills will be counted in MobKill objectives.

### Conditions

#### Heroes Class: `heroesclass`

This condition checks the classes of the player.
The first argument must be `primary`, `secondary` or `mastered`. Second is the name of a class or `any`.
You can optionally specify `level:` argument followed by the required level of the player.

```YAML title="Example"
conditions:
  masteredWarrior: "heroesclass mastered warrior"
```

#### Heroes Attribute: `heroesattribute`

This condition check's the level of a player's attribute.
The first argument must be `strength`, `constitution`, `endurance`, `dexterity`, `intellect`, `wisdom`, or `charisma`.
Second argument is the required level of the attribute. Must be greater than or equal the specified number.

```YAML title="Example"
conditions:
  strength5: "heroesattribute strength 5"
```

#### Skill: `heroesskill`

This condition checks if the player can use specified skill. The first argument is the name of the skill.

```YAML title="Example"
conditions:
  charge: "heroesskill charge"
```

### Actions

#### Heroes experience: `heroesexp`

This action simply gives the player specified amount of Heroes experience. The first argument is either `primary` or `secondary` and it means player's class. Second one is the amount of experience to add.

```YAML title="Example"
actions:
  primary1000: "heroesexp primary 1000"
```
