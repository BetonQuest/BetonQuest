# [MMOItems](https://www.spigotmc.org/resources/39267/)

@snippet:versions:minimum@ _6.9.4-SNAPSHOT_

## Items

### `MmoItem`

MMOItems usage is integrated to the [Items](../../../../Features/Items.md) system and thus used for actions and 
conditions.

The item can be adjusted to the players level by adding the `scale` option.  
The [`soulbound`](https://docs.phoenixdevt.fr/mmoitems/features/soulbound.html) keyword can be used
with an optional `amount`, defaulting to `1`.  
In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  crown: "mmoitem ARMOR SKELETON_CROWN"
  boundBoot: "mmoitem ARMOR TRAVELERS_BOOTS scale soulbound:100"
  gem: "mmoitem GEMS SPEED_GEM quest-item"
conditions:
  hasCrown: "hand crown"
actions:
  giveGem: "give gem:3"
```

!!! info
    When MMOItems is installed the [`craft`](../../Objectives-List.md#craft) objective also processes MMOItems
    "recipe-amounts" crafting and MMOItems station crafting.
    The amount is based on how many items have actually been crafted, not how often a specific recipe has been used!
    Therefore, a recipe that makes four items at once will let the objective progress by four steps.

## Objectives

### `MmoItemUpgrade`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `mmoitemupgrade <itemType> <itemID>`  
__Description__: The player has to upgrade the given item with an upgrade consumable.

```YAML title="Example"
objectives:
  sword: "mmoitemupgrade SWORD FALCON_BLADE"
```

### `MmoItemApplyGem`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `mmoitemapplygem <itemType> <itemID> <gemstoneID>`  
__Description__: The player has to apply a gemstone to an item.

```YAML title="Example"
objectives:
  sword: "mmoitemapplygem SWORD CUTLASS GEM_OF_ACCURACY"
```
