---
icon: material/food-apple
tags:
  - Items
---

# Items

Items are an essential part of any minecraft server and even more so in a quest-based one.
In BetonQuest, custom items are managed through the `items` section of the "_config.yml_" and may be created using various 
methods of serialization.   
The default implementation can be found [here](../../../../Features/Items.md).  

## Provided Integrations

BetonQuest provides integrations for the following items plugins:

- [Brewery & BreweryX](./Brewery.md)
- [CraftEngine](./CraftEngine.md)
- [ItemsAdder](./ItemsAdder.md)
- [MMOItems](./MMOItems.md)
- [MythicMobs](./MythicMobs.md)
- [Nexo](./Nexo.md)

## Usages

Items may be used in a variety of [conditions](../../Conditions-List.md), [actions](../../Actions-List.md) and [objectives](../../Objectives-List.md).

<div class="grid cards" markdown>
 -   Conditions

     ---
    - [`armor`](../../Conditions-List.md#armor)
    - [`chestitem`](../../Conditions-List.md#chestitem)
    - [`hand`](../../Conditions-List.md#hand)
    - [`item`](../../Conditions-List.md#item)

 -   Placeholders
 
      ---
     - [`item`](../../Placeholders-List.md#item)

 -   Actions

     ---
     - [`chestgive`](../../Actions-List.md#chestgive)
     - [`chesttake`](../../Actions-List.md#chesttake)
     - [`drop`](../../Actions-List.md#drop)
     - [`give`](../../Actions-List.md#give)
     - [`spawn`](../../Actions-List.md#spawn)
     - [`take`](../../Actions-List.md#take)

 -   Objectives

     ---
     - [`brew`](../../Objectives-List.md#brew)
     - [`chestput`](../../Objectives-List.md#chestput)
     - [`consume`](../../Objectives-List.md#consume)
     - [`craft`](../../Objectives-List.md#craft)
     - [`enchant`](../../Objectives-List.md#enchant)
     - [`equip`](../../Objectives-List.md#equip)
     - [`fish`](../../Objectives-List.md#fish)
     - [`pickup`](../../Objectives-List.md#pickup)
     - [`smelt`](../../Objectives-List.md#smelt)
</div>
