---
icon: material/calendar-question
---

# How to reset random daily quests

Resets for random daily quests can be implemented using different strategies.
The following examples achieve the same goal but differ in how and when quests become available again.
Choose which one fits best for you.

The goal is:

- A tag prevents players from starting another daily quest while one is active.
- A random folder action selects one quest from a pool of available daily quests.
- The selected quest adds the blocking tag when it starts.
- After the quest is completed, the blocking tag is removed either after 24 hours or at a fixed daily reset time.
- Once the tag is removed, the player can receive a new random daily quest.


    
=== "... every 24 hours after completing (with delay objective)"

    ``` yaml
    objectives:
      questSelection: "login actions:selectRandomQuest auto-once persistent" #(1)!
    
      killMobs: "mobkill ZOMBIE 2 notify actions:questCompleted"
      tameWolfs: "tame WOLF 2 actions:questCompleted"
      shearSheeps: "shear 5 actions:questCompleted"
    
      resetAfterTime: delay 1440 actions:resetAfterSpecificTime #(2)!
    
    actions:
      selectRandomQuest: "folder quest_1.selectQuest,quest_2.selectQuest,quest_3.selectQuest random:1 conditions:!alreadyStartedQuest" #(3)!
      
      quest_1:
        selectQuest: "folder quest_1.addTagStarted,quest_1.addObjectiveQuest,quest_1.notifyPlayerQuest delay:5 seconds"
        addTagStarted: "globaltag add startedQuest_1"
        addObjectiveQuest: "objective add killMobs"
        notifyPlayerQuest: "notify Go and kill 2 zombies today!"
    
      quest_2:
        selectQuest: "folder quest_2.addTagStarted,quest_2.addObjectiveQuest,quest_2.notifyPlayerQuest delay:5 seconds"
        addTagStarted: "tag add startedQuest_2"
        addObjectiveQuest: "objective add tameWolfs"
        notifyPlayerQuest: "notify Go and tame 2 wolfs today!"
      
      quest_3:
        selectQuest: "folder quest_3.addTagStarted,quest_3.addObjectiveQuest,quest_3.notifyPlayerQuest delay:5 seconds"
        addTagStarted: "tag add startedQuest_3"
        addObjectiveQuest: "objective add shearSheeps"
        notifyPlayerQuest: "notify Go and shear 5 sheeps today!"
    
      questCompleted: "folder rewardPlayer,sendNotify,addResetCounter" #(4)!
      rewardPlayer: "give reward:5"
      sendNotify: "notify Congratulations! You can get a new task in 24 hours after you relog!"
      
      addResetCounter: "objective add resetAfterTime"
    
      resetAfterSpecificTime: tag delete startedQuest_1,startedQuest_2,startedQuest_3 #(5)!
    
    conditions:
      alreadyStartedQuest: "OR startedQuest_1,startedQuest_2,startedQuest_3"
      startedQuest_1: "tag startedQuest_1"
      startedQuest_2: "tag startedQuest_2"
      startedQuest_3: "tag startedQuest_3"
    
    items:
      reward: "simple diamond"
    ```

    1. When the player logs in, a random daily quest is assigned if they haven't already received one that day.
    2. This is the **delay objective** that triggers every `1440` minutes and removes the quest tags.
    3. The **folder action** with the `random` argument can be used to create random daily quests.
    4. Upon completion, the player is assigned the reset objective (the `delay` objective).
    5. Removes all tags that may have been added by the quests.

=== "... static reset at 13:00 (01:00 PM) (with schedules)"

    ``` yaml
    schedules:
      resetDailyQuests: #(4)!
        type: realtime-daily
        time: '13:00'
        actions: resetAfterSpecificTime
    
    objectives:
      questSelection: "login actions:selectRandomQuest auto-once persistent" #(1)!
    
      killMobs: "mobkill ZOMBIE 2 notify actions:questCompleted"
      tameWolfs: "tame WOLF 2 actions:questCompleted"
      shearSheeps: "shear 5 actions:questCompleted"
    
    actions:
      selectRandomQuest: "folder quest_1.selectQuest,quest_2.selectQuest,quest_3.selectQuest random:1 conditions:!alreadyStartedQuest" #(2)!
      
      quest_1:
        selectQuest: "folder quest_1.addTagStarted,quest_1.addObjectiveQuest,quest_1.notifyPlayerQuest delay:5 seconds"
        addTagStarted: "globaltag add startedQuest_1"
        addObjectiveQuest: "objective add killMobs"
        notifyPlayerQuest: "notify Go and kill 2 zombies today!"
    
      quest_2:
        selectQuest: "folder quest_2.addTagStarted,quest_2.addObjectiveQuest,quest_2.notifyPlayerQuest delay:5 seconds"
        addTagStarted: "tag add startedQuest_2"
        addObjectiveQuest: "objective add tameWolfs"
        notifyPlayerQuest: "notify Go and tame 2 wolfs today!"
      
      quest_3:
        selectQuest: "folder quest_3.addTagStarted,quest_3.addObjectiveQuest,quest_3.notifyPlayerQuest delay:5 seconds"
        addTagStarted: "tag add startedQuest_3"
        addObjectiveQuest: "objective add shearSheeps"
        notifyPlayerQuest: "notify Go and shear 5 sheeps today!"
    
      questCompleted: "folder rewardPlayer,sendNotify"
      rewardPlayer: "give reward:5"
      sendNotify: "notify Congratulations! You get a new quest at 01:00 PM"
    
      resetAfterSpecificTime: "tag delete startedQuest_1,startedQuest_2,startedQuest_3" #(3)!
    
    conditions:
      alreadyStartedQuest: "OR startedQuest_1,startedQuest_2,startedQuest_3"
      startedQuest_1: "tag startedQuest_1"
      startedQuest_2: "tag startedQuest_2"
      startedQuest_3: "tag startedQuest_3"
    
    items:
      reward: "simple diamond"
    ```

    1. When the player logs in, a random daily quest is assigned if they haven't already received one that day.
    2. The **folder action** with the `random` argument can be used to create random daily quests.
    3. Removes all tags that may have been added by the quests.
    4. This is the `schedules` section. It triggers the reset action at 01:00 PM every day.
