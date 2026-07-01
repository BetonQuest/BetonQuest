---
icon: material/view-dashboard-edit
---

# How to create a quest menu

Menus are useful when players should see available, active, and completed quests in one place.
Each slot can contain multiple menu items, and BetonQuest will display the first item whose conditions are met.

The following example creates a small quest menu with two quests.
Players can open it with `/quests` or by clicking a quest item.

The goal is:

- A menu can be opened by command or by a bound item.
- Each quest has a red, orange, and green menu item.
- Clicking a red item starts the quest.
- Clicking an orange item reminds the player what to do.
- Green items show completed quests.
- The visible item changes automatically after clicking because the conditions are checked again.

```yaml
menus:
  questMenu:
    height: 3
    title: "&6&lQuests"
    command: "/quests" #(1)!
    bind: "questMenuItem" #(2)!
    slots:
      0-8: "filler,filler,filler,filler,filler,filler,filler,filler,filler" #(3)!
      11: "coalNotStarted,coalActive,coalDone" #(4)!
      15: "wolfNotStarted,wolfActive,wolfDone" #(5)!
      18-26: "filler,filler,filler,filler,filler,filler,filler,filler,filler"

menu_items:
  coalNotStarted:
    item: "coalLocked"
    conditions: "!coalStarted,!coalDone" #(6)!
    text:
      - "&4[Quest] &fMiner's Request"
      - "&7Collect coal for the miner."
      - ""
      - "&eClick to accept this quest."
    click: "startCoalQuest"
    close: false
  coalActive:
    item: "coalActiveItem"
    conditions: "coalStarted,!coalDone" #(7)!
    text:
      - "&6[Quest] &fMiner's Request"
      - "&7Mine &f10 coal ore&7."
      - ""
      - "&eClick for a reminder."
    click: "coalReminder"
    close: false
  coalDone:
    item: "questDone"
    conditions: "coalDone" #(8)!
    text:
      - "&2[Quest] &fMiner's Request"
      - "&aCompleted!"
    close: false

  wolfNotStarted:
    item: "wolfLocked"
    conditions: "!wolfStarted,!wolfDone"
    text:
      - "&4[Quest] &fForest Trouble"
      - "&7Clear the forest path."
      - ""
      - "&eClick to accept this quest."
    click: "startWolfQuest"
    close: false
  wolfActive:
    item: "wolfActiveItem"
    conditions: "wolfStarted,!wolfDone"
    text:
      - "&6[Quest] &fForest Trouble"
      - "&7Defeat &f3 wolves&7."
      - ""
      - "&eClick for a reminder."
    click: "wolfReminder"
    close: false
  wolfDone:
    item: "questDone"
    conditions: "wolfDone"
    text:
      - "&2[Quest] &fForest Trouble"
      - "&aCompleted!"
    close: false

  filler:
    item: "fillerItem"
    text: "&7 "
    close: false

objectives:
  mineCoal: "block coal_ore -10 notify actions:finishCoalQuest" #(9)!
  huntWolves: "mobkill WOLF 3 notify actions:finishWolfQuest" #(10)!

actions:
  giveQuestMenuItem: "give questMenuItem" #(11)!

  startCoalQuest: "folder addCoalStarted,addCoalObjective,coalReminder" #(12)!
  addCoalStarted: "tag add coal_started"
  addCoalObjective: "objective add mineCoal"
  coalReminder: "notify &6Mine 10 coal ore for the miner."

  finishCoalQuest: "folder addCoalDone,rewardCoalQuest" #(13)!
  addCoalDone: "tag add coal_done"
  rewardCoalQuest: "give reward:2"

  startWolfQuest: "folder addWolfStarted,addWolfObjective,wolfReminder" #(14)!
  addWolfStarted: "tag add wolf_started"
  addWolfObjective: "objective add huntWolves"
  wolfReminder: "notify &6Defeat 3 wolves near the old forest path."

  finishWolfQuest: "folder addWolfDone,rewardWolfQuest"
  addWolfDone: "tag add wolf_done"
  rewardWolfQuest: "give reward:3"

conditions:
  coalStarted: "tag coal_started"
  coalDone: "tag coal_done"
  wolfStarted: "tag wolf_started"
  wolfDone: "tag wolf_done"

items:
  questMenuItem: "simple COMPASS title:&6Quest_Menu"
  fillerItem: "simple GRAY_STAINED_GLASS_PANE"
  coalLocked: "simple COAL_ORE"
  coalActiveItem: "simple COAL"
  wolfLocked: "simple BONE"
  wolfActiveItem: "simple IRON_SWORD"
  questDone: "simple LIME_CONCRETE"
  reward: "simple EMERALD"
```

1. Allows players to open the menu with `/quests`.
2. Allows players to open the menu by clicking the `questMenuItem`.
3. Fills the top and bottom rows with decorative glass panes.
4. Slot 11 can show one of three coal quest states. The first item whose conditions are met will be displayed.
5. Slot 15 uses the same pattern for the wolf quest.
6. The red item is shown while the quest has not been started and not completed.
7. The orange item is shown while the quest is active.
8. The green item is shown after the quest has been completed.
9. Completing the coal objective calls the action that marks the quest as done.
10. Completing the wolf objective calls the action that marks the quest as done.
11. Gives the player the item that opens the menu.
12. Starts the coal quest, adds the objective, and sends a reminder. After the click, the menu automatically shows the active item.
13. Marks the coal quest as completed and gives the reward.
14. Starts the wolf quest using the same structure as the coal quest.
