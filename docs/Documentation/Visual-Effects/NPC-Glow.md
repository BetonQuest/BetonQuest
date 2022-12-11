---
hide:
  - footer
---
If Citizens and ProtocolLib are installed you can make NPCs glow.
This can also be linked to conditions that must be met. For that you have to add a `glow_npc` section.
You can customize the [color](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html) by adding a color argument.
```yaml
glow_npc:
  tutorial_npc:
    id: "0"
    color: "white" #(1)!
  cool_npc:
    id: "1"
    color: "light_purple"
    conditions: "questActive" #(2)!
```

1. The NPC with the ID 0 will always glow white.
2. The NPC with the ID 1 will only glow light purple if the condition `questActive` is met.
