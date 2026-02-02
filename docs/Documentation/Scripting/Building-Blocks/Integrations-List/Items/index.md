---
icon: material/food-apple
tags:
  - Items
---

Items are an essential part of any minecraft server and even more so in a quest-based one.
In BetonQuest, custom items are managed through the `items` section of the config.yml and may be created using various 
methods of serialization.   
The default implementation can be found [here](../../../../Features/Items.md).  

## Provided Integrations

BetonQuest provides integrations for the following items plugins:

- [Brewery & BreweryX](Brewery.md)
- [CraftEngine](CraftEngine.md)
- [ItemsAdder](ItemsAdder.md)
- [MMOItems](MMOItems.md)
- [Nexo](Nexo.md)

## Usages

Items may be used in a variety of [conditions](../../Conditions-List.md), [actions](../../Actions-List.md) and [objectives](../../Objectives-List.md).

<div class="grid cards" markdown>
 -   Conditions

     ---
    - [`armor`](../../Conditions-List.md#is-wearing-armor)
    - [`chestitem`](../../Conditions-List.md#are-items-in-a-chest)
    - [`hand`](../../Conditions-List.md#has-item-in-a-hand)
    - [`item`](../../Conditions-List.md#has-items)

 -   Placeholders
 
      ---
     - [`item`](../../Placeholders-List.md#item-property)

 -   Actions

     ---
     - [`chestgive`](../../Actions-List.md#put-items-into-a-chest)
     - [`chesttake`](../../Actions-List.md#take-items-from-a-chest)
     - [`drop`](../../Actions-List.md#drop-items)
     - [`give`](../../Actions-List.md#give-items)
     - [`spawn`](../../Actions-List.md#spawn-a-mob)
     - [`take`](../../Actions-List.md#take-items)

 -   Objectives

     ---
     - [`brew`](../../Objectives-List.md#brew-a-potion)
     - [`chestput`](../../Objectives-List.md#put-items-into-a-chest)
     - [`consume`](../../Objectives-List.md#consume-an-item)
     - [`craft`](../../Objectives-List.md#craft-an-item)
     - [`enchant`](../../Objectives-List.md#enchant-an-item)
     - [`equip`](../../Objectives-List.md#equip-armor)
     - [`fish`](../../Objectives-List.md#fish-an-item)
     - [`pickup`](../../Objectives-List.md#pick-up-an-item)
     - [`smelt`](../../Objectives-List.md#smelt-an-item)
</div>
