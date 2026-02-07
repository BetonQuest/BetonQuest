# [MMOItems](https://www.spigotmc.org/resources/39267/)

### Item system integration: `mmoitem`

MMOItems usage is integrated to the [Items](../../../../Features/Items.md) system and thus used for actions and 
conditions.

The [`soulbound`](https://docs.phoenixdevt.fr/mmoitems/features/soulbound.html) keyword can be used
with an optional `amount`, defaulting to `1`.  
In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  crown: "mmoitem ARMOR SKELETON_CROWN"
  boundBoot: "mmoitem ARMOR TRAVELERS_BOOTS soulbound:100"
  gem: "mmoitem GEMS SPEED_GEM quest-item"
conditions:
  hasCrown: "hand crown"
actions:
  giveGem: "give gem:3"
```

#### Craft item: `craft`

When MMOItems is installed the [`craft`](../../Objectives-List.md#craft-craft-an-item) objective also processes MMOItems
"recipe-amounts" crafting and MMOItems station crafting.
The amount is based on how many items have actually been crafted, not how often a specific recipe has been used!
Therefore, a recipe that makes four items at once will let the objective progress by four steps.

```YAML title="Example"
items:
  potion: "mmoitem HEALTH_POTION_RECIPE"
objectives:
  craftPotion: "craft potion 5 notify"
```

### Upgrade Item: `mmoitemupgrade`

This objective tracks if a player upgrades the given item with an upgrade consumable.

```YAML title="Example"
objectives:
  sword: "mmoitemupgrade SWORD FALCON_BLADE"
```

### Apply gemstone: `mmoitemapplygem`

This objective is completed when the player applies the gemstone with the given gemstoneID to an item with the given
itemType and itemID.

```YAML title="Example"
objectives:
  sword: "mmoitemapplygem SWORD CUTLASS GEM_OF_ACCURACY"
```
