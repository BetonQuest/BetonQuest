# [FakeBlock](https://github.com/toddharrison/BriarCode/tree/main/fake-block)

@snippet:versions:minimum@ _v2.0.0_

If you have the FakeBlock integration installed, you will be able to view and hide the block groups
created in FakeBlock on a player-specific basis.

## Actions

### `FakeBlock`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `fakeblock <showgroup|hidegroup> <group>`  
__Description__: Shows or hides the block group for the player.

The block group can be specified as a comma-separated list.
The groups are case-sensitive. To show a group the `showgroup` argument is required. To hide a group the `hidegroup` argument is required.

```YAML title="Example"
actions:
  showBridge: "fakeblock showgroup bridge"
  hideCityBorder: "fakeblock hidegroup gate,wall,door"
```
