---
icon: octicons/question-16
tags:
  - Conditions-Tutorial
---
Now that you know how to give the player tasks using objectives, it is time to learn about conditions. 
These are another essential building blog of quests as they allow you to create different outcomes based on the player's
actions. This works by attaching conditions to any conversation, event or objective.
For example, you could have a conversation option that is only available if the player has a certain item in their inventory.

In this tutorial you will learn how to create and use conditions!

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

Now that we've created it, open "_conditions.yml_" and add the following content:

``` YAML title="conditions.yml" linenums="1"
conditions: # (1)!
  isDay: "time 6-18"
```

1. All conditions must be defined in a section called `conditions`.

So, what are we looking at here?

* `isDay` is the name of a condition. You can name it whatever you want to. It is recommended to name
  it after what it should check.
*  The Condition Instruction:
    - `time`: The first value in the instruction is always the **condition type**.
    - `6-18`: This is an option of the `time` condition. It defines the timespan in which the condition is true. In this
      case it is true from 6am to 6pm.

Now we've created your first condition that checks if a specific game time is set on the server. Save the file and
continue with the next step!

## 3. Checking the condition in-game

You can check conditions if they are true or false in-game.

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.

Running a command is the simplest way to accomplish this:

Enter `/bq condition NAME tutorialQuest.isDay` on the server.
This command will show you the result "false" or "true" depending on what time it is.
If it is day it should show true and if it is night, false.

!!! note "Tip"
       Change the world time using the `/time set day` and `/time set night` commands.

| Command Part     | Meaning                                                                                                                  |
|------------------|--------------------------------------------------------------------------------------------------------------------------|
| `/bq conditions` | Tells BetonQuest that some conditions should be checked if true or false.                                                |
| `NAME`           | A player's name.                                                                                                         |
| `tutorialQuest`  | The name of a QuestPackage. This is required because you could have events with the same name in different packages.     |
| `isDay`          | The name of the condition to check. Don't forget to separate it with a dot from the package `tutorialQuest{==.==}isDay`. |


--8<-- "Tutorials/download-solution.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conditions/2-FirstCondition /tutorialQuest
    ```

## 4. Integrating conditions into objectives

Conditions can be added to objectives to limit the players ability to progress and complete the objective.
The objective will only progress when the condition is "true".
In this case we want to achieve that the player is only able to make progress when he's fishing at night.
Let's add the condition `isDay` to the objective:

``` YAML title="objectives.yml"
objectives:
  fishingObj: "fish COD 10 hookLocation:100;50;100;world range:20 events:caughtAllFish {==conditions:!isDay==}"
```
We can see multiple things here:

* `conditions:` - this option works on all objectives. Multiple conditions can be added if seperated by comma 
  (`conditions:con1,con2,con3`). 

* The exclamation mark (`!`) in front of a condition inverts it. That means that a condition that is "true" will be
  received as "false" and the other way around.
  - Remember that our condition is configured to check if it is daytime (`6-18`).
    We need to invert the `isDay` condition because we want to make progress when it is night.
    Now the condition will return "true" if it's nighttime (`23-2`).

Make these changes, reload and test!


## 5. Integrating conditions into conversations

Conditions can be used in conversations to control the available `player_options`.
This is a powerful feature for creating complex conversations with multiple answers.

For example, the current dialog with the NPC Jack can be repeated infinitely.
The player will always obtain more food. This is not what we want.    

To solve this problem we need to create an alternative path for the conversation. It must only be shown if the player
has obtained the food. 
To do so, we will create an event to give the player a "tag" and add a condition to the conversation.

We will start with a tag condition:

``` YAML title="conditions.yml" hl_lines="3"
conditions:
  isNight: "time 6-18"
  hasRecivedFood: "tag foodReceived" # (1)!
```

!!! question "What is a tag?"
    A tag is a label that can be added to a player. It is a simple way to store permanent information about the player.
    Later on, you will learn more about them in another tutorial.

This tag condition is "true" if the player has the defined tag. Let's break it down:

* `hasRecivedFood`: The name of the condition. You can name it whatever you want to. It is recommended to name
  it after what it should check.
*  The Condition Instruction:
    - `tag`: The first value in the instruction is always the **condition type**.
    - `foodReceivedTag`: This is the name of the tag that the player must have.


Tags can be assigned to a player using events. Let's create an event that gives the player the tag:

``` YAML title="events.yml" hl_lines="3"
events:
  # Other events not shown
  addFoodReceivedTag: "tag add foodReceived"
```

!!! warning ""
    If you don't understand why we created this event here, you should go a step back to the events tutorial and read carefully again!

We are now ready to for the next step: Adding the condition and event to the conversation.


Open up your `jack.yml` file in the conversations folder and add the event to give the tag to a player and the
condition to not repeat the specified part of the conversation.

``` YAML title="jack.yml" hl_lines="10 16-20 25-27"
conversations:
  Jack:
    quester: "Jack"
    first: "{==alreadyReceivedFood,==}firstGreeting" # (1)!

    NPC_options:
      firstGreeting:
        text: "Hello and welcome to my town traveler! Nice to see you. Where are you from?"
        pointer: "whereYouFrom"
        conditions: "!hasReceivedFood" # (2)!
      # Other NPC_options not shown
      foodAnswer:
        text: "You're welcome! Take it... &7*gives food*"
        events: "giveFoodToPlayer,{==addFoodReceivedTag==}" # (3)!
        pointer: "thankYou"
        conditions: "!foodReceivedTag"
      alreadyReceivedFood:
        text: "Hey %player%! I think I already gave you your welcome food..."
        conditions: "hasReceivedFood"  # (4)!
        pointer: "saySorry"
      # Other NPC_options not shown

    player_options:
      # Other player_options not shown
      saySorry:
        text: "You are right. Thanks again!"
        pointer: "townTour"
      # Other player_options not shown
