---
icon: material/stairs-up
tags:
  - Quest-Progress
  - Tracking
  - Stages
---

# Tracking Quest Progress with Stages

Stages are useful when a quest has one clear path and the player should always be in exactly one step of that path.
Instead of creating many tags like `metJoe`, `metFren` and `returnedToBonny`, you define one stage objective and move it
forward whenever the player progresses.

Use stages when:

- the quest has a clear order,
- only one state should be active at a time,
- you want to compare whether a player is before, at, or after a certain step,
- you want to display the current, next, previous, or index value with objective placeholders.

Use tags instead when several independent facts can be true at the same time.
Use points when you need to count amounts.

<div class="grid" markdown>
!!! danger "Requirements"
    You should already understand the basics of actions, objectives, and conditions before using stages.

    * [Actions Tutorial](../../../Tutorials/Getting-Started/Basics/Actions.md)
    * [Objectives Tutorial](../../../Tutorials/Getting-Started/Basics/Objectives.md)
    * [Conditions Tutorial](../../../Tutorials/Getting-Started/Basics/Conditions.md)

!!! example "Related Docs"
    * [Stage Objective](../../../Documentation/Reference/Objectives-List.md#stage)
    * [Stage Action](../../../Documentation/Reference/Actions-List.md#stage)
    * [Stage Condition](../../../Documentation/Reference/Conditions-List.md#stage)
</div>

@snippet:tutorials:download-this-part@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Tracking-Quest-Progress/Stages/1-ExampleQuest /trackingStages overwrite
    ```
    You can now find the starter files for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/trackingStages_"

## 1. Create the stage objective

In this example the player meets Bonny first, then Joe, then Fren, and finally returns to Bonny.
That progress can be represented with one stage objective:

```yaml linenums="1"
objectives:
  townIntroductions: "stage talkToBonny,meetJoe,meetFren,returnToBonny,finished preventCompletion" #(1)!
```

1. The stage names are ordered from first to last. `preventCompletion` keeps the stage objective active even if you
   accidentally increase past the final stage.

When the objective is added to a player, BetonQuest starts it at the first stage, `talkToBonny`.
The stage objective stores the current stage in the player's objective data.

## 2. Create stage actions

You move the player through the stages with the `stage` action.
The action can `set`, `increase`, or `decrease` the current stage.

```yaml linenums="1"
actions:
  startIntroductions: "objective add townIntroductions" #(1)!
  goToJoe: "stage townIntroductions set meetJoe" #(2)!
  goToFren: "stage townIntroductions set meetFren" #(3)!
  goBackToBonny: "stage townIntroductions set returnToBonny" #(4)!
  finishIntroductions: "stage townIntroductions set finished" #(5)!
  resetIntroductions: "objective remove townIntroductions" #(6)!
```

1. Adds the stage objective to the player. This starts them at `talkToBonny`.
2. Sets the current stage to `meetJoe`.
3. Sets the current stage to `meetFren`.
4. Sets the current stage to `returnToBonny`.
5. Sets the current stage to `finished`.
6. Removes the objective if you want to reset this progress.

!!! tip

    `set` is usually the clearest option for story quests because it names the exact stage you want.
    `increase` and `decrease` are useful when the next or previous stage is always correct.

## 3. Create stage conditions

Stage conditions compare the player's current stage with one of the defined stages.
The comparison uses the order from the objective.

```yaml linenums="1"
conditions:
  introductionsActive: "objective townIntroductions" #(1)!

  stageTalkToBonny: "stage townIntroductions = talkToBonny" #(2)!
  stageMeetJoe: "stage townIntroductions = meetJoe"
  stageMeetFren: "stage townIntroductions = meetFren"
  stageReturnToBonny: "stage townIntroductions = returnToBonny"
  stageFinished: "stage townIntroductions = finished"

  hasMetJoe: "stage townIntroductions > meetJoe" #(3)!
  hasReachedFren: "stage townIntroductions >= meetFren" #(4)!
  needsMoreIntroductions: "or stageMeetJoe,stageMeetFren" #(5)!
  joeAlreadyMet: "or stageMeetFren,stageReturnToBonny,stageFinished"
  frenAlreadyMet: "or stageReturnToBonny,stageFinished"
```

1. Checks whether the stage objective is currently active for the player.
2. Checks whether the player is exactly at one stage.
3. Checks whether the player is past `meetJoe`.
4. Checks whether the player is at `meetFren` or any later stage.
5. Uses an `or` condition because the player can only be in one exact stage at a time.

Valid comparison operators are `<`, `<=`, `=`, `!=`, `>=`, and `>`.

## 4. Use stages in conversations

Now the conversations can react to the player's current stage.
The important part is that every NPC only advances the stage that belongs to them.

=== "bonny.yml"

    ```yaml linenums="1"
    conversations:
      Bonny:
        quester: "Bonny"
        first: "finished,returnToBonny,continueIntro,startIntro"
        NPC_options:
          startIntro:
            text: "Hey stranger! Please introduce yourself to Joe and Fren, then come back to me."
            conditions: "!introductionsActive"
            actions: "startIntroductions,goToJoe" #(1)!
          continueIntro:
            text: "You still need to introduce yourself to everyone. Come back when you are done."
            conditions: "needsMoreIntroductions" #(2)!
          returnToBonny:
            text: "Great, you met both of them. Welcome to our town!"
            conditions: "stageReturnToBonny"
            actions: "finishIntroductions" #(3)!
          finished:
            text: "Good to see you again. Everyone knows you now."
            conditions: "stageFinished"
    ```

    1. Starts the stage objective and immediately moves the player to the `meetJoe` stage.
    2. This option is available while the player still needs to meet Joe or Fren.
    3. Moves the player to the final `finished` stage.

=== "joe.yml"

    ```yaml linenums="1"
    conversations:
      Joe:
        quester: "Joe"
        first: "alreadyMetJoe,meetJoe,notReady"
        NPC_options:
          meetJoe:
            text: "Bonny sent you? Nice to meet you. You should talk to Fren next."
            conditions: "stageMeetJoe"
            actions: "goToFren" #(1)!
          alreadyMetJoe:
            text: "We already met. Fren is waiting for you."
            conditions: "joeAlreadyMet"
          notReady:
            text: "You should talk to Bonny first."
            conditions: "!introductionsActive"
    ```

    1. Joe advances the quest from `meetJoe` to `meetFren`.

=== "fren.yml"

    ```yaml linenums="1"
    conversations:
      Fren:
        quester: "Fren"
        first: "alreadyMetFren,meetFren,notReady"
        NPC_options:
          meetFren:
            text: "I heard you are new here. Go back to Bonny when you are ready."
            conditions: "stageMeetFren"
            actions: "goBackToBonny" #(1)!
          alreadyMetFren:
            text: "Bonny wanted to see you again."
            conditions: "frenAlreadyMet"
          notReady:
            text: "Bonny and Joe can help you first."
            conditions: "!hasReachedFren"
    ```

    1. Fren advances the quest from `meetFren` to `returnToBonny`.

## 5. Use stage placeholders

Stage objectives provide useful placeholder properties.
These can be used in notifications, menus, journals, or conversations.

```yaml linenums="1"
actions:
  showCurrentStage: "notify &7Current stage: &e%objective.townIntroductions.current%"
  showNextStage: "notify &7Next stage: &e%objective.townIntroductions.next%"
  showStageIndex: "notify &7Stage index: &e%objective.townIntroductions.index%"
```

Useful properties are:

- `current` - the current stage name
- `next` - the next stage name
- `previous` - the previous stage name
- `index` - the current stage number, starting at `0`

## 6. When to use stages

Stages are best for linear quest progress:

```text
talkToBonny -> meetJoe -> meetFren -> returnToBonny -> finished
```

This keeps your quest state in one place.
You do not need to remember which old tags to delete when the player moves forward.

If your quest can branch into several independent paths, combine stages with tags.
Use the stage for the main chapter and tags for optional facts, side choices, or permanent unlocks.

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Tracking-Quest-Progress/Stages/2-FullExample /trackingStages overwrite
    ```
