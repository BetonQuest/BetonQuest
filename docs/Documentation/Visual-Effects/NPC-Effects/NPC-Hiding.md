---
icon: fontawesome/solid/person-through-window
---

# NPC Hiding

## Usage
Hide Npcs if specified conditions are met!
You can do that by adding a `hide_npcs` section in your package. 
It allows you to assign conditions to specific NpcIDs like so:

```YAML
hide_npcs:
  Farmer: killedAlready,questStarted
  Guard: '!questStarted'
```

The interval the conditions are checked in can be configured in the [config.yml](../../Configuration/Configuration.md#npc-hider-interval).

### Force Visibility Update
You can run the `updatevisibility` event to manually update the visibility. This is useful for performance optimizations
if used with the [npc hider interval](../../Configuration/Configuration.md#npc-hider-interval) set to high values.
