---
icon: material/counter
tags:
  - Quest-Progress
  - Tracking
  - Points
---

Points are useful when the progress of a quest can be counted. Instead of creating a separate tag for every small
step, you store the current amount in one point category and check that amount with a point condition.

In this tutorial, we will create a small mining quest. A miner asks the player to mine five stone blocks. Every mined
stone adds one point to the player's progress. When the player has five points, the miner can complete the quest.

<div class="grid" markdown>
!!! danger "Requirements"
    It is helpful to be familiar with the basics of actions, objectives, and conditions before using this tutorial.

    * [Actions Tutorial](../../../Tutorials/Getting-Started/Basics/Actions.md)
    * [Objectives Tutorial](../../../Tutorials/Getting-Started/Basics/Objectives.md)
    * [Conditions Tutorial](../../../Tutorials/Getting-Started/Basics/Conditions.md)

!!! example "Related Docs"
    * [Action Point](../../../Documentation/Reference/Actions-List.md#point)
    * [Condition Point](../../../Documentation/Reference/Conditions-List.md#point)
    * [Point Placeholder](../../../Documentation/Reference/Placeholders-List.md#point)
</div>

## 1. Creating the folder structure for the example quest

Add a new structure for the example quest in the `QuestPackage` folder. The name could be "_pointTracking_" for
example.

The file structure should look like this:

* :material-folder-open: pointTracking
    - :material-file: package.yml
    - :material-file: actions.yml
    - :material-file: objectives.yml
    - :material-file: conditions.yml
    - :material-folder-open: conversations
        - :material-file: miner.yml

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Tracking-Quest-Progress/Points/1-ExampleQuest /pointTracking overwrite
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/pointTracking_"

First, we create a simple conversation and connect it to one NPC.

=== "miner.yml"

    ``` yaml linenums="1"
    conversations:
      Miner:
        quester: "Miner"
        first: "startQuest"
        NPC_options:
          startQuest:
            text: "Hey %player%, could you mine five stone blocks for me?"
            pointers: "acceptQuest"
          questStarted:
            text: "Great! Bring me five stone blocks and I will reward you with 3 diamonds."
        player_options:
          acceptQuest:
            text: "Sure, I will do that."
            pointers: "questStarted"
    ```

=== "package.yml"

    ``` yaml linenums="1"
    npcs:
      'Miner': "citizens 21"

    npc_conversations:
      Miner: "Miner"
    ```

This conversation does not start any quest logic yet. The player can only accept the task in the dialogue.

## 2. Store the progress with points

Now we add an objective that reacts to every mined stone block. The objective adds one point to the `stoneMined`
points each time it is completed.


=== "miner.yml"

    ``` yaml hl_lines="13" linenums="1"
    conversations:
      Miner:
        quester: "Miner"
        first: "startQuest"
        NPC_options:
          startQuest:
            text: "Hey %player%, could you mine five stone blocks for me?"
            pointers: "acceptQuest"
          questStarted:
            text: "Great! Bring me five stone blocks and I will reward you with 3 diamonds."
        player_options:
          acceptQuest:
            text: "Sure, I will do that."
            pointers: "questStarted"
            actions: "startMiningQuest"
    ```

=== "actions.yml"

    ``` yaml linenums="1"
    actions:
      startMiningQuest: "folder addMiningQuestStarted,resetMiningProgress,addMineStoneObjective"
      addMiningQuestStarted: "tag add miningQuestStarted"
      resetMiningProgress: "point stoneMined 0 action:set"
      addMineStoneObjective: "objective add mineStone"
      addMiningProgress: "folder addMiningPoint,sendMiningProgress,sendMiningComplete,removeMineStoneObjectiveOnComplete"
      addMiningPoint: "point stoneMined 1 action:add"
      sendMiningProgress: "notify &a%point.stoneMined.amount%&8/&25 &7stone mined. io:chat conditions:!hasMinedEnoughStone"
      sendMiningComplete: "notify &aYou mined enough stone. Return to the Miner! io:chat conditions:hasMinedEnoughStone"
      removeMineStoneObjectiveOnComplete: "objective remove mineStone conditions:hasMinedEnoughStone"
    ```

=== "objectives.yml"

    ``` yaml linenums="1"
    objectives:
      mineStone: "block stone -1 persistent actions:addMiningProgress"
    ```

=== "conditions.yml"

    ``` yaml linenums="1"
    conditions:
      miningQuestStarted: "tag miningQuestStarted"
      hasMinedEnoughStone: "point stoneMined 5"
    ```

The important part is the `stoneMined` point category. The `startMiningQuest` action sets it to `0`, then starts the
objective. Because the objective is `persistent`, it starts again after every mined stone block. Each completion runs
`addMiningProgress`, which adds one point and sends the player a chat message with the current progress. Once the
player reaches five points, `removeMineStoneObjectiveOnComplete` removes the objective so the completion message is
not sent again when the player mines more stone.

The `hasMinedEnoughStone` condition checks whether the player has at least five points in the `stoneMined` category.
That means the point category stores the player's quest progress.

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Tracking-Quest-Progress/Points/2-FullExample /pointTracking overwrite
    ```

## 3. Use the saved progress in the conversation

We can now use the point condition in the conversation. The miner should finish the quest when the player has mined
enough stone. If the player comes back too early, the miner can still read the current progress from the point
placeholder. We also add an `items.yml` file for the reward.

=== "miner.yml"

    ``` yaml hl_lines="4 10-19 23" linenums="1"
    conversations:
      Miner:
        quester: "Miner"
        first: "finishQuest,checkProgress,completedQuest,startQuest"
        NPC_options:
          startQuest:
            text: "Hey %player%, could you mine five stone blocks for me?"
            pointers: "acceptQuest"
            conditions: "!miningQuestStarted,!miningQuestDone"
          questStarted:
            text: "Great! Bring me five stone blocks and I will reward you with 3 diamonds."
          checkProgress:
            text: "You have mined %point.stoneMined.amount% of 5 stone blocks. Keep going!"
            conditions: "miningQuestStarted,!hasMinedEnoughStone"
          finishQuest:
            text: "Perfect, that is enough stone. Here are your 3 diamonds!"
            conditions: "miningQuestStarted,hasMinedEnoughStone"
            actions: "finishMiningQuest"
          completedQuest:
            text: "Thanks again for helping me with the stone."
            conditions: "miningQuestDone"
        player_options:
          acceptQuest:
            text: "Sure, I will do that."
            pointers: "questStarted"
            actions: "startMiningQuest"
    ```

=== "actions.yml"

    ``` yaml hl_lines="6-11 12 15-16" linenums="1"
    actions:
      startMiningQuest: "folder addMiningQuestStarted,resetMiningProgress,addMineStoneObjective"
      addMiningQuestStarted: "tag add miningQuestStarted"
      resetMiningProgress: "point stoneMined 0 action:set"
      addMineStoneObjective: "objective add mineStone"
      addMiningProgress: "folder addMiningPoint,sendMiningProgress,sendMiningComplete,removeMineStoneObjectiveOnComplete"
      addMiningPoint: "point stoneMined 1 action:add"
      sendMiningProgress: "notify &a%point.stoneMined.amount%&8/&25 &7stone mined. io:chat conditions:!hasMinedEnoughStone"
      sendMiningComplete: "notify &aYou mined enough stone. Return to the Miner! io:chat conditions:hasMinedEnoughStone"
      removeMineStoneObjectiveOnComplete: "objective remove mineStone conditions:hasMinedEnoughStone"
      finishMiningQuest: "folder addMiningQuestDone,deleteMiningQuestStarted,rewardPlayer,notifyReward,deleteMiningProgress"
      addMiningQuestDone: "tag add miningQuestDone"
      deleteMiningQuestStarted: "tag delete miningQuestStarted"
      rewardPlayer: "give reward:3"
      notifyReward: "notify &aYou received 3 diamonds for helping the Miner! io:chat"
      deleteMiningProgress: "deletepoint stoneMined"
    ```

=== "items.yml"

    ``` yaml linenums="1"
    items:
      reward: "simple diamond"
    ```

=== "conditions.yml"

    ``` yaml hl_lines="4" linenums="1"
    conditions:
      miningQuestStarted: "tag miningQuestStarted"
      hasMinedEnoughStone: "point stoneMined 5"
      miningQuestDone: "tag miningQuestDone"
    ```

The order in `first` is important. BetonQuest checks `finishQuest` first, so the quest can complete as soon as the
player has enough points. If the player has started the quest but has fewer than five points, `checkProgress` is used
instead. The point placeholder `%point.stoneMined.amount%` shows the stored amount. The `sendMiningProgress` action
uses the same placeholder to show the player their progress in chat, while `sendMiningComplete` tells them when they
are done. The objective is removed at the same moment, so mining more stone will not send the completion message again.

When the quest is completed at the NPC, `finishMiningQuest` removes the active quest tag, gives the reward, sends a
short reward message, and deletes the temporary point category. This keeps the player's data clean after the progress
is no longer needed.

@snippet:tutorials:download-this-part@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Tracking-Quest-Progress/Points/2-FullExample /pointTracking overwrite
    ```

You have now tracked quest progress with points. Use this approach whenever the player can make measurable progress,
for example mining blocks, collecting items, catching fish, gaining reputation, or contributing to a repeated task.
