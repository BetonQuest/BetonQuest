---
icon: material/message-bulleted
---

# Custom Quest notifications

Custom notifications are useful when quest feedback should be more visible than a default chat message.
You can send notifications as chat messages, actionbars, bossbars, titles, sounds, or combinations of these.

The following example creates a small quest flow with different notification styles.
It also defines reusable notification categories, so repeated quest messages can share the same look and sound.

The goal is:

- A chat message confirms that the quest started.
- An actionbar reminds the player what to do.
- A bossbar shows objective progress.
- A title announces quest completion.
- A sound plays together with important feedback.
- Reusable categories keep notification actions short and consistent.

```yaml
notifications:
  quest_info: #(1)!
    io: actionbar
    sound: block.note_block.pling
    soundpitch: 1.2

  quest_progress: #(2)!
    io: bossbar
    barColor: green
    style: progress
    stay: 60

  quest_complete: #(3)!
    io: title
    fadeIn: 10
    stay: 60
    fadeOut: 20
    sound: ui.toast.challenge_complete

objectives:
  gatherLogs: "block oak_log -1 persistent actions:logGathered" #(4)!

actions:
  startQuest: "folder resetProgress,addObjective,notifyQuestStarted,notifyQuestHint" #(5)!
  resetProgress: "point logs 0 action:set"
  addObjective: "objective add gatherLogs"

  notifyQuestStarted: "notify &6Woodland Request started! io:chat sound:entity.experience_orb.pickup" #(6)!
  notifyQuestHint: "notify &eGather 5 oak logs. category:quest_info" #(7)!

  logGathered: "folder addProgress,notifyProgress,checkForCompletion" #(8)!
  addProgress: "point logs 1 action:add"
  notifyProgress: "notify &a%point.logs.amount%&8/&25 &7oak logs gathered. category:quest_progress progress:%math.calc:point.logs.amount/5%" #(9)!

  checkForCompletion: "folder completeQuest conditions:enoughLogs"
  completeQuest: "folder removeObjective,rewardPlayer,notifyQuestComplete,cleanupProgress" #(10)!
  removeObjective: "objective remove gatherLogs"
  rewardPlayer: "give reward:3"
  notifyQuestComplete: "notify &aQuest complete!\\n&75 oak logs gathered. category:quest_complete" #(11)!
  cleanupProgress: "deletepoint logs"

  announceEvent: "notifyall &6A forest quest is now available! io:chat sound:block.bell.use" #(12)!

conditions:
  enoughLogs: "point logs 5" #(13)!

items:
  reward: "simple EMERALD"
```

1. Defines a reusable actionbar category for short quest hints.
2. Defines a reusable bossbar category for progress messages.
3. Defines a reusable title category for completion messages.
4. The persistent objective fires after every broken oak log, so progress can be displayed after each step.
5. Starts the quest, resets old progress, adds the objective, and sends the first notifications.
6. Sends a one-time chat message and plays a pickup sound directly in the action.
7. Uses the `quest_info` category, so the action only needs the message.
8. Runs after every gathered log.
9. Uses a bossbar and calculates the progress from the player's point amount.
10. Completes the quest once enough logs were gathered.
11. Uses the `quest_complete` title category. The `\\n` separates title and subtitle.
12. Uses `notifyall` to broadcast a message to every online player.
13. Checks if the player has gathered at least 5 logs.

Use direct `io:` settings for one-off messages.
Use categories when the same kind of notification appears in multiple actions or quests.
