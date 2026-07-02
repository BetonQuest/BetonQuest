---
icon: material/calendar-refresh
tags:
  - Quest-Progress
  - Tracking
  - Schedules
---

Schedules are useful when quest progress should change at a fixed server time. A common example is a daily task:
players may finish it once, then BetonQuest resets the saved progress every morning.

In this tutorial, a herbalist gives the player a small daily gathering task. The player can complete it once per day.
At 06:00 server time, a schedule removes the daily progress so the task becomes available again.

<div class="grid" markdown>
!!! danger "Requirements"
    It is helpful to be familiar with actions, objectives, conditions, and tags before using schedules for progress.

    * [Actions Tutorial](../../../Tutorials/Getting-Started/Basics/Actions.md)
    * [Objectives Tutorial](../../../Tutorials/Getting-Started/Basics/Objectives.md)
    * [Conditions Tutorial](../../../Tutorials/Getting-Started/Basics/Conditions.md)
    * [Tags Tutorial](../Tracking-Quest-Progress/Tags.md)

!!! example "Related Docs"
    * [Schedules](../../../Documentation/Advanced/Schedules.md)
    * [Action RunForAll](../../../Documentation/Reference/Actions-List.md#runforall)
    * [Action Tag](../../../Documentation/Reference/Actions-List.md#tag)
    * [Condition Tag](../../../Documentation/Reference/Conditions-List.md#tag)
</div>

## 1. Creating the folder structure for the example quest

Add a new structure for the example quest in the `QuestPackage` folder. The name could be "_dailyHerbs_" for
example.

The file structure should look like this:

* :material-folder-open: dailyHerbs
    - :material-file: package.yml
    - :material-file: actions.yml
    - :material-file: objectives.yml
    - :material-file: conditions.yml
    - :material-file: items.yml
    - :material-folder-open: conversations
        - :material-file: herbalist.yml

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Daily-Repetition/1-Setup /dailyHerbs overwrite
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/dailyHerbs_"

First, we create a simple NPC conversation. The player can ask for work, but the quest does not track anything yet.

=== "herbalist.yml"

    ``` yaml linenums="1"
    conversations:
      Herbalist:
        quester: "Herbalist"
        first: "startQuest"
        NPC_options:
          startQuest:
            text: "The garden grows back every morning. Could you gather three oak logs for today's remedies?"
            pointers: "acceptQuest"
          questStarted:
            text: "Thank you. Bring me three oak logs and I will pay you with an emerald."
        player_options:
          acceptQuest:
            text: "I will gather them."
            pointers: "questStarted"
    ```

=== "package.yml"

    ``` yaml linenums="1"
    npcs:
      HerbalistNpc: "citizens 31" #(1)!

    npc_conversations:
      HerbalistNpc: "Herbalist" #(2)!
    ```

    1. Replace `31` with the Citizens ID of your Herbalist NPC.
    2. This connects the NPC ID from `npcs` with the `Herbalist` conversation.

## 2. Track one daily completion

Now we add the actual task. The player starts an objective and completes it by breaking three oak logs. When the
objective is complete, it adds the `dailyHerbsReady` tag. The NPC also checks whether the player has three oak logs in
their inventory. After the player hands in the logs and receives the reward, the `dailyHerbsDone` tag is added. That
tag is the saved progress: it means "this player already completed today's task".

=== "herbalist.yml"

    ``` yaml hl_lines="4 7 11-16 21" linenums="1"
    conversations:
      Herbalist:
        quester: "Herbalist"
        first: "finishQuest,questActive,alreadyDone,startQuest" #(1)!
        NPC_options:
          startQuest:
            text: "The garden grows back every morning. Could you gather three oak logs for today's remedies?"
            pointers: "acceptQuest"
            conditions: "!dailyHerbsStarted,!dailyHerbsDone" #(2)!
          questStarted:
            text: "Thank you. Bring me three oak logs and I will pay you with an emerald."
          questActive:
            text: "You still need to gather and bring three oak logs for me."
            conditions: "dailyHerbsStarted,!hasDailyHerbs" #(3)!
          finishQuest:
            text: "These are perfect. Here is your emerald."
            conditions: "dailyHerbsStarted,hasDailyHerbs" #(4)!
            actions: "finishDailyHerbs" #(5)!
          alreadyDone:
            text: "You already helped me today. Come back after the morning reset."
            conditions: "dailyHerbsDone" #(6)!
        player_options:
          acceptQuest:
            text: "I will gather them."
            pointers: "questStarted"
            actions: "startDailyHerbs" #(7)!
    ```

    1. BetonQuest checks the options from left to right. The finish option must be checked before the active and start options.
    2. The player may only start when they do not already have the active or completed daily tags.
    3. This option is shown after the player accepted the task but before the objective is complete.
    4. The quest can finish only while it is active and the objective condition is true.
    5. This action gives the reward, marks the daily as done, and removes temporary active progress.
    6. This blocks another completion until the scheduled reset removes the tag.
    7. This starts the objective and stores that the player has an active daily task.

=== "actions.yml"

    ``` yaml linenums="1"
    actions:
      startDailyHerbs: "folder addDailyHerbsStarted,addGatherLogsObjective" #(1)!
      addDailyHerbsStarted: "tag add dailyHerbsStarted" #(2)!
      addGatherLogsObjective: "objective add gatherDailyLogs" #(3)!
      addDailyHerbsReady: "tag add dailyHerbsReady"
      finishDailyHerbs: "folder takeDailyHerbs,rewardDailyHerbs,addDailyHerbsDone,deleteDailyHerbsStarted,deleteDailyHerbsReady,removeGatherLogsObjective,notifyDailyDone" #(4)!
      takeDailyHerbs: "take dailyHerbs:3 abort" #(5)!
      rewardDailyHerbs: "give dailyReward:1"
      addDailyHerbsDone: "tag add dailyHerbsDone" #(6)!
      deleteDailyHerbsStarted: "tag delete dailyHerbsStarted"
      deleteDailyHerbsReady: "tag delete dailyHerbsReady" #(7)!
      removeGatherLogsObjective: "objective remove gatherDailyLogs" #(8)!
      notifyDailyDone: "notify &aDaily task completed. You can do it again after 06:00 server time. io:chat"
    ```

    1. A folder runs multiple smaller actions as one named action.
    2. This tag means the player accepted the daily task.
    3. This gives the player the block objective.
    4. The finish action groups item hand-in, reward, cleanup, and notification.
    5. Takes the three oak logs from the player's inventory before the reward is given. `abort` prevents partial item removal if something changed.
    6. This tag is the saved daily completion.
    7. The ready tag is temporary and is deleted after the reward was claimed.
    8. The objective is removed after completion so it does not stay in the player's data.

=== "objectives.yml"

    ``` yaml linenums="1"
    objectives:
      gatherDailyLogs: "block OAK_LOG -3 notify actions:addDailyHerbsReady" #(1)!
    ```

    1. Negative block amounts mean the player must break blocks. `notify` shows progress, and `actions:addDailyHerbsReady`
       runs when the objective is completed.

=== "conditions.yml"

    ``` yaml linenums="1"
    conditions:
      dailyHerbsStarted: "tag dailyHerbsStarted" #(1)!
      dailyHerbsDone: "tag dailyHerbsDone" #(2)!
      dailyHerbsReady: "tag dailyHerbsReady" #(3)!
      hasDailyHerbItems: "item dailyHerbs:3" #(4)!
      hasDailyHerbs: "and dailyHerbsReady,hasDailyHerbItems" #(5)!
    ```

    1. Checks whether the player currently has the active daily tag.
    2. Checks whether the player already completed today's daily task.
    3. Checks whether the objective already added the temporary ready tag.
    4. Checks whether the player currently has three oak logs in their inventory.
    5. Combines both requirements: the player must have completed the objective and still have the logs to hand in.

=== "items.yml"

    ``` yaml linenums="1"
    items:
      dailyHerbs: "simple oak_log" #(1)!
      dailyReward: "simple emerald" #(2)!
    ```

    1. This item is used by the item condition and the take action.
    2. This item is given as the reward.

The `dailyHerbsDone` tag prevents the player from starting the same daily task again. At this point, the quest can be
completed once, but it never resets.

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Daily-Repetition/2-QuestProgress /dailyHerbs overwrite
    ```

## 3. Reset the daily progress with a schedule

We now add a `schedules` section to `package.yml`. The schedule runs every day at 06:00 and calls one action:
`resetDailyHerbs`.

=== "package.yml"

    ``` yaml hl_lines="7-11" linenums="1"
    npcs:
      HerbalistNpc: "citizens 31"

    npc_conversations:
      HerbalistNpc: "Herbalist"

    schedules:
      resetDailyHerbs: #(1)!
        type: realtime-daily #(2)!
        time: '06:00' #(3)!
        actions: resetDailyHerbs #(4)!
    ```

    1. This is the schedule name. Use a unique name inside the package.
    2. `realtime-daily` runs once every day at the configured real-world server time.
    3. The time must be quoted and uses the server machine's time zone.
    4. This action is called without a specific player.

=== "actions.yml"

    ``` yaml hl_lines="11-14" linenums="1"
    actions:
      startDailyHerbs: "folder addDailyHerbsStarted,addGatherLogsObjective"
      addDailyHerbsStarted: "tag add dailyHerbsStarted"
      addGatherLogsObjective: "objective add gatherDailyLogs"
      addDailyHerbsReady: "tag add dailyHerbsReady"
      finishDailyHerbs: "folder takeDailyHerbs,rewardDailyHerbs,addDailyHerbsDone,deleteDailyHerbsStarted,deleteDailyHerbsReady,removeGatherLogsObjective,notifyDailyDone"
      takeDailyHerbs: "take dailyHerbs:3 abort"
      rewardDailyHerbs: "give dailyReward:1"
      addDailyHerbsDone: "tag add dailyHerbsDone"
      deleteDailyHerbsStarted: "tag delete dailyHerbsStarted"
      deleteDailyHerbsReady: "tag delete dailyHerbsReady"
      removeGatherLogsObjective: "objective remove gatherDailyLogs"
      notifyDailyDone: "notify &aDaily task completed. You can do it again after 06:00 server time. io:chat"
      resetDailyHerbs: "folder deleteDailyHerbsTags,removeDailyHerbsObjectives,announceDailyHerbsReset" #(1)!
      deleteDailyHerbsTags: "tag delete dailyHerbsStarted,dailyHerbsReady,dailyHerbsDone" #(2)!
      removeDailyHerbsObjectives: "objective remove gatherDailyLogs" #(3)!
      announceDailyHerbsReset: "notifyall &aThe Herbalist has fresh daily work available. io:chat" #(4)!
    ```

    1. The schedule calls only this action, and the folder groups the full reset.
    2. Because this runs from a schedule, it removes these tags for all players.
    3. This removes unfinished daily objectives for all players.
    4. `notifyall` is player independent and can be used directly from a schedule.

Schedules are player independent. That is why the reset action uses actions that can work without a specific player.
When `tag delete` and `objective remove` are called by a schedule, they remove the data for all players, including
offline players. `notifyall` announces the reset to online players.

!!! warning

    The schedule time uses the real server time, not Minecraft day time. Always write the time in quotes, for example
    `'06:00'`.

@snippet:tutorials:download-this-part@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Daily-Repetition/3-FullExample /dailyHerbs overwrite
    ```

You have now created a daily quest with progress that resets at a fixed server time. Use this pattern when every
player should get a fresh chance at the same time, for example daily chores, shop restocks, bounty boards, or rotating
NPC requests.
