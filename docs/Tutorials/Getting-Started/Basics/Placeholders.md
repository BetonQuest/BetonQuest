---
icon: material/variable-box
tags:
  - Placeholder-Tutorials
---
Now that you know conversations, actions, objectives and conditions, it is time to learn about placeholders.
Placeholders are small pieces of text that BetonQuest replaces with live values. You can use them to show a player's
name, the amount of missing quest items, the result of a condition or a value you defined yourself.

In this tutorial, you will add placeholders to the quest from the previous tutorials.

<div class="grid" markdown>
!!! danger "Requirements"
    * [Conversations Tutorial](Conversations.md)
    * [Actions Tutorial](Actions.md)
    * [Objectives Tutorial](Objectives.md)
    * [Conditions Tutorial](Conditions.md)

!!! example "Related Docs"
    * [Placeholders List](../../../Documentation/Reference/Placeholders-List.md)
    * [Variable Condition](../../../Documentation/Reference/Conditions-List.md#variable)
</div>
@snippet:tutorials:download-setup-warning@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Placeholders/1-Setup /tutorialQuest
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"

## 1. Understanding placeholder syntax

A placeholder starts and ends with a percent sign (`%`). The text between these signs tells BetonQuest which value
should be inserted.

``` YAML title="Example"
text: "Hello %player%!"
```

When the player named Steve sees this text, BetonQuest replaces `%player%` with `Steve`.

Most placeholders use this structure:

``` txt
%placeholder.argument.property%
```

For example, `%item.cod.amount%` means:

* `item` - Use the item placeholder.
* `cod` - Look at the item named `cod` in this QuestPackage.
* `amount` - Show how many of this item the player has.

You do not need to remember every placeholder. The [placeholders list](../../../Documentation/Reference/Placeholders-List.md)
contains all available placeholders.

## 2. Using simple placeholders in conversations

You already used `%player%` in the previous tutorials. Let's add a few more placeholders to the blacksmith conversation.

Open the "_blacksmith.yml_" file in the "_conversations_" folder and change the greeting and good luck texts:

``` YAML title="blacksmith.yml" hl_lines="7 19" linenums="1"
conversations:
  Blacksmith:
    quester: "Blacksmith"
    first: "questDone,caughtAllFish,alreadyStarted,firstGreeting"
    NPC_options:
      firstGreeting:
        text: "Welcome %player% to Valencia! I am %quester%, and the mayor already told me that you are new to our town."
        pointers: "thatsRight"
      newArmorForNewCitizens:
        text: "So every new citizen in our town will get new armour from me, but you have to do something for me in order to get this really nice upgrade!"
        pointers: "whatToDo"
      collectFish:
        text: "You will have to fish 3 fresh cod for me and bring them to me. After that I will give you the nice new armour! Is that a deal?"
        pointers: "accept,deny"
      maybeLater:
        text: "No problem! You can comeback later as well. Bye!"
      goodLuck:
        text: "Good luck, %player%! I will see you later!"
```

Now also change the lines where the blacksmith talks about the active fishing quest:

``` YAML title="blacksmith.yml" hl_lines="3 6" linenums="1"
NPC_options:
  alreadyStarted:
    text: "Come back to me if you caught all the fish! You still need %objective.fishingObj.left% cod."
    conditions: "hasStartedFishing"
  caughtAllFish:
    text: "Oh let me see! You currently carry %item.cod.amount% cod. Can I have them?"
    pointers: "agree"
    conditions: "hasFishInInv"
```

Let's look at the new placeholders:

* `%player%` is replaced with the player's name.
* `%quester%` is replaced with the current conversation's quester name. In this example it is `Blacksmith`.
* `%objective.fishingObj.left%` shows how many cod the player still needs for the active `fishingObj` objective.
* `%item.cod.amount%` shows how many `cod` items the player has in their inventory and backpack.

!!! warning
    `%objective.fishingObj.left%` only works while the player has the `fishingObj` objective active.
    If the player does not have this objective, the placeholder will be empty.

## 3. Defining reusable text with constants

Sometimes you use the same value in multiple places. For example, the town name or the amount of fish needed for the
quest. You can define these values in the `constants` section and then use them as placeholders.

Open "_package.yml_" and add this section:

``` YAML title="package.yml" hl_lines="16-18" linenums="1"
npcs:
  JackNpc: "citizens 1"
  BlacksmithNpc: "citizens 2"
npc_conversations:
  JackNpc: "Jack"
  BlacksmithNpc: "Blacksmith"

items:
  steak: "simple COOKED_BEEF"
  cod: "simple COD"
  ironHelmet: "simple IRON_HELMET"
  ironChestplate: "simple IRON_CHESTPLATE"
  ironLeggings: "simple IRON_LEGGINGS"
  ironBoots: "simple IRON_BOOTS"

constants:
  townName: "Valencia"
  requiredFish: "3"
```

Now update the blacksmith conversation to use these constants:

``` YAML title="blacksmith.yml" hl_lines="7 13" linenums="1"
conversations:
  Blacksmith:
    quester: "Blacksmith"
    first: "questDone,caughtAllFish,alreadyStarted,firstGreeting"
    NPC_options:
      firstGreeting:
        text: "Welcome %player% to %constant.townName%! I am %quester%, and the mayor already told me that you are new to our town."
        pointers: "thatsRight"
      newArmorForNewCitizens:
        text: "So every new citizen in our town will get new armour from me, but you have to do something for me in order to get this really nice upgrade!"
        pointers: "whatToDo"
      collectFish:
        text: "You will have to fish %constant.requiredFish% fresh cod for me and bring them to me. After that I will give you the nice new armour! Is that a deal?"
        pointers: "accept,deny"
```

The `constant` placeholder is useful when the same text or number appears in multiple places. In this example, the
town name can now be changed in the `constants` section without editing every conversation line. The fish amount is
also used for the objective, but the item condition and the take action still use `cod:3` because those instructions
expect an item amount.

## 4. Using placeholders in actions and objectives

Placeholders are not limited to conversations. They can also be used in actions, and some objective options support them.
Not every part of every instruction should use placeholders, so always check the related reference page when you use
them in actions, objectives or conditions.

Open "_objectives.yml_" and change the amount of fish to the `requiredFish` constant:

``` YAML title="objectives.yml" hl_lines="2" linenums="1"
objectives:
  fishingObj: "fish cod %constant.requiredFish% notify hookLocation:100;63;100;world range:20 actions:caughtAllFish conditions:!isDay"
```

When this objective starts, BetonQuest replaces `%constant.requiredFish%` with `3`. The active objective will keep that
number, even if the constant is changed later.

Now open "_actions.yml_" and add a notification action:

``` YAML title="actions.yml" hl_lines="16" linenums="1"
actions:
  giveFoodToPlayer: "give steak:16"
  townTour: "folder tpLocation1,tpLocation2,tpLocation3,tpBlacksmith delay:2 period:5"
  tpLocation1: "teleport 100;70;100;world"
  tpLocation2: "teleport 200;73;200;world"
  tpLocation3: "teleport 300;71;300;world"
  tpBlacksmith: "teleport 50;70;50;world"
  startFishingObj: "objective start fishingObj"
  caughtAllFish: "notify You caught enough fish!\nReturn to the blacksmith in %constant.townName%! io:Title sound:firework_rocket"
  addFoodReceivedTag: "tag add foodReceived"
  addTourDoneTag: "tag add tourDone"
  rewardPlayer: "give ironBoots,ironChestplate,ironLeggings,ironHelmet"
  takeFishFromPlayer: "take cod:3"
  addStartedFishingTag: "tag add startedFishing"
  addQuestDoneTag: "tag add questDone"
  showQuestStatus: "notify %player%, you still need %objective.fishingObj.left% cod. You currently carry %item.cod.amount% cod. Enough fish is %condition.hasFishInInv% io:chat"
```

This action sends a chat message with the player's name, the missing cod, the amount of cod already carried and the
result of the `hasFishInInv` condition. The condition placeholder returns `true` or `false`.

Finally, run this action when the player accepts the blacksmith's quest:

``` YAML title="blacksmith.yml" hl_lines="4" linenums="1"
player_options:
  accept:
    text: "Sure! I could use a new armour."
    actions: "startFishingObj,addStartedFishingTag,showQuestStatus"
    pointers: "goodLuck"
```

## 5. Using placeholders in conditions

You can also use placeholders inside conditions. For this, BetonQuest provides the `variable` condition. It resolves a
placeholder and checks if the result matches a pattern.

Open "_conditions.yml_" and add this condition:

``` YAML title="conditions.yml" hl_lines="8" linenums="1"
conditions:
  isDay: "time 6-18"
  hasReceivedFood: "tag foodReceived"
  hasDoneTour: "tag tourDone"
  hasStartedFishing: "tag startedFishing"
  hasFishInInv: "item cod:3"
  hasDoneQuest: "tag questDone"
  playerNameStartsWithA: "variable %player% A.*"
```

Let's break down the new condition:

* `variable` is the condition type.
* `%player%` is resolved before the condition checks the result.
* `A.*` is a simple pattern. It matches names that start with `A`.

Now use this condition in the blacksmith conversation:

``` YAML title="blacksmith.yml" hl_lines="4 6-9" linenums="1"
conversations:
  Blacksmith:
    quester: "Blacksmith"
    first: "questDone,caughtAllFish,alreadyStarted,nameStartsWithA,firstGreeting"
    NPC_options:
      nameStartsWithA:
        text: "Your name starts with A, %player%. That is easy to remember!"
        pointers: "thatsRight"
        conditions: "playerNameStartsWithA"
      firstGreeting:
        text: "Welcome %player% to %constant.townName%! I am %quester%, and the mayor already told me that you are new to our town."
        pointers: "thatsRight"
```

The conversation checks the options in `first` from left to right. If the player's name starts with `A`, the
`nameStartsWithA` option is used. Otherwise, the normal `firstGreeting` option is used.

## 6. Testing placeholders in-game

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.

Talk to the blacksmith and accept the fishing quest. You should see a message that contains your name and the amount
of cod you still need.

You can also run the status action manually after accepting the fishing quest:

```
/bq action YOUR_NAME tutorialQuest>showQuestStatus
```

| Command Part       | Meaning                                                                                                                                    |
|--------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `/bq action`       | Tells BetonQuest that an action should be executed.                                                                                        |
| `YOUR_NAME`        | Your player name.                                                                                                                          |
| `tutorialQuest`    | The name of the QuestPackage.                                                                                                              |
| `showQuestStatus`  | The name of the action to execute. Don't forget to separate it with the `>` symbol from the package `tutorialQuest{==>==}showQuestStatus`. |

!!! tip "Testing the cod amount"
    Use `/give YOUR_NAME cod 3` or the creative inventory to give yourself cod.
    Then talk to the blacksmith again and check how `%item.cod.amount%` changes.

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Placeholders/2-FullExample /tutorialQuest overwrite
    ```

## Summary

You've learned what placeholders are and how to use them in conversations, actions and objective instructions.
You used player, quester, objective, item, condition and constant placeholders. You also used a placeholder inside a
condition with the `variable` condition type.
More placeholders can be found in the [placeholders list](../../../Documentation/Reference/Placeholders-List.md).
---
<div markdown style="text-align: left;">
[:octicons-arrow-left-16: Conditions](./Conditions.md){ .md-button .md-button--primary}
</div>
