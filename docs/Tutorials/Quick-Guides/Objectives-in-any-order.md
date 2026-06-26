---
icon: material/swap-horizontal
---

# How to allow objectives in any order

Some quests require players to complete multiple objectives before receiving their reward.
By default, it may seem like these objectives need to be completed in a specific order.

The following example shows how to allow players to complete three objectives 
in any order while ensuring the reward is only granted once all objectives have been completed.

The goal is:

- The player receives multiple objectives at the same time.
- Each objective calls the same reward action when completed.
- The reward action first checks whether any objectives are still active.
- The reward is only granted once none of the objectives remain active.


```yaml
objectives:
  visitTown: "location 100;100;100;world 3 actions:visitTown.notifyPlayer,visitTown.rewardPlayer,checkForCompletion" #(1)!
  catchFish: "fish cod 5 actions:catchFish.notifyPlayer,catchFish.rewardPlayer,checkForCompletion" #(2)!
  tameCat: "tame cat 1 actions:tameCat.notifyPlayer,tameCat.rewardPlayer,checkForCompletion" #(3)!

actions:
  startTheQuest: "objective add visitTown,catchFish,tameCat"

  visitTown:
    notifyPlayer: "notify Good job! Now you know where the town is located!"
    rewardPlayer: "give reward:5"

  catchFish:
    notifyPlayer: "notify Well done, you caught 5 cod!"
    rewardPlayer: "give reward:3"

  tameCat:
    notifyPlayer: "notify Congratulations! You have a new friend."
    rewardPlayer: "give reward:8"

  checkForCompletion: "folder sendNotify delay:2 conditions:completedAllObjectives" #(4)!
  sendNotify: "notify &2You have completed all tasks! Get back to James for a new task."

conditions:
  completedAllObjectives: "AND !hasObjectiveVisitTown,!hasObjectiveCatchFish,!hasObjectiveTameCat" #(5)!
  
  hasObjectiveVisitTown: "objective visitTown" #(6)!
  hasObjectiveCatchFish: "objective catchFish"
  hasObjectiveTameCat: "objective tameCat"

items:
  cod: "simple cod"
  reward: "simple diamond"
```

1. This objective can be completed in any order. Like all other objectives, it checks whether every required objective has been completed.
2. This objective can be completed in any order. Like all other objectives, it checks whether every required objective has been completed.
3. This objective can be completed in any order. Like all other objectives, it checks whether every required objective has been completed.
4. After each completed objective, the shared completion condition is checked before notifying the player.
5. The condition is only true once none of the objectives remain active, meaning they have all been completed.
6. Checks whether the player is still working on the corresponding objective. The ! operator inverts the result, so the combined AND condition only succeeds when all objectives are finished.
