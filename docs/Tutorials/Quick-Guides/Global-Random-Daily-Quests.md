---
icon: material/account-group
---

# How to assign the same random daily quest to every player

Using schedules, you can assign the same random daily quest to every player each day.
Instead of selecting a random quest individually for every player, a global tag determines which quest is 
active for everyone.


The basic idea is:

- A schedule runs once every day at a fixed time.
- The schedule removes the previously selected global quest tag.
- A random folder action selects one new global quest tag.
- When a player logs in, the system checks which global tag is currently active.
- Every player therefore receives the same daily quest until the next scheduled reset.


The following example shows how to assign the same random daily quest to every player using global tags and schedules.

```yaml
schedules:
  resetDailyQuests: #(1)!
    type: realtime-daily 
    time: '04:00' 
    actions: resetAfterSpecificTime,selectRandomQuest #(2)!

objectives:
  questSelection: "login actions:selectRandomQuest auto-once persistent"

  killMobs: "mobkill ZOMBIE 2 notify actions:questCompleted"
  tameWolfs: "tame WOLF 2 actions:questCompleted"
  shearSheeps: "shear 5 actions:questCompleted"

actions:
  selectRandomQuest: "folder selectQuest_1,selectQuest_2,selectQuest_3 random:1 conditions:!alreadyStartedQuest" #(3)!

  # Quest 1
  selectQuest_1: "folder addTagStarted_1,addObjectiveQuest_1,notifyPlayerQuest_1 delay:5 seconds"
  addTagStarted_1: "globaltag add startedQuest_1"
  addObjectiveQuest_1: "objective add killMobs"
  notifyPlayerQuest_1: "notify Go and kill 2 zombies today!"

  # Quest 2
  selectQuest_2: "folder addTagStarted_2,addObjectiveQuest_2,notifyPlayerQuest_2 delay:5 seconds"
  addTagStarted_2: "tag add startedQuest_2"
  addObjectiveQuest_2: "objective add tameWolfs"
  notifyPlayerQuest_2: "notify Go and tame 2 wolfs today!"
  
  # Quest 3
  selectQuest_3: "folder addTagStarted_3,addObjectiveQuest_3,notifyPlayerQuest_3 delay:5 seconds"
  addTagStarted_3: "tag add startedQuest_3"
  addObjectiveQuest_3: "objective add shearSheeps"
  notifyPlayerQuest_3: "notify Go and shear 5 sheeps today!"

  questCompleted: "folder rewardPlayer,sendNotify"
  rewardPlayer: "give reward:5"
  sendNotify: "notify Congratulations! You get a new quest at 1pm"

  resetAfterSpecificTime: "run
          ^globaltag delete startedQuest_1 
          ^globaltag delete startedQuest_2 
          ^globaltag delete startedQuest_3" #(4)!

conditions:
  alreadyStartedQuest: "OR startedQuest_1,startedQuest_2,startedQuest_3"
  startedQuest_1: "globaltag startedQuest_1"
  startedQuest_2: "globaltag startedQuest_2"
  startedQuest_3: "globaltag startedQuest_3"

items:
  reward: "simple diamond"
```

1. This schedule runs every day at 4:00 AM and selects the quest for that day.
2. Clears all previously assigned global quest tags before adding a new one.
3. The **folder action** with the `random` argument can be used to create random daily quests.
4. Removes all tags that may have been added by the quests.
