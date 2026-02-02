## Shopkeepers[](http://dev.bukkit.org/bukkit-plugins/shopkeepers/)

### Conditions

#### Shop amount: `shopamount`

This condition checks if the player owns specified (or greater) amount of shops. It doesn't matter what type these shops are. The only argument is a number - minimum amount of shops.

```YAML title="Example"
conditions:
  twoShops: "shopamount 2"
```

### Actions

#### Open shop window: `shopkeeper`

This action opens a trading window of a Villager. The only argument is the uniqueID of the shop. You can find it in _Shopkeepers/saves.yml_ file, under `uniqueID` option.

```YAML title="Example"
actions:
  openShop: "shopkeeper b687538e-14ce-4b77-ae9f-e83b12f0b929"
```
