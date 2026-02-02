# [Nexo](https://polymart.org/product/6901/nexo)

### Items

Nexo usage is integrated to the [Items](../../../../Features/Items.md) system and thus used for actions and conditions.

In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  forestTrident: "nexo forest_trident"
  tableLamp: "nexo table_lamp quest-item"
conditions:
  hasForestTrident: "hand forestTrident"
actions:
  giveTableLamp: "give tableLamp:3"
```
