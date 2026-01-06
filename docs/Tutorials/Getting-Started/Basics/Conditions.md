---
icon: octicons/question-16
tags:
  - Conditions-Tutorial
---
Now that you know how to give the player tasks using objectives, it is time to learn about conditions. 
These are another essential building blog of quests as they allow you to create different outcomes based on the player's
actions. This works by attaching conditions to any conversation, action or objective.
For example, you could have a conversation option that is only available if the player has a certain item in their inventory.

In this tutorial, you will learn how to create and use conditions!

<div class="grid" markdown>
!!! danger "Requirements"
    * [Conversations Tutorial](Conversations.md)
    * [Actions Tutorial](Actions.md)
    * [Objectives Tutorial](Objectives.md)

!!! example "Related Docs"
    * [Conditions Reference](../../../Documentation/Scripting/About-Scripting.md#conditions)
    * [Conditions List](../../../Documentation/Scripting/Building-Blocks/Conditions-List.md)
</div>
@snippet:tutorials:download-setup-warning@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Conditions/1-DirectoryStructure /tutorialQuest
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"

## 1. Creating the folder structure for your first condition

Add a new file to your "_tutorialQuest_" `QuestPackage` named "_conditions.yml_".
The file structure should look like this now:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: actions.yml
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
       case, it is true from 6am to 6pm.

Now we've created your first condition that checks if a specific game time is set on the server. Save the file and
continue with the next step!

## 3. Checking the condition in-game

You can check conditions if they are true or false in-game.

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.

Running a command is the simplest way to accomplish this:

Enter `/bq condition NAME tutorialQuest>isDay` on the server.
This command will show you the result "false" or "true" depending on what time it is.
During day time, the result will show true. In the nighttime it will be false.

!!! note "Tip"
       Change the world time using the `/time set day` and `/time set night` commands.
       This will allow you to test your conditions in-game.

| Command Part     | Meaning                                                                                                                  |
|------------------|--------------------------------------------------------------------------------------------------------------------------|
| `/bq conditions` | Tells BetonQuest that some conditions should be checked if true or false.                                                |
| `NAME`           | A player's name.                                                                                                         |
| `tutorialQuest`  | The name of a QuestPackage. This is required because you could have actions with the same name in different packages.    |
| `isDay`          | The name of the condition to check. Don't forget to separate it with a dot from the package `tutorialQuest{==.==}isDay`. |


@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Conditions/2-FirstCondition /tutorialQuest overwrite
    ```

## 4. Integrating conditions into objectives

Conditions can be added to objectives to limit the player's ability to progress and complete the objective.
The objective will only progress when the condition is "true".
In this case, we want to achieve that the player is only able to make progress when he's fishing at night.
Let's add the condition `isDay` to the objective:

``` YAML title="objectives.yml"
objectives:
  fishingObj: "fish COD 3 hookLocation:100;63;100;world range:20 actions:caughtAllFish {==conditions:!isDay==}"
```
We can see multiple things here:

* `conditions:` - this option works on all objectives. Multiple conditions can be added if separated by comma 
  (`conditions:con1,con2,con3`). 

* The exclamation mark (`!`) in front of a condition inverts it. That means that a condition that is "true" will be
  received as "false" and the other way around. This must be set per condition if multiple conditions are in use 
  (`conditions:!con1,!con2,!con3`).
  - Remember that our condition is configured to check if it is daytime (`6-18`).
    We need to invert the `isDay` condition because we want to make progress when it is night.
    Now the condition will return "true" if it's nighttime (`23-2`).

Make these changes, reload and test!


## 5. Integrating conditions into conversations

Conditions can be used in conversations to control the available `player_options`.
This is a powerful feature for creating complex conversations with multiple answers.

For example, the current dialog with the NPC Jack can be repeated infinitely.
The player will always obtain more food. This is not what we want.    

### 5.1. Making Jack only give food once

To solve this problem we need to create an alternative path for the conversation. It must only be shown if the player
has obtained the food. 
To do so, we will create an action to give the player a "tag" and add a condition to the conversation.

We will start with a tag condition:

``` YAML title="conditions.yml" hl_lines="3"
conditions:
  isNight: "time 6-18"
  hasReceivedFood: "tag foodReceived"
```

!!! question "What is a tag?"
    A tag is a label that can be added to a player. It is a simple way to store permanent information about the player.
    Later on, you will learn more about them in another tutorial.

This tag condition is "true" if the player has the defined tag. Let's break it down:

* `hasRecivedFood`: The name of the condition. You can name it whatever you want to. It is recommended to name
  it after what it should check.
*  The Condition Instruction:
    - `tag`: The first value in the instruction is always the **condition type**.
    - `foodReceived`: This is the name of the tag that the player must have.


Tags can be assigned to a player using actions. Let's create an action that gives the player the tag:

``` YAML title="actions.yml" hl_lines="3"
actions:
  # Other actions not shown
  addFoodReceivedTag: "tag add foodReceived"
```

!!! warning "Tip"
    If you don't understand why we created the action in the `actions` section, you should go back to the actions tutorial
    and read carefully!

We are now ready for the next step: Adding the condition and action to the conversation.


Open up your `jack.yml` file in the conversations folder and add the action to give the tag to a player and the
condition to not repeat the specified part of the conversation.

``` YAML title="jack.yml" hl_lines="16-19 24-26"
conversations:
  Jack:
    quester: "Jack"
    first: "{==alreadyReceivedFood,==}firstGreeting" #(1)!

    NPC_options:
      firstGreeting: #(2)!
        text: "Hello and welcome to my town traveler! Nice to see you. Where are you from?"
        pointers: "whereYouFrom"
      # Other NPC_options not shown
      foodAnswer:
        text: "You're welcome! Take it... &7*gives food*"
        actions: "giveFoodToPlayer,{==addFoodReceivedTag==}" #(3)!
        pointers: "thankYou"
        conditions: "!hasReceivedFood"
      alreadyReceivedFood:
        text: "Hey %player%! I think I already gave you your welcome food..."
        conditions: "hasReceivedFood"  #(4)!
        pointers: "saySorry"
      # Other NPC_options not shown

    player_options:
      # Other player_options not shown
      saySorry:
        text: "You are right. Thanks again!"
        pointers: "townTour"
      # Other player_options not shown
```

1. This option checks all possible starting points for the conversation from left to right.
   The first option that the player matches the conditions for will be used. The conditions can be found down in the
   `NPC_options`. If the player matches none of the conditions, the conversation will not start.
   <br><br>
   In this case, if the player meets the condition `hasRecivedFood`, they will start at the `alreadyReceivedFood` option.
2. This option will be shown if no other option matches the conditions.
   This is because this option has no conditions and is the last option in the `first` list.
3. These actions will be executed if the player chooses the `foodAnswer` option. It will give the player the food and the
   tag `foodReceived`.
4. This condition ensures that the player will only see the `alreadyFoodReceived` option if he has the tag `foodReceived`.


As you can see, we also added new options to it. Now the NPC will say that you already received the food
and won't give you more!

!!! tip "Note about testing"
    If you want to test this conversation again, you need to remove the tag from the player.
    You can do this by using the `/bq tag remove <player> <tag>` command.

@snippet:tutorials:download-solution@
       ```
       /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Conditions/3-ConditionsInConversations /tutorialQuest overwrite
       ```

### 5.2. Limiting the town tour as well
The same problem exists with the town tour. The player can do it over and over again. Try to fix it on your own 
using the method you just learned.

??? tip "Solution"
    Get the correct configs by running the following command.<br>
    :warning: _This will overwrite any changes (including NPC ID's and locations) you have made to the example.<br>_
    Linking NPCs to conversations is explained in the [basics tutorial](./Conversations.md#1-linking-a-conversation-to-a-npc).
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Conditions/4-JackCompleted /tutorialQuest overwrite
    ```

### 5.3. Making the Blacksmith only trade the armor once

The blacksmith conversation suffers from a similar problem. There is no way to get the reward, the conversation will 
start over and over again. Let's fix that!

#### 5.3.1 Preparing the conversation

Let's add some dialog for when the player has accepted the quest but not completed it yet:
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
        conditions: "hasStartedFishing"
    player_options:
      # Other player_options not shown
      accept:
        text: "Sure! I could use a new armour."
        actions: "startFishingObj{==,addStartedFishingTag==}"
        pointers: "goodLuck"
      # Other player_options not shown
```

As usual, we need to add the condition to the _"conditions.yml"_.
We will also add a condition to check if the player has the required amount of cod in their inventory.
We can do so with an `item` condition.

``` YAML title="conditions.yml" hl_lines="5-6"
conditions:
  isNight: "time 6-18"
  hasRecivedFood: "tag foodReceived"
  hasDoneTour: "tag tourDone"
  hasStartedFishing: "tag startedFishing"
  hasFishInInv: "item cod:3"
  hasDoneQuest: "tag questDone"
```
Additionally, we must add the new actions as well. 
Those remove three cod from the player's inventory and add a tag for completing the quest.

```YAML title="actions.yml" hl_lines="3-5"
actions:
  # Other actions not shown
  addStartedFishingTag: "tag add startedFishing"
  addQuestDoneTag: "tag add questDone"
  takeFishFromPlayer: "take cod:3"  
```

Now let's use all these new elements to finish up the conversation.

Note that we check the `hasFishInInv` twice in the dialog. This prevents players from cheating by dropping the items once
the starting option is determined. If they do so, the conversation will simply end without giving out any items.

``` YAML title="blacksmith.yml" hl_lines="8-21 24-32"
conversations:
  Blacksmith:
    quester: "Blacksmith"
    first: "{==questDone,caughtAllFish,==}alreadyStarted,firstGreeting"
    NPC_options:
      firstGreeting:
        # Other player_options not shown
      caughtAllFish:
        text: "Oh let me see! Amazing.. Can I have them?"
        pointers: "agree"
        conditions: "hasFishInInv"
      giveFishToBlacksmith:
        text: "Thank you very much and here is the promised armour!"
        pointers: "seeYouSoon"
        actions: "takeFishFromPlayer,addQuestDoneTag"
        conditions: "hasFishInInv" #(1)!
      goodbye:
        text: "It was nice to meet you! I hope we will see us soon again. Goodbye"
      questDone: 
        text: "Nice to see you again %player%!" #(2)!
        conditions: "hasDoneQuest"
    player_options:
      # Other player_options not shown
      deny:
        text: "I dont have time right now."
        pointers: "maybeLater"
      agree:
        text: "Of course! Take em."
        pointers: "giveFishToBlacksmith"
      seeYouSoon:
        text: "That was a pleasure! See you soon..."
        pointers: "goodbye"
```

1. This is the cheat protection we talked about earlier. If the player drops the items, the conversation will end.
2. Some dialog for when the player has already completed the quest.

#### 5.3.2 Handing out the armor

Now add an action to reward the player on your own. Tip: You must use the
[`give`](../../../Documentation/Scripting/Building-Blocks/Actions-List.md#give-items-give) action to hand out items that are defined
in the `items` section of your "_package.yml_" file.

??? example "SPOILER: Solution"
    ```YAML title="package.yml" hl_lines="3-6"
    items:
      # Other items not shown
      ironHelmet: "simple IRON_HELMET"
      ironChestplate: "simple IRON_CHESTPLATE"
      ironLeggings: "simple IRON_LEGGINGS"
      ironBoots: "simple IRON_BOOTS"
    ```
    
    ```YAML title="actions.yml" hl_lines="3"
    actions:
      # Other actions not shown
      rewardPlayer: "give ironBoots,ironChestplate,ironLeggings,ironHelmet"
    ``` 
    
    ```YAML title="blacksmith.yml"
    giveFishToBlacksmith:
      text: "Thank you very much and here is the promised armour!"
      pointers: "seeYouSoon"
      actions: "takeFishFromPlayer,addQuestDoneTag{==,rewardPlayer==}"
      conditions: "hasFishInInv"
    ``` 
        

@snippet:tutorials:download-solution@
       ```
       /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Conditions/5-FullExample /tutorialQuest overwrite
       ```

## 6. Conditions in Actions

In this section, you will learn how to use conditions in actions. This is handy when you want to block an action
from triggering because some conditions for the players are not met.

We will temporarily create a tag condition called `receiveNotify` in the "_conditions.yml_" like so:

``` YAML title="conditions.yml" hl_lines="2"
conditions:
  receiveNotify: "tag receiveNotify"
```

We will now create an action to test our recently created condition.
For testing purposes, we will use a notify action:

``` YAML title="actions.yml" hl_lines="2"
actions:
  notifyPlayer: "notify You completed the quest! io:Title sound:firework_rocket conditions:receiveNotify"
```

Let's break it down:

  * `notifyPlayer`: The name of the action.
  * `notify`: The action type - notify actions are used to send notifications to the player.
  * `You completed the quest!`: The message of the notification.
  * `io:Title`: The message will be displayed as a title.
  * `sound:firework_rocket`: The message will be accompanied by a firework sound.
  * `conditions:receiveNotify`: The action will only trigger if the condition `receiveNotify` is met. This argument
     works for all actions.

You can see that the notify action uses a condition. This means the player is only able to receive the notification
if they have the tag. 
Save, reload and execute the command in the game to test how it works!

You can test it with this BetonQuest command:
```
/bq action PLAYERNAME tutorialQuest>notifyPlayer
```
Nothing should happen because the player doesn't have the `receiveNotify` tag.

Now run the following command and then try the command from above again.
```
/bq tag PLAYERNAME add tutorialQuest>receiveNotify
``` 
You should now see the notification on your screen!

You can also manually delete a tag using 
```
/bq tag PLAYERNAME del tutorialQuest>receiveNotify`.
```
This is very helpful when you are testing your quest and want to reset the player's progress.

If you like, you could add the action to your blacksmith conversation. Make sure to remove the condition from the action's
instruction. There is no use for it in this quest, but the notify action is a good example to show how to use conditions
in actions. 

## Summary

You've learned what conditions are and how to they are used in objectives, conversations and actions.
More conditions can be found in the [conditions list](../../../Documentation/Scripting/Building-Blocks/Conditions-List.md).
---
