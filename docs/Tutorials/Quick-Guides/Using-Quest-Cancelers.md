---
icon: material/cancel
---

# How to cleanly cancel quests

Quest cancelers let players abandon active quests from the quest backpack or with `/cancelquest`.
They are also useful because they clean up quest data in one place:
objectives, tags, points, journal entries, actions, and even a teleport location.

The following example shows a quest where the player searches ruins for relics.
If the player cancels the quest, all temporary progress is removed.

The goal is:

- The quest can only be canceled while it is active.
- Active objectives are removed without completing them.
- Temporary tags, points, and journal entries are deleted.
- A cancel action notifies the player.
- The same canceler can also be triggered manually with the `cancel` action.

```yaml
cancel:
  ruinRelics:
    name: "&6Ruin Relics" #(1)!
    conditions: "relicQuestStarted,!relicQuestCompleted" #(2)!
    objectives: "collectRelics,returnToArchaeologist" #(3)!
    tags: "relic_quest_started,relic_quest_completed" #(4)!
    points: "relicProgress" #(5)!
    journal: "relic_started,relic_found" #(6)!
    actions: "notifyCanceled" #(7)!
    location: "120;65;-40;world" #(8)!

journal:
  relic_started:
    en-US: "&0The archaeologist asked me to recover 3 relics from the old ruins."
  relic_found:
    en-US: "&0I found all relics. I should return to the archaeologist."

objectives:
  collectRelics: "block suspicious_sand -3 notify actions:foundRelic" #(9)!
  returnToArchaeologist: "location 100;65;-20;world 3 actions:finishRelicQuest"

actions:
  startRelicQuest: "folder addStartedTag,addStartedJournal,addCollectObjective,resetRelicProgress" #(10)!
  addStartedTag: "tag add relic_quest_started"
  addStartedJournal: "journal add relic_started"
  addCollectObjective: "objective add collectRelics"
  resetRelicProgress: "point relicProgress 0 action:set"

  foundRelic: "folder addRelicProgress,replaceJournal,addReturnObjective"
  addRelicProgress: "point relicProgress 3 action:set"
  replaceJournal: "folder removeStartedJournal,addFoundJournal"
  removeStartedJournal: "journal delete relic_started"
  addFoundJournal: "journal add relic_found"
  addReturnObjective: "objective add returnToArchaeologist"

  finishRelicQuest: "folder addCompletedTag,rewardPlayer,cleanupAfterCompletion conditions:hasAllRelics" #(11)!
  addCompletedTag: "tag add relic_quest_completed"
  rewardPlayer: "give reward:5"
  cleanupAfterCompletion: "folder deleteRelicProgress,removeFoundJournal"
  deleteRelicProgress: "deletepoint relicProgress"
  removeFoundJournal: "journal delete relic_found"

  notifyCanceled: "notify &cYou abandoned the Ruin Relics quest." #(12)!
  forceCancelRelicQuest: "cancel ruinRelics bypass" #(13)!

conditions:
  relicQuestStarted: "tag relic_quest_started"
  relicQuestCompleted: "tag relic_quest_completed"
  hasAllRelics: "point relicProgress 3"

items:
  reward: "simple emerald"
```

1. The name shown in the quest cancel GUI.
2. The canceler is only available while the quest is started and not completed.
3. These objectives are removed without running their completion actions.
4. These tags are deleted when the quest is canceled.
5. The player's temporary progress points are deleted.
6. These journal entries are removed from the player's journal.
7. This action runs after the quest is canceled.
8. The player is teleported to this location after canceling.
9. The quest objective that creates temporary progress.
10. Starts the quest and creates the data that the canceler will later clean up.
11. Normal quest completion should clean up temporary data that should not remain after finishing.
12. Sends feedback when the player cancels the quest.
13. Calls the same canceler from an action. `bypass` ignores the canceler conditions.
