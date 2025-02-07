---
icon: material/text-account
---
# NPC Holograms

## Requirements
@snippet:integrations:holograms@

## Usage
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
      - Mayor
      - Guard
    max_range: 40 #(9)!
```

1. The lines of the hologram.
2. A vector that points from the Npc's location to the hologram's location. Can be used to configure an offset. 
Optional.
3. If the hologram follows the Npc. Optional, defaults to `false`.
4. Conditions that must be true for the hologram to display.
5. How often the conditions are checked. Optional. 
6. A list of NpcIDs that the hologram is attached to. 
7. The section that all holograms must be placed in.
8. The identifier of the hologram. Must be unique.
9. Maximum hologram display distance. Optional.

All other [hologram features](../Additional-Effects/Quest-Holograms.md) are also supported.

If you have moving NPCs (walking around) then you can have the holograms follow them by setting `follow: true`,
but this will cause a lot of updates to the holograms and may cause lag if used on a lot of Npcs.
So only set this to true for holograms with a Npc that actually moves.
Also, not every Npc supports that feature.
