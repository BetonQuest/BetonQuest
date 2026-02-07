# [MythicMobs](http://dev.bukkit.org/bukkit-plugins/mythicmobs/)

!!! info ""
    **Required MythicMobs version: _5.3.5_ or above**

!!! info
    MythicMobs integration supports all [BetonQuest NPC](../NPCs/index.md) features as well.
    For those, please visit the [MythicMobs](../NPCs/MythicMobs.md) NPC page.

### Items

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
