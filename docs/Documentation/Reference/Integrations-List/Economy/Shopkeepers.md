# [Shopkeepers](http://dev.bukkit.org/bukkit-plugins/shopkeepers/)

@snippet:versions:minimum@ _2.22.3_

## Actions

### `shopkeeper`

__Context__: @snippet:action-meta:online@  
__Syntax__: `shopkeeper <uniqueID>`  
__Description__: This action opens a trading window of a villager.

The only argument is the uniqueID of the shop. You can find it in _Shopkeepers/saves.yml_ file, under `uniqueID` option.

```YAML title="Example"
actions:
  openShop: "shopkeeper b687538e-14ce-4b77-ae9f-e83b12f0b929"
```

## Conditions

### `shopamount`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `shopamount <amount>`  
__Description__: Whether the player owns the specified (or greater) amount of shops.

It doesn't matter what type these shops are. The only argument is a number - minimum amount of shops.

```YAML title="Example"
conditions:
  twoShops: "shopamount 2"
```
