# [MythicMobs](http://dev.bukkit.org/bukkit-plugins/mythicmobs/)

@snippet:versions:minimum@ _4.0.10_

!!! info
    MythicMobs integration supports all [BetonQuest NPC](../NPCs/index.md) features as well.
    For those, please visit the [MythicMobs](../NPCs/MythicMobs.md) NPC page.

## Items

### `Mythic`

Mythic(Mobs) items are integrated to the [BetonQuest Items](../Items/index.md) system.

In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  crown: "mythic KingsCrown"
  sword: "mythic SkeletonKingSword quest-item"
conditions:
  hasCrown: "armor crown"
actions:
  giveSword: "give sword"
```
