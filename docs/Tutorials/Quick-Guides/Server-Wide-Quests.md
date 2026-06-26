---
icon: material/earth
---

# How to create server-wide quests

Server-wide quests allow all players to contribute towards the same objective.
Every player's progress is added to a shared globalpoint counter until the required amount is reached.

The following example shows how to create a server-wide quest where all players work together to catch 100 cod.

The basic idea is:

- Every player automatically receives the same persistent objective.
- Each completed objective increases a shared globalpoint counter.
- The objective immediately restarts, allowing players to contribute multiple times.
- Once the shared counter reaches the required amount, the server-wide quest is completed.
- Every player must contribute a minimum amount to participate.
- Players who contributed receive their reward.


```yaml
objectives:
  catchFish: "fish cod 1 actions:addGlobalPoint,addPersonalPoints,checkForCompletion persistent" #(1)!
  loginInitialization: "login actions:addCatchObjective,showGlobalCounterLoop auto-once conditions:active" #(2)!

actions:
  startGlobalFishEvent: "folder initializePoints,addTagActive,addCatchObjective,sendNotifyToAllPlayers,showGlobalCounterLoop" #(3)!

  initializePoints: "globalpoint fish 0 action:set" #(4)!
  addCatchObjective: "objective add catchFish"
  sendNotifyToAllPlayers: "notifyall &4A fishing event started! The server goal is to catch 100 cod. (You need to catch least 10 cod to participate)"
  addTagActive: "globaltag add active" #(5)!

  addGlobalPoint: "globalpoint fish 1 action:add" #(6)!
  addPersonalPoints: "point fish 1 action:add" #(7)!

  stopGlobalFishEvent: "runforall actions:deleteGlobalPoints,deletePersonalPoints,deleteCatchObjective,sendNotifyOnResetting,deleteTagActive"

  deleteGlobalPoints: "deleteglobalpoint fish"
  deletePersonalPoints: "deletepoint fish"
  deleteCatchObjective: "objective delete catchFish"
  sendNotifyOnResetting: "notifyall &4The fishing event has been stopped. Wait for more instructions."
  deleteTagActive: "globaltag delete active"

  showGlobalCounterLoop: "folder showGlobalCounter,showGlobalCounterLoop period:70 unit:ticks stay:70 conditions:active" #(8)!
  showGlobalCounter: "notifyall &2Server Counter: &6%globalpoint.fish.amount% of 100 cod caught. io:bossbar progress:%math.calc:globalpoint.fish.amount/100%" #(9)!

  checkForCompletion: "runforall actions:participated,notifyOnCompleting,deleteTagActive,deletePersonalPoints,deleteGlobalPoints conditions:serverGoal" #(10)!
  participated: "folder rewardPlayer,notifyForReward delay:3 seconds conditions:participated" #(11)!
  rewardPlayer: "give reward:5"
  notifyForReward: "notify &3You received 5 diamonds for participating at the fishing event!"
  notifyOnCompleting: "notifyall &4Thanks for participating! The fishing event is over."

conditions:
  active: "globaltag active"
  participated: "point fish 10" #(12)!
  serverGoal: "globalpoint fish 100" #(13)!

items:
  cod: "simple cod"
  reward: "simple diamond"
```

1. Every player continuously contributes to the server-wide objective. The persistent flag automatically restarts the objective after every caught cod.
2. Players joining while the event is active automatically receive the fishing objective and the progress display.
3. Initializes the event by resetting the global counter, enabling the event, assigning the objective, and notifying all players.
4. Resets the shared globalpoint counter to 0 before a new event starts.
5. Enables the event by adding a global tag. This tag is used to determine whether the event is currently active.
6. Increases the shared globalpoint counter whenever a player catches a cod. This counter tracks the combined server progress.
7. Tracks each player's individual contribution. This is later used to determine who is eligible for a reward.
8. Repeats the progress display every few ticks while the event is active.
9. Displays the current server progress as a boss bar using the shared globalpoint counter.
10. Once the server reaches the goal of 100 cod, all players are checked for rewards, the event is ended, and all temporary data is cleaned up.
11. Only players that contributed at least the required amount receive the reward.
12. Requires players to catch at least 10 cod to be eligible for the event reward.
13. The server-wide quest is completed when the shared globalpoint counter reaches 100.
