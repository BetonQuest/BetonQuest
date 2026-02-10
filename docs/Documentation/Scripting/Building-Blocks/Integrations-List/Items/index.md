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
    - [`armor`](../../Conditions-List.md#armor-is-wearing-armor)
    - [`chestitem`](../../Conditions-List.md#chestitem-are-items-in-a-chest)
    - [`hand`](../../Conditions-List.md#hand-has-item-in-a-hand)
    - [`item`](../../Conditions-List.md#item-has-items)

 -   Placeholders
 
      ---
     - [`item`](../../Placeholders-List.md#item-item-property)

 -   Actions

     ---
     - [`chestgive`](../../Actions-List.md#chestgive-put-items-into-a-chest)
     - [`chesttake`](../../Actions-List.md#chesttake-take-items-from-a-chest)
     - [`drop`](../../Actions-List.md#drop-drop-items)
     - [`give`](../../Actions-List.md#give-give-items)
     - [`spawn`](../../Actions-List.md#spawn-spawn-a-mob)
     - [`take`](../../Actions-List.md#take-take-items)

 -   Objectives

     ---
     - [`brew`](../../Objectives-List.md#brew-brew-a-potion)
     - [`chestput`](../../Objectives-List.md#chestput-put-items-into-a-chest)
     - [`consume`](../../Objectives-List.md#consume-consume-an-item)
     - [`craft`](../../Objectives-List.md#craft-craft-an-item)
     - [`enchant`](../../Objectives-List.md#enchant-enchant-an-item)
     - [`equip`](../../Objectives-List.md#equip-equip-armor)
     - [`fish`](../../Objectives-List.md#fish-fish-an-item)
     - [`pickup`](../../Objectives-List.md#pickup-pick-up-an-item)
     - [`smelt`](../../Objectives-List.md#smelt-smelt-an-item)
</div>
