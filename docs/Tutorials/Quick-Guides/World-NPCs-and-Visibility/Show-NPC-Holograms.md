---
icon: material/text-recognition
---

# How to show NPC holograms

!!!info "Requirements"
    
    NPC holograms require a supported hologram plugin.
    BetonQuest supports:
    
    - DecentHolograms
    - FancyHolograms
    - Holographic Displays
    
    If more than one hologram plugin is installed, choose the default plugin with the `hologram.default` option in the
    BetonQuest config.
    After installing or changing hologram plugins, restart the server and check `/bq version`.

NPC holograms are useful when players should quickly see which NPC starts, continues, or finishes a quest.
BetonQuest can attach holograms to NPCs and show different text depending on the player's quest state.

The following example shows floating item markers above a mayor NPC.
Players see a quest book before accepting the quest, a compass while the quest is active, and no hologram after the
quest is finished.

The goal is:

- Define the NPC that should receive the hologram.
- Show one hologram before the quest starts.
- Show another hologram while the quest is active.
- Hide both holograms after the quest is completed.
- Use item lines as clear visual markers.
- Keep the hologram above the NPC with a vertical offset.

```yaml
npcs:
  mayor: "citizens 4" #(1)!

npc_conversations:
  mayor: "mayorQuest"

npc_holograms:
  mayorNewQuest:
    lines:
      - "item:questAvailableIcon" #(2)!
      - "&6New Quest"
    npcs: mayor #(3)!
    vector: 0;0.7;0 #(4)!
    conditions: "!mayorQuestStarted,!mayorQuestDone" #(5)!
    check_interval: 20 #(6)!
    max_range: 32 #(7)!

  mayorReturnQuest:
    lines:
      - "item:questReturnIcon"
      - "&2Return here"
    npcs: mayor
    vector: 0;0.7;0
    conditions: "mayorQuestStarted,!mayorQuestDone" #(8)!
    check_interval: 20
    max_range: 32

objectives:
  visitOrchard: "location 220;64;-180;world 5 actions:finishMayorQuest" #(9)!

actions:
  startMayorQuest: "folder addMayorStarted,addOrchardObjective,sendOrchardHint" #(10)!
  addMayorStarted: "tag add mayor_quest_started"
  addOrchardObjective: "objective add visitOrchard"
  sendOrchardHint: "notify &6Visit the old orchard outside town."

  finishMayorQuest: "folder addMayorDone,rewardPlayer" #(11)!
  addMayorDone: "tag add mayor_quest_done"
  rewardPlayer: "give reward:3"

conditions:
  mayorQuestStarted: "tag mayor_quest_started" #(12)!
  mayorQuestDone: "tag mayor_quest_done"

items:
  questAvailableIcon: "simple WRITABLE_BOOK title:&6New_Quest" #(13)!
  questReturnIcon: "simple COMPASS title:&aReturn_to_Mayor"
  reward: "simple EMERALD"

conversations:
  mayorQuest:
    quester: "Mayor"
    first: "newQuest,activeQuest,doneQuest" #(14)!

    NPC_options:
      newQuest:
        text: "The old orchard needs checking. Can you go there?"
        pointers: "acceptQuest"
        conditions: "!mayorQuestStarted,!mayorQuestDone"
      activeQuest:
        text: "Please check the old orchard outside town."
        conditions: "mayorQuestStarted,!mayorQuestDone"
      doneQuest:
        text: "Thank you for checking the orchard."
        conditions: "mayorQuestDone"

    player_options:
      acceptQuest:
        text: "I will check it."
        actions: "startMayorQuest"
```

1. Defines the BetonQuest NPC ID. Use this ID in `npc_holograms`, not the raw Citizens ID.
2. Shows the `questAvailableIcon` item as a floating hologram line above the NPC.
3. Attaches the hologram to the `mayor` NPC. Multiple NPC IDs can be separated with commas.
4. Moves the hologram 0.7 blocks above the NPC location.
5. Shows this hologram only while the quest has not been started and not completed.
6. Checks the hologram conditions every 20 ticks.
7. Only shows the hologram to players within 32 blocks.
8. Shows the return hologram while the quest is active but not finished.
9. Completes when the player reaches the old orchard.
10. Starts the quest and changes the player's quest state.
11. Finishes the quest. After the tag is added, neither hologram condition matches anymore.
12. These tag conditions control which hologram the player sees.
13. These items are used by the `item:` hologram lines.
14. The conversation is included because `npc_conversations` must point to a defined conversation.


## Moving NPCs

If the NPC moves around, add `follow: true`:

```yaml
npc_holograms:
  patrolQuest:
    lines:
      - "item:patrolQuestIcon"
      - "&6New Patrol"
    npcs: patrolGuard
    vector: 0;0.7;0
    follow: true
    conditions: "!patrolQuestDone"

items:
  patrolQuestIcon: "simple MAP title:&6Patrol_Quest"
```

Only use `follow: true` for NPCs that actually move.
Following holograms need more updates and can become expensive when many NPCs use them at the same time.
Not every NPC integration supports following holograms.

## Common patterns

Use one hologram for available quests:

```yaml
conditions: "!questStarted,!questDone"
```

Use one hologram for active quests:

```yaml
conditions: "questStarted,!questDone"
```

Use one hologram for completed quests that can be turned in:

```yaml
conditions: "objectiveDone,!questDone"
```

Use `max_range` to avoid showing markers from too far away:

```yaml
max_range: 24
```

## Troubleshooting

- If no hologram appears, check that a supported hologram plugin is installed and hooked.
- If the hologram appears on the wrong NPC, check the BetonQuest NPC ID in the `npcs` section.
- If the hologram never changes, test the conditions with `/bq condition <player> <package>><condition>`.
- If the hologram changes too slowly, lower `check_interval` for that hologram.
- If moving NPC holograms lag, remove `follow: true` or increase update intervals.
