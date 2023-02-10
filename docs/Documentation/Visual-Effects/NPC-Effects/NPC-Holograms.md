# NPC Holograms

!!! info "Required Dependency: Hologram Plugin"
    This feature can be activated by using any of the following hologram plugins:
    
    | Plugin               | Required Version | Additional Dependencies                                                            |
    |----------------------|------------------|------------------------------------------------------------------------------------|
    | DecentHolograms      | 2.7.5 or above   | [PlaceholderAPI](https://www.spigotmc.org/resources/6245/) for in-line variables.  |
    | Holographic Displays | 3.0.0 or above   | [ProtocolLib](https://www.spigotmc.org/resources/1997/) for conditioned holograms. | 
    
    If you have both plugins installed, you can use the [`default_hologram` option in "_config.yml_"](../../Configuration/Configuration.md#default-hologram-plugin) to set which plugin should be used.

!!! info "Required Dependency: NPC Plugin" 
    Additionally, the [Citizens plugin](https://www.spigotmc.org/resources/citizens.13811/) is required!
    
```YAML title="Example"
npc_holograms: #(7)!
  myHologram: #(8)!
    lines: #(1)!
      - "Some text!" 
    vector: 0;0.5;0 #(2)!
    follow: true #(3)!
    conditions: has_some_quest,!finished_some_quest #(4)!
    check_interval: 20 #(5)!
    npcs: #(6)!
      - 0
      - 22
```

1. The lines of the hologram.
2. A vector that points from the NPC's location to the hologram's location. Can be used to configure an offset. Optional.
3. If the hologram follows the NPC. Optional, defaults to `false`.
4. Conditions that must be true for the hologram to display.
5. How often the conditions are checked. Optional. 
6. A list of NPC IDs that the hologram is attached to. 
7. The section that all holograms must be placed in.
8. The identifier of the hologram. Must be unique.

Item lines are also supported.
!!! bug ""
    **When used by external plugins like BetonQuest, DecentHolograms does not support custom model data in items lines!**

If you have moving NPCs (walking around) then you can have the holograms follow them by setting `follow: true`,
but this will cause a lot of updates to the holograms and may cause lag if used on a lot of NPCs.
So only set this to true for holograms with an NPC that actually moves.



