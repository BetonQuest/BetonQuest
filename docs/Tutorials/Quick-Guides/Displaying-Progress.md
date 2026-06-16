---
icon: material/progress-check
---

# How to display `1 / 10` during objective progression

BetonQuest already provides default objective progress notifications.
However, if you want to fully customize the displayed message, you can create a simple tracking system
using points and a repeating objective.

The basic idea is:

* The objective completes after every single progression.
* A point category tracks the current progress.
* A notification displays the current amount using a point placeholder.
* The objective automatically restarts until the desired amount is reached.

The following example displays `1 / 10`, `2 / 10`, and so on while the player breaks stone blocks.

```yaml
objectives:
  mineStone: "block stone -1 persistent actions:blockBroken" #(1)!

actions:
  blockBroken: "folder addPoint,sendNotify,checkForCompletion" #(2)!

  addPoint: "point blockCounter 1" #(3)!
  sendNotify: "notify &a%point.blockCounter.amount%&8/&210 &7stone broken. io:chat" #(4)!

  checkForCompletion: "folder deleteObjective,deletePoint conditions:has10Points" #(5)!

  deleteObjective: "objective remove mineStone" #(6)!
  deletePoint: "deletepoint blockCounter" #(7)!

conditions:
  has10Points: "point blockCounter 10" #(8)!
```

1. The `persistent` objective that should be tracked.
2. The actions that should be executed when ever a block is broken.
3. Increase the point counter by 1 every time a block is broken.
4. Display the current number of points using a point placeholder.
5. Calls the cleanup actions when the player reaches 10 points.
   This is also the place where you can call further actions after the objective is completed.
6. Removes the objective once the player reaches 10 points.
7. Deletes the point counter once the player reaches 10 points.
8. Checks if the player has reached 10 points.
