---
icon: fontawesome/solid/person-through-window
---

# NPC Hiding

## Requirements
@snippet:integrations:npcs@
@snippet:integrations:protocollib@


## Usage
Hide Citizens NPCs if specified conditions are met!
You can do that by adding a `hide_npcs` section in your package. 
It allows you to assign conditions to specific NPC IDs like so:

```YAML
hide_npcs:
  41: killedAlready,questStarted
  127: '!questStarted'
```

The interval the conditions are checked in can be configured in the [config.yml](../../Configuration/Configuration.md#npc-hider-interval).

### Force Visibility Update
You can run the `updatevisibility` event to manually update the visibility. This is useful for performance optimizations
if used with the [npc hider interval](../../Configuration/Configuration.md#npc-hider-interval) set to high values.
