# [MMOLib](https://www.spigotmc.org/resources/90306/)

@snippet:versions:minimum@ _1.7.1-SNAPSHOT_

## Actions

### `MmoStat`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `mmostat [stat] [value] [key] [type] [slot] [source] [add] [remove] [clear]`  
__Description__: Modifies a stat of a player.

Modify values that combine all sorts of stats from MMOCore and MMOItems.
The functional flags `add`, `remove` and `clear` can be used to modify the stat and may even be combined.
They follow a strict order of priority in their execution:

1. `clear`
2. `remove`
3. `add`

| Parameter | Syntax         | Default Value          | Explanation                                                      |
|-----------|----------------|------------------------|------------------------------------------------------------------|
| _stat_    | Name           | :octicons-x-circle-16: | The stat to modify.                                              |
| _value_   | Number         | 0                      | The value to add the stat. Only used if `add` is set to true.    |
| _key_     | Name           | `default`              | The key to modify.                                               |
| _type_    | ModifierType   | `FLAT`                 | The type of the modification.                                    |
| _slot_    | EquipmentSlot  | `OTHER`                | The slot of the modification.                                    |
| _source_  | ModifierSource | `OTHER`                | The source of the modification.                                  |
| _add_     | Boolean Flag   | `false`                | Whether to add a new modifier for the stat with the given value. |
| _remove_  | Boolean Flag   | `false`                | Whether to remove a modifier for the given key and stat.         |
| _clear_   | Boolean Flag   | `false`                | Whether to clear all modifiers for the key.                      |

??? abstract "ModifierType values"

    - `FLAT`
    - `ADDITIVE_MULTIPLIER`
    - `RELATIVE`


??? abstract "EquipmentSlot values"

    - `ARMOR`
    - `HEAD`
    - `CHEST`
    - `LEGS`
    - `FEET`
    - `ACCESSORY`
    - `INVENTORY`
    - `OFF_HAND`
    - `MAIN_HAND`
    - `OTHER`
    
??? abstract "ModifierSource values"

    - `MELEE_WEAPON`
    - `RANGED_WEAPON`
    - `OFFHAND_ITEM`
    - `MAINHAND_ITEM`
    - `HAND_ITEM`
    - `ARMOR`
    - `ACCESSORY`
    - `ORNAMENT`
    - `OTHER`
    - `VOID`

```YAML title="Example"
actions:
  damageReduction3: "mmostat add stat:DAMAGE_REDUCTION value:3"
  damageReductionRemove: "mmostat remove stat:DAMAGE_REDUCTION"
  armorToughnessReplace: "mmostat add remove stat:ARMOR_TOUGHNESS value:5 key:default type:ADDITIVE_MULTIPLIER slot:CHEST source:ACCESSORY"
  statClear: "mmostat clear"
```

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
