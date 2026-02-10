# [Magic](http://dev.bukkit.org/bukkit-plugins/magic/)

### Conditions

#### Wand: `wand`

This condition can check wands. The first argument is either `hand`, `inventory` or `lost`.
If you choose `lost`, the condition will check if the player has lost a wand.
If you choose `hand`, the condition will check if you're holding a wand in your hand.
`inventory` will check your whole inventory instead of just the hand.
In case of `hand` and `inventory` arguments you can also add optional `name:` argument followed by the name
of the wand (as defined in _wands.yml_ in Magic plugin) to check if it's the specific type of the wand.
In the case of `inventory` you can specify an amount with `amount` and this will only return true if a player has that amount.
You can also use optional `spells:` argument, followed by a list of spells separated with a comma.
Each spell in this list must have a minimal level defined after a colon.

```YAML title="Example"
conditions:
  wand: "wand hand name:master spells:flare:3,missile:2"
```
