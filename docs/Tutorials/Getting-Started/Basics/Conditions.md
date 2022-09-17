---
icon: octicons/question-16
tags:
- Conditions-Tutorial
---
Now that you know the basics of conversations, events an objectives we can move on to the conditions part.
Conditions allow you to nearly do everything you want. From a simple inventory check if the player has the right
item amount to have multiply answers in a conversation based on the time of the day.
As the name "condition" suggests, it is possible to attach conditions to any conversation, event or objective.
A condition can be either true or false!
In this tutorial you will grant knowledge to create your own conditions and how to use them!

<div class="grid" markdown>
!!! danger "Requirements"
    * [Conversations Tutorial](Conversations.md)
    * [Events Tutorial](Events.md)
    * [Objectives Tutorial](Objectives.md)

!!! example "Related Docs"
    * [Conditions Reference](../../../Documentation/Reference.md#conditions)
    * [Conditions List](../../../Documentation/Conditions-List.md)
</div>
--8<-- "Tutorials/download-setup-warning.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conditions/1-DirectoryStructure /tutorialQuest
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"

## 1. Creating the folder structure for your first condition

Add a new file to your "_tutorialQuest_" `QuestPackage` named "_conditions.yml_".
The file structure should look like this now:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: events.yml
    - :material-file: objectives.yml
    - :material-file: {==conditions.yml==}
    - :material-folder-open: conversations
        - :material-file: jack.yml
        - :material-file: blacksmith.yml

We now have our file structure in place and can begin creating conditions!

## 2. Defining your first condition

Now that we've built it, open `conditions.yml` and add the following content:

``` YAML title="conditions.yml" linenums="1"
conditions: # (1)!
  isNight: "time 6-18"
```

1. All conditions must be defined in a section called `conditions`.

So, what are we looking at here?

* `isNight`  is the name of the condition. You can name it whatever you want to. It is recommended to name
  it after what it should check.
*  The Condition Instruction.
  - `time 6-18` The first value in the instruction is always the **condition type**. After the instruction you have to specify the timespan. If you want to check if it is night, you have to negate the condition!

Now we've created your first condition that checks if a specific time is given on the server. Save the file and continue with the next step!

## 3. Checking the condition in-game

You can check conditions if they are true or false in-game.

!!! warning ""
It is very important to save all files everytime you test something!
Type `/bq reload` on your server after saving.

Running a command is the simplest way to accomplish this:

Enter `/bq condition NAME tutorialQuest.isNight` on the server.
This command will show you the result "false" or "true" in your chat dependent on what time it is.
If it is day it should show true and if it is night, false.

| Command Part     | Meaning                                                                                                                    |
|------------------|----------------------------------------------------------------------------------------------------------------------------|
| `/bq conditions` | Tells BetonQuest that some conditions should be checked if true or false.                                                  |
| `NAME`           | A player's name.                                                                                                           |
| `tutorialQuest`  | The name of a QuestPackage. This is required because you could have events with the same name in different packages.       |
| `isNight`        | The name of the condition to check. Don't forget to separate it with a dot from the package `tutorialQuest{==.==}isNight`. |


--8<-- "Tutorials/download-solution.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conditions/2-FirstCondition /tutorialQuest
    ```

## 4. Integrating conditions into objectives

We now add a condition to the objective so that the objective can only be completed if the condition is true.
In this case we want to achieve that the player is only able to make progress when he's fishing at night and have the hook
at a specific location.


``` YAML title="objectives.yml"
objectives:
  fishingObj: "fish COD 10 hookLocation:100;50;100;world range:20 events:caughtAllFish {==conditions:!isNight==}"
```

We've added the condition `isNight` to the objective. So from now on the player will only be able to progress the objective when fishing at night.
The reason why the condition is now true if its night, that's because of the negation `!`.
And if you want to achieve time period between 23 and 2 you need to negate the condition.


Make these changes to your conditions reload and test!


## 5. Integrating conditions into conversations

We recently added a condition to a objective and now you will learn how to implement a condition into a conversation.
It's really good to know how to do that because if you give food to a player with a event, you want to prevent the player to receive it multiply times!
To solve this problem we need a condition tag that checks if a player already received the food.
In this step we will create an event to give the actual condition tag and the condition itself.

We will start with the condition tag:

``` YAML title="conditions.yml" hl_lines="3"
conditions:
  isNight: "time 6-18"
  foodReceivedTag: "tag foodReceivedTag" # (1)!
```

1. The `tag` condition is one of the most important conditions when creating a conversation, to prevent the player from cheating or to prevent having bugs.

So, what are we looking at here again?

* `foodReceivedTag`  is the name of the condition. You can name it whatever you want to. It is recommended to name
  it after what it should check.
*  The Condition Instruction.
- `tag` The first value in the instruction is always the **condition type**.
- `foodReceivedTag` This is the name of the tag that's given to the player


Now we are ready to create the event to give the tag to a player because we want to automatically assign a tag when receiving the food!
Open up the `events.yml` file and add the following event to the list:

``` YAML title="events.yml" hl_lines="3"
events:
  # Other events not shown
  foodReceivedTagAdd: "tag add foodReceivedTag"
```

!!! warning ""
    If you don't understand why we created this event here, you should go a step back to the events tutorial and read carefully again!

We've now done everything to make the next step: adding the condition/event to the conversation.
This part is extremely important so concentrate on that!

Open up your `jack.yml` file in the conversations folder and add the event to give the tag to a player and the
condition to not repeat the specified part of the conversation to a player.

``` YAML title="jack.yml" hl_lines="12-16 21-23"
conversations:
  Jack:
    quester: "Jack"
    first: "{==alreadyFoodReceived,==}firstGreeting" # (1)!

    NPC_options:
      # Other conversations not shown
      foodAnswer:
        text: "You're welcome! Take it... &7*gives food*"
        events: "{==foodReceivedTagAdd==},giveFoodToPlayer"
        pointer: "thankYou"
        conditions: "!foodReceivedTag"
      alreadyFoodReceived:
        text: "Oh! I think you already got food from me..."
        conditions: "foodReceivedTag"
        pointer: "saySorry"
      # Other conversations not shown

    player_options:
      # Other conversations not shown
      saySorry:
        text: "You are right. The food you gave me smells good."
        pointer: "townTour"
      # Other conversations not shown
```

1. If the player meets the condition give in that conversation, this conversation will be used instead of the other conversation because of the tag from the player.


As you can see we also added some new conversation to it, so now the NPC will say that you already received the food
and not getting any more!

Explanation: The `foodAnswer` conversation now has a event that gives the player a tag on receiving food and a conditions part
where you define that the player should not have the tag `foodReceivedTag` to prevent that the same conversation opens again.

We also need to set up another conversation with that exact condition and add it to the `first:` part.
This is necessary to start a conversation if the player already have the `foodReceiveTag` tag.


--8<-- "Tutorials/download-solution.md"
```
/bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Events/3-ConditionsInConversations /tutorialQuest overwrite
```

## Summary

You've learned some important facts about conditions and how to use them. You can now give a player a tag
to prevent him to get more food. More conditions can be found in the [conditions list](../../../Documentation/Conditions-List.md).
Next you will learn more about conditions and adding them to the whole tutorial quest.
---
[Extra Conditions Tutorial](./Extra_conditions.md){ .md-button .md-button--primary}

This is the end of the current basic tutorial, you can find more information in the [old tutorial](../Learn-BetonQuest.md).
