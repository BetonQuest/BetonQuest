# [PlaceholderAPI](https://www.spigotmc.org/resources/6245/)

@snippet:versions:minimum@ _2.11.6_

If you have this plugin, BetonQuest will add a `betonquest` placeholder to it. You will be able to use the `ph` placeholder.

## PAPI Placeholder: `betonquest`

You can even use BetonQuests conditions using the [condition placeholder](../../Placeholders-List.md#condition)!  
You can use all BetonQuest placeholders in any other plugin that supports PlaceholderAPI.
This works using the `%betonquest_package:placeholder%` placeholder. The `package:` part is the name of a package.
The `placeholder` part is just a [BetonQuest placeholder](../../Placeholders-List.md) without percentage characters, like `point.beton.amount`.

Testing your placeholder is easy using this command:  
`/papi parse <PlayerName> %betonquest_<PackageName>:<PlaceholderType>.<Property>%`

```scss title="Example"
%betonquest_someGreatQuest:objective.killZombies.left%
```

## Placeholders

### `Ph`

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `ph.<id>`  
__Description__: Use placeholders from another plugin via PlaceholderAPI.

Insert a placeholder starting with `ph` and the second argument should be the placeholder without percentage characters.

```scss title="Example"
%ph.player_item_in_hand%
```
