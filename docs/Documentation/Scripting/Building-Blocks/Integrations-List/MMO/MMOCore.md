# [MMOCore](https://www.spigotmc.org/resources/70575/)

### Conditions

#### MMOCore class: `mmoclass`

Checks if a player has the given MMOCore class. You can check for any class that is not the default class by writing `*`
instead of a class name.
If a level has been specified the player needs to be on that level
or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument.

```YAML title="Example"
conditions:
  5: "mmoclass * 5"
  warrior: "mmoclass WARRIOR"
  mage5: "mmoclass MAGE 5"
  mage5Equal: "mmoclass MAGE 5 equal"
```

#### MMOCore attribute: `mmoattribute`

Checks if a player has the specified attribute on the given level or higher.
You can disable this behaviour by adding the `equal` argument.

```YAML title="Example"
conditions:
  strength2: "mmoclass mmoattribute strength 2"
  strength2Equal: "mmoclass mmoattribute strength 2 equal"
```

#### MMOCore profession: `mmoprofession`

Checks if a player has the specified profession on the given level or higher.
You can disable this behaviour by adding the `equal` argument.

```YAML title="Example"
conditions:
  mining2: "mmoprofession mining 2"
  mining2Equal: "mmoprofession mining 2 equal"
```

### Objectives

#### Break Special Blocks: `mmocorebreakblock`

This objective requires the player to break
[special blocks from MMOCore](https://gitlab.com/phoenix-dvpmt/mmocore/-/wikis/Mining%20and%20Block%20Regen). Please note that you *must* use this objective over `block` if you are using MMOCore's custom mining system.
All three different block types and an amount can be defined. You can also send notifications to the player by appending
the `notify` keyword optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of blocks already broken,
`left` is the amount of blocks still left to break and `total` is the amount of blocks initially required.

```YAML title="Example"
objectives:
  block1: "mmocorebreakblock 5 block:1"      #A custom block's block ID
  stone: "mmocorebreakblock 64 block:STONE"  #vanilla material
  head: "mmocorebreakblock 1 block:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVy" #... this is a heads texture data
```

#### Change MMOCore class: `mmochangeclass`

This objective requires the player to change their class.

```YAML title="Example"
objectives:
  selectAnyClass: "mmochangeclass actions:pickedClass"
  selectMage: "mmochangeclass class:MAGE actions:startMageIntroQuest"
```

#### MMOCore Profession levelup: `mmoprofessionlevelup`

This objective requires the player to level the given profession to the specified level.
Use `main` to check for class level ups.

```YAML title="Example"
objectives:
  mining10: "mmoprofessionlevelup MINING 10"
```

### Actions

#### Give MMOCore class experience: `mmoclassexperience`

Adds experience to the players class. The amount is a number. The `level` argument
is optional and would convert the amount to levels instead of XP points.

```YAML title="Example"
actions:
  150: "mmoclassexperience 150"
  level1: "mmoclassexperience 1 level"
```

#### Give MMOCore profession experience: `mmoprofessionexperience`

Adds experience in the specified player profession. The amount is a number. The `level` argument
is optional and would convert the amount to levels instead of XP points.

```YAML title="Example"
actions:
  mining: "mmoprofessionexperience MINING 100"
  customProf: "mmoprofessionexperience CUSTOM_PROFESSION_NAME 1 level"
```

#### Give class points: `mmocoreclasspoints`

Gives the player class points. The amount is a number.

```YAML title="Example"
actions:
  give1: "mmocoreclasspoints 1"
```

#### Give skill points: `mmocoreskillpoints`

Gives the player skill points. The amount is a number.

```YAML title="Example"
actions:
  give10: "mmocoreskillpoints 10"
```

#### Give attribute points: `mmocoreattributepoints`

Gives the player attribute points. The amount is a number.

```YAML title="Example"
actions:
  give2: "mmocoreattributepoints 2"
```

#### Give attribute reallocation points: `mmocoreattributereallocationpoints`

Gives the player attribute reallocation points. The amount is a number.

```YAML title="Example"
actions:
  give1: "mmocoreattributereallocationpoints 1"
```
