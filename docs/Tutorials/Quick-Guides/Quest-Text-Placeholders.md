---
icon: material/variable
---

# How to use placeholders in quest text

Placeholders let you insert live quest data into text shown to players.
They are useful when conversations, notifications, journals, and menus should display the player's current state instead of static text.

The following example creates a small gathering quest.
The same progress values are shown in a conversation, a notification, the journal, and a quest menu.

The goal is:

- The NPC greets the player by name.
- Notifications show the current objective progress.
- The journal displays the current task and remaining amount.
- A menu item shows the same live progress.
- Conditions and math placeholders turn quest state into readable text.

```yaml
conversations:
  Forester:
    quester: "Forester"
    first: "questIntro"
    NPC_options:
      questIntro:
        text: "Hello %player%! You have gathered &a%point.logs.amount%&7/&25 &7oak logs so far." #(1)!
        pointers: "acceptQuest,askProgress"
      progressInfo:
        text: "You still need &e%point.logs.left:5% &7oak logs. Quest ready to finish: &e%condition.enoughLogs.papiMode%&7." #(2)!
    player_options:
      acceptQuest:
        text: "I will gather the logs."
        actions: "startLogQuest"
      askProgress:
        text: "How much work is left?"
        pointers: "progressInfo"

journal:
  logQuest:
    "&0Gather 5 oak logs for the forester.\n&0Progress: &2%point.logs.amount%&0/&25\n&0Remaining: &6%point.logs.left:5%" #(3)!

journal_main_page:
  logQuestStatus:
    priority: 10
    text: "&6Woodland Request:&0 %point.logs.amount%/5 logs gathered. Ready: %condition.enoughLogs.papiMode%" #(4)!
    conditions: "logQuestStarted,!logQuestDone"

menus:
  questMenu:
    height: 3
    title: "&6Quests - %player%" #(5)!
    command: "/quests"
    slots:
      13: "logQuestItem"

menu_items:
  logQuestItem:
    item: "oakLogIcon"
    text:
      - "&6Woodland Request"
      - "&7Progress: &a%point.logs.amount%&8/&25"
      - "&7Remaining: &e%point.logs.left:5%"
      - "&7Done: &e%condition.enoughLogs.papiMode%"
      - "&7Progress: &a%math.calc:point.logs.amount/5*100~0% percent" #(6)!
    click: "sendProgressReminder"
    close: false

objectives:
  gatherLogs: "block oak_log -1 persistent actions:logGathered" #(7)!

actions:
  startLogQuest: "folder resetLogProgress,addStartedTag,addJournalEntry,giveJournal,addLogObjective,sendProgressReminder"
  resetLogProgress: "point logs 0 action:set"
  addStartedTag: "tag add log_quest_started"
  addJournalEntry: "journal add logQuest"
  giveJournal: "givejournal"
  addLogObjective: "objective add gatherLogs"

  logGathered: "folder addLogPoint,sendProgressReminder,updateJournal,checkForCompletion" #(8)!
  addLogPoint: "point logs 1 action:add"
  sendProgressReminder: "notify &a%point.logs.amount%&8/&25 &7logs gathered. &e%point.logs.left\\:5% &7left. io:actionbar"
  updateJournal: "journal update"

  checkForCompletion: "folder completeLogQuest conditions:enoughLogs"
  completeLogQuest: "folder addDoneTag,removeLogObjective,sendCompleteTitle,rewardPlayer,cleanupLogProgress,updateJournal" #(10)!
  addDoneTag: "tag add log_quest_done"
  removeLogObjective: "objective remove gatherLogs"
  sendCompleteTitle: "notify &aQuest complete!\\n&7You gathered %point.logs.amount% logs. io:title"
  rewardPlayer: "give reward:3"
  cleanupLogProgress: "deletepoint logs"

conditions:
  logQuestStarted: "tag log_quest_started"
  logQuestDone: "tag log_quest_done"
  enoughLogs: "point logs 5" #(11)!

items:
  oakLogIcon: "simple OAK_LOG"
  reward: "simple EMERALD"
```

1. `%player%` shows the player's name, and `%point.logs.amount%` shows their current point amount.
2. `%point.logs.left:5%` shows how many points are missing until 5, and `%condition.enoughLogs.papiMode%` returns a readable yes/no value.
3. Journal entries can include placeholders, so the same entry can show live quest values.
4. The journal main page can combine placeholders and conditions to show a compact quest status.
5. Menu titles support placeholders, so the menu can include player-specific text.
6. Menu item lore supports placeholders too. The math placeholder calculates the progress percentage from the point amount.
7. The objective completes after each gathered log and then restarts because it is persistent.
8. Every gathered log updates the point counter, sends feedback, refreshes the journal, and checks for completion.
9. Notifications can use the same placeholders as conversations, journals, and menus.
10. The completion title still reads the point amount before the points are cleaned up.
11. This condition is used both as normal quest logic and as text through `%condition.enoughLogs.papiMode%`.

Placeholders are resolved for the player who sees the text.
If a placeholder cannot be resolved, for example because an objective is not active, it may be replaced with an empty value.
