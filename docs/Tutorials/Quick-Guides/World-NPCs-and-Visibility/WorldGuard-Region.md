---
icon: material/map-marker-radius
---

# How to use WorldGuard regions in quests

WorldGuard regions are useful when quest progress should depend on where the player is.
You can start actions when a player enters or leaves a region, and you can use region conditions to only allow actions while the player is inside a specific area.

The following example creates a scout quest for a WorldGuard region named `bandit_camp`.
The player accepts the quest, enters the region, and can only complete the scout report while standing inside that region.
The report action can be called from a menu, a command objective, a bound item, or an NPC placed inside the region.

The goal is:

- A quest starts outside the region.
- Entering the WorldGuard region triggers quest progress.
- A condition checks whether the player is currently inside the region.
- The player can only complete the report while inside the region.
- Leaving the region before completion gives a reminder.

```yaml
conversations:
  ScoutCaptain:
    quester: "Scout Captain"
    first: "offerQuest,activeQuest,finishedQuest"
    NPC_options:
      offerQuest:
        text: "I need someone to scout the bandit camp. Can you get close enough to report back?"
        conditions: "!scoutStarted,!scoutDone"
        pointers: "acceptScoutQuest,declineScoutQuest"
      activeQuest:
        text: "The camp is still unscouted. Go to the bandit camp and send your report from inside the region."
        conditions: "scoutStarted,!scoutDone"
      finishedQuest:
        text: "Good work. Your report helped us plan the next move."
        conditions: "scoutDone"
    player_options:
      acceptScoutQuest:
        text: "I will scout the camp."
        actions: "startScoutQuest"
      declineScoutQuest:
        text: "Not right now."

objectives:
  enterBanditCamp: "region bandit_camp entry actions:enteredBanditCamp" #(1)!
  leaveBanditCamp: "region bandit_camp exit actions:leftBanditCamp" #(2)!

actions:
  startScoutQuest: "folder addScoutStarted,addEnterObjective,notifyStart" #(3)!
  addScoutStarted: "tag add scout_started"
  addEnterObjective: "objective add enterBanditCamp"
  notifyStart: "notify &6Reach the bandit camp and scout it from inside the region."

  enteredBanditCamp: "folder addCampEntered,addLeaveObjective,notifyEntered" #(4)!
  addCampEntered: "tag add bandit_camp_entered"
  addLeaveObjective: "objective add leaveBanditCamp"
  notifyEntered: "notify &aYou entered the bandit camp. Send your report before leaving."

  trySendScoutReport: "folder completeScoutReport,wrongLocationReminder" #(5)!
  completeScoutReport: "folder addScoutDone,removeRegionObjectives,rewardPlayer,notifyComplete conditions:inBanditCamp,scoutStarted,!scoutDone" #(6)!
  wrongLocationReminder: "notify &cYou must stand inside the bandit camp region to send this report. conditions:!inBanditCamp,scoutStarted,!scoutDone"

  addScoutDone: "tag add scout_done"
  removeRegionObjectives: "folder removeEnterObjective,removeLeaveObjective"
  removeEnterObjective: "objective remove enterBanditCamp"
  removeLeaveObjective: "objective remove leaveBanditCamp"
  rewardPlayer: "give reward:3"
  notifyComplete: "notify &aScout report sent. Return to the captain."

  leftBanditCamp: "notify &eYou left the bandit camp. Go back inside if you still need to send the report. conditions:scoutStarted,!scoutDone" #(7)!

conditions:
  scoutStarted: "tag scout_started"
  scoutDone: "tag scout_done"
  inBanditCamp: "region bandit_camp" #(8)!
  campEntered: "tag bandit_camp_entered"

items:
  reward: "simple EMERALD"
```

1. The `region` objective completes when the player enters the WorldGuard region named `bandit_camp`.
2. This objective completes when the player leaves the same region.
3. Starts the quest and adds the entry objective.
4. Runs when the player enters the region, marks that progress, and starts watching for region exit.
5. Calls both possible report outcomes. Conditions decide which one actually runs. Use this action from the interaction that should submit the report.
6. Completes the report only while the player is inside `bandit_camp`.
7. Warns the player if they leave the region before finishing the scout report.
8. The WorldGuard region condition is true while the player is standing inside the region.

The region name must match an existing WorldGuard region.
Use the `region` objective when entering or leaving an area should trigger progress.
Use the `region` condition when an action, menu option, or conversation option should only work while the player is currently inside an area.
