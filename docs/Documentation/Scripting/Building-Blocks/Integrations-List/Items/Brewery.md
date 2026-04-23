# [Brewery](https://www.spigotmc.org/resources/3082/) & [BreweryX](https://www.spigotmc.org/resources/114777/)

@snippet:versions:minimum@ _3.1.1_

## Items

### `Brew`

Brewery usage is integrated to the [Items](../../../../Features/Items.md) system and thus used for actions and 
conditions.

The first argument is the name and the second the quality.
You can specify the mode to select the brew by either the `name` or its `id`, defaulting to the name.  
The quality is not used for determining equality, only for generating the brew item.  
In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  appleLiquor: 'brew "Apple Liquor" 2'
  hotChocolate: "brew hot_choc 10 mode:id quest-item"
conditions:
  hasAppleLiquor: "item appleLiquor"
  brewId: "hand hotChocolate"
actions:
  giveHotChocolate: "give hot_chocolate"
  takeLiquor: "take appleLiquor"
```

## Conditions

### `Drunk`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `drunk <drunkness>`  
__Description__: Whether the player is drunk.

The only argument is the minimal drunkness (0-100).

```YAML title="Example"
conditions:
  drunk50: "drunk 50"
```

### `DrunkQuality`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `drunkquality <quality>`  
__Description__: Whether the player has the given drunk quality.

The only argument is the minimal drunk quality (1-10).

```YAML title="Example"
conditions:
  quality3: "drunkquality 3"
```
