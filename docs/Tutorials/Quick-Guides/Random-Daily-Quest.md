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
      selectRandomQuest: "folder selectQuest_1,selectQuest_2,selectQuest_3 random:1 conditions:!alreadyStartedQuest" #(3)!
    
      # Quest 1
      selectQuest_1: "folder addTagStarted_1,addObjectiveQuest_1,notifyPlayerQuest_1 delay:5 seconds"
      addTagStarted_1: "tag add startedQuest_1"
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

=== "... static reset at 1pm (with schedules)"

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
      selectRandomQuest: "folder selectQuest_1,selectQuest_2,selectQuest_3 random:1 conditions:!alreadyStartedQuest" #(2)!
      
      # Quest 1
      selectQuest_1: "folder addTagStarted_1,addObjectiveQuest_1,notifyPlayerQuest_1 delay:5 seconds"
      addTagStarted_1: "tag add startedQuest_1"
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
                ^tag delete startedQuest_1 
                ^tag delete startedQuest_2 
                ^tag delete startedQuest_3" #(3)!
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
    4. This is the `schedules` section. It triggers the reset action at 1pm every day.
