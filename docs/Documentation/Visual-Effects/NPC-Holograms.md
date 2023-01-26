---
---
!!! info "Required Dependencies"
    The following feature can be activated by using any of the following plugins:
    
    | Plugin               | Required Version | Additional Dependencies                                                            |
    |----------------------|------------------|------------------------------------------------------------------------------------|
    | DecentHolograms      | 2.7.5 or above   | [PlaceholderAPI](https://www.spigotmc.org/resources/6245/) for in-line variables.  |
    | Holographic Displays | 3.0.0 or above   | [ProtocolLib](https://www.spigotmc.org/resources/1997/) for conditioned holograms. | 
    
    If you have both plugins installed, you can use the [`default_hologram` option in "_config.yml_"](Configuration.md#default-hologram-plugin) to set which plugin should be used.


If Citizens is also installed then you can have holograms configured relative to a npc. Add the following:

```YAML
npc_holograms:
  # How often to check conditions
  check_interval: 100
  
  # Holograms follow npcs when they move (higher cpu usage when true)
  follow: false

  # Disable npc_holograms
  disabled: false

  # Hologram Settings
  default:
    # Lines in hologram
    lines:
      - "Some text!"
    # Vector offset to NPC position to place hologram
    vector: 0;3;0

    # Conditions to display hologram
    conditions: has_some_quest, !finished_some_quest

    # NPC's to apply these settings to. If blank, applies by default
    npcs:
      - 0
      - 22
```

Item lines are also supported here.