```

1. This option defines all possible starting points for the conversation. If the player does not meet the condition
   `hasRecivedFood` they will start at the `firstGreeting` option. If they have the tag, they will start at the
   `alreadyReceivedFood` option.
2. This condition ensures that the player will only see the `firstGreeting` option if he doesn't have the tag `foodReceived`.
   Remember: `!` inverts the condition so that it is "true" if the player doesn't have the tag.
3. These events will be executed if the player chooses the `foodAnswer` option. It will give the player the food and the
   tag `foodReceived`.
4. This condition ensures that the player will only see the `alreadyFoodReceived` option if he has the tag `foodReceivedTag`.


As you can see we also added new options to it. Now the NPC will say that you already received the food
and won't give you more!

We have the same problem with the citytour for the player. He can do it again and again. Try to do it on your own and add
a condition to that specific conversation as well as to the first argument. If your solution is not working, download the
quest from the link below!

We also have another problem with the blacksmith conversation. We can start te objective over and over again, because
there is a condition left. We will create the condition in this next step to avoid the player to accept it again and again.

``` YAML title="blacksmith.yml" hl_lines="8-10"
conversations:
  Blacksmith:
    quester: "Blacksmith"
    first: "{==alreadyStarted,==}firstGreeting"
    NPC_options:
      firstGreeting:
        # Other player_options not shown
      alreadyStarted:
        text: "Come back to me if you caught all the fish!"
        conditions: "startedFishingTag"
    player_options:
      # Other player_options not shown
      accept:
        text: "Sure! I could use a new armour."
        event: "startFishingObj{==,startedFishingTagAdd==}"
        pointer: "goodLuck"
      # Other player_options not shown
```

And we also have to add it in the conditions.yml again.
In this step we can also add a condition to check if the player really have the needed amount of cod in his inventory.
We can do this with a `item` condition that checks the players inventory.

``` YAML title="conditions.yml" hl_lines="5-6"
conditions:
  isNight: "time 6-18"
  hasRecivedFood: "tag foodReceived"
  tourAlreadyDoneTag: "tag tourAlreadyDoneTag"
  startedFishingTag: "tag startedFishingTag"
  hasFishInInv: "item COD:10"
```

Now we created the `hasFishInInv` condition that checks if the players has 10 cod in his inventory. We now need to implement it
to our conversation in the blacksmith file like so:

``` YAML title="blacksmith.yml" hl_lines="8-24 27-35"
conversations:
  Blacksmith:
    quester: "Blacksmith"
    first: "{==caughtAllFish,==}alreadyStarted,firstGreeting"
    NPC_options:
      firstGreeting:
        # Other player_options not shown
      caughtAllFish:
        text: Oh let me see! Amazing.. Can I have them?
        pointer: agree
        conditions: hasFishInInv # (1)!
      giveFishToBlacksmith:
        text: Thank you very much and here is the promised armour!
        pointer: seeYouSoon
        conditions: hasFishInInv
      cheater:
        text: Heeeyy! Dont try to cheat boy!
        conditions: '!hasFishInInv'
      goodbye:
        text: It was nice to meet you! I hope we will see us soon again. Goodbye
    player_options:
      # Other player_options not shown
      deny:
        text: I dont have time right now.
        pointer: maybeLater
      agree:
        text: Of course! Take em.
        pointer: giveFishToBlacksmith,cheater
      seeYouSoon:
        text: That was a pleasure! See you soon...
        pointer: goodbye
```

Try to write your own events in the `giveFishToBlacksmith` conversation for exmample `rewardPlayer` or something.
Otherwise just download from the link below!

--8<-- "Tutorials/download-solution.md"
       ```
       /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conditions/3-ConditionsInConversations /tutorialQuest overwrite
       ```
## 6. Optional bonus section - Conditions in Events

In this section you will learn how to use conditions in events, because sometimes you want to be able to block a specific event from triggering
because some conditions for the players are not given.

!!! warning ""
    This is not part of the main quest and will not be implemented to it!

We will temporarily create a condition called `receiveNotify` in the conditions.yml like so:
This one will be a simple tag condition.

``` YAML title="conditions.yml" hl_lines="1-2"
conditions:
  receiveNotify: "tag receiveNotify"
```

We will now create an event to test our recently created condition.
For testing purpose we will use a notify event:

``` YAML title="events.yml" hl_lines="1-2"
events:
  notifyPlayer: "notify You completed the quest! Congratulations! io:Title sound:firework_rocket conditions:receiveNotify"
```

You can see that the notify event has that tag we've created before, so the player is only be able to receive that notify if he has the tag.
Save and reload this and go ingame to test if it works!

You can simply test it with the give BetonQuest command:
`/bq event PLAYERNAME tutorialQuest.notifyPlayer`
Nothing should happen because the player dont have the `receiveNotify` tag.

Type `/bq tag PLAYERNAME add tutorialQuest.receiveNotify` and try the command from above again.
The player should now get the notify on his screen!

You can also manually delete a tag with `/bq tag PLAYERNAME del tutorialQuest.receiveNotify`. This is very helpful because
if you have a lots of quests and tags you dont want to purge a player because he will be mad if he have to do the other quests again.


## Summary

You've learned some important facts about conditions and how to use them. You can now give a player a tag
to prevent him to get more food. More conditions can be found in the [conditions list](../../../Documentation/Conditions-List.md).
Next you will learn more about conditions and adding them to the whole tutorial quest.
---
[Extra Conditions Tutorial](./Extra_conditions.md){ .md-button .md-button--primary}

This is the end of the current basic tutorial, you can find more information in the [old tutorial](../Learn-BetonQuest.md).
