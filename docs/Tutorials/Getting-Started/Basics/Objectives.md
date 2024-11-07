---
icon: octicons/codescan-checkmark-16
tags:
- Objectives
---
In the last tutorial you learned to create and use events. 
This tutorial is about objectives. Objectives are tasks which you can assign to a player. For example breaking blocks or
fishing fish. The possibilities are nearly endless! You will learn about these in this tutorial.

<div class="grid" markdown>
!!! danger "Requirements"
    * [Conversations Tutorial](Conversations.md)
    * [Events Tutorial](Events.md)

!!! example "Related Docs"
    * [Objectives Reference](../../../Documentation/Scripting/About-Scripting.md#objectives)
    * [Objectives List](../../../Documentation/Scripting/Building-Blocks/Objectives-List.md)
</div>
@snippet:tutorials:download-setup-warning@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Objectives/1-DirectoryStructure /tutorialQuest
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"

## 1. Creating the folder structure for your first objective
Add a new file to your "_tutorialQuest_" `QuestPackage` named "_objectives.yml_" and 
a new file to your "_tutorialQuest_" Conversations folder named "_blacksmith.yml_".
You may ask why we add a new file to the conversations folder. This is because the city tour currently ends in nothingness. 
We're going to add a blacksmith NPC that the player can talk to.

Here is an overview of what your directory structure should look like now:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: events.yml
    - :material-file: {==objectives.yml==}
    - :material-folder-open: conversations
        - :material-file: jack.yml
        - :material-file: {==blacksmith.yml==}

We now have our file structure ready and can start writing objectives and a new conversation!

## 2. Defining your first objective and finishing event

Open the newly created file "_objectives.yml_" and add the following:

``` YAML title="objectives.yml" linenums="1"
objectives: # (1)!
  fishingObj: "fish cod 3 notify hookLocation:100;63;100;world range:20 events:caughtAllFish"
```

1. All objectives must be defined in an `objectives` section.

Let's explain:

* `fishingObj`  is the name of the objective. You can choose any name you want. However, it is  recommended to name
  it after what it does. That just makes it easier to understand your quest.
  * The Objective Instruction.
    - `fish`: The first value in the instruction is always the **objective type**.
    - `cod`: This is an **option** of the objective `fish`. It defines which item you have to fish.
    - `3`: This is another **option**. It defines the amount to fish.
    - `notify`: This is a general argument for most objectives. It enables a notification when the player progresses the objective.
    - `hookLocation:100;63;100;world`: This **option** defines where the hook of the fishing rod must be located. Only fish that are 
       fished in this specific area are counted by the objective. You must adjust this to your world!
    - `range:20`: If you use the hook location you also have to define the range **option**. This is the range around the hook location coordinate
       where fished things are still counted.
    - `events:caughtAllFish`: This is not an option of the fish objective but a general objective argument. The defined event(s)
       get triggered once the objective is completed (after you caught 3 cod at the specified hook location).

After that we add the `caughtAllFish` event to the "_events.yml_" like this:

``` YAML title="events.yml" hl_lines="4" linenums="1"
events:
  # Other events not shown here
  tpBlacksmith: "teleport 50;70;50;world"
  caughtAllFish: "notify You caught enough fish!\nReturn to the blacksmith! io:Title sound:firework_rocket"
```
It lets the player know that they successfully completed the objective.


## 3. Creating the item in the items section

As we learned in the [previous tutorial](Events.md#3-creating-the-item-in-the-items-section) we have to define `cod` in
the item section because BetonQuest doesn't know what `cod` is.
To add the item to the list, let's reopen the "_package.yml_" file.

``` YAML title="package.yml" hl_lines="6" linenums="1"
npcs:
  '1': "Jack"

items:
  steak: "COOKED_BEEF"
  cod: "COD" # (1)!
```

1. Links the `cod` item name from your BetonQuest configs to the ingame `minecraft:COD` item.

Now, `cod` is a defined item that can be utilized throughout the entire quest.

## 4. Testing your first objective ingame

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.


Objectives must be started before they start watching the player's actions.
The easiest way to do this is by running a command:

Enter `/bq objective YOUR_NAME add tutorialQuest.fishObj` on the server.
This command will start the objective for the player.
If you want to check if you have done it correctly, go to the defined location and fish 3 cod. After you caught 3 cod
you should get a notification.

!!! tip "Faster Fishing"
    Use this command to get a superfast fishing rod:
    ``` YAML title="1.20.5+"
    /give @p fishing_rod[custom_name='["",{"text":"Instant Fishing Rod","color":"yellow"}]',lore=['["",{"text":"Instantly summons a hungry fish...","italic":false}]'],item_name=derp,enchantments={levels:{lure:100}}]
    ```
    ``` YAML title="pre 1.20.5"
    /give @p fishing_rod{display:{Name:'{"text":"Instant Fishing Rod","color":"yellow","italic":true}',Lore:['{"text":"Instantly summons a hungry fish..."}']},HideFlags:1,Enchantments:[{id:"minecraft:lure",lvl:100s}]} 1
    ```

| Command Part                  | Meaning                                                                                                                                                                       |
|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/bq objective`               | Tells BetonQuest that some event should be executed.                                                                                                                          |
| `NAME`                        | A player's name.                                                                                                                                                              |
| `add`/`complete`/`del`/`list` | Use these arguments to rather add, complete or delete an objective. The list argument does not require any further arguments and lists all objectives of the selected player. |
| `tutorialQuest`               | The name of a QuestPackage. This is required because you could have objectives with the same name in different packages.                                                      |
| `fishObj`                     | The name of the objective to execute. Don't forget to separate it with a dot from the package `tutorialQuest{==.==}fishObj`.                                                  |

You can also run the `/bq objective NAME` to list all active objectives of a player.
 `/bq objective NAME` to list all active objectives of a player.

To manually complete the objective for a player you need to type
`/bq objective YOUR_NAME complete tutorialQuest.fishObj`. After you send this command you should also get
a notification about the completion of this objective.

## 5. Using events to start objectives

Objectives cannot only be started and stopped using commands, but also with events.
Let's add an event to start the fishing objective:

``` YAML title="events.yml" hl_lines="5" linenums="1"
events:
  # Other events not shown here
  tpBlacksmith: "teleport 50;70;50;world"
  caughtAllFish: "notify You caught enough fish!\nReturn to the blacksmith! io:Title sound:firework_rocket"
  startFishingObj: "objective start fishingObj" # (1)!
```

1. Starts the objective `fishingObj` for the player that this event is executed on.

## 6. Integrating objectives into conversations

As you know, we can run events from conversations. We can now use the new event to start an objective from a conversation.

Let's add some dialog to the newly created file named "_blacksmith.yml_" in the conversation folder:

``` YAML title="blacksmith.yml" linenums="1" 
conversations:
  Blacksmith:
    quester: Blacksmith
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Welcome %player% in Valencia! The mayor already told me that you are new to our town.
        pointer: thatsRight
      newArmorForNewCitizens:
        text: So every new citizens in our town will get a new armour from me but you have to do something for me in order to get this really nice upgrade!
        pointer: whatToDo
      collectFish:
        text: You will have to fish 3 fresh cod for me and bring them to me. After that I will give you the nice new armour! Is that a deal?
        pointer: accept,deny # (1)!
      maybeLater:
        text: No problem! You can comeback later aswell. Bye!
      goodLuck:
        text: Good luck and I will see you later!
    player_options:
      thatsRight:
        text: Yeah thats true. Thank you!
        pointer: newArmorForNewCitizens
      whatToDo:
        text: What can I do for you?
        pointer: collectFish
      accept:
        text: Sure! I could use a new armour.
        event: startFishingObj # (2)!
        pointer: goodLuck
      deny:
        text: I dont have time right now.
        pointer: maybeLater
```

1. The player have the choice to say yes or no.
2. This is the event to start your actual objective task to fish 3 fresh cod.

Now link the conversation to a new NPC that is placed wherever the city tour ends. You should already know how to link
the dialog to the npc in "_package.yml_". If not, [check the previous tutorials](Conversations.md#1-linking-a-conversation-to-a-npc)!

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.


@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Objectives/2-FullExample /tutorialQuest
    ```

## Summary

You've learned what objectives are and how to create them. You can now give a player an 
objective to have a more advanced quest! More objectives can be found in the [objectives list](../../../Documentation/Scripting/Building-Blocks/Objectives-List.md).
In the next tutorial you will learn how **conditions** work and how to use them to make the Blacksmith react to the completed objective.
---
[ Conditions Tutorial ](Conditions.md){ .md-button .md-button--primary}


