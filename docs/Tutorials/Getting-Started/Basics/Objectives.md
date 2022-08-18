---
icon: material/check-circle
tags:
- Objectives
---
You have learned how to create your own events, test them on the server directly without a conversation, 
and integrate an event into your own conversations.
This section is about objectives. Objectives are tasks which you can assign to a player or a group for example 
a collect or fishing task. The possibilities are nearly endless! You will learn about that in this section.

<div class="grid" markdown>
!!! danger "Requirements"
    * [Conversations Tutorial](Conversations.md)
    * [Events Tutorial](Events.md)

!!! example "Related Docs"
    * [Objectives Reference](../../../Documentation/Reference.md#objectives)
    * [Objectives List](../../../Documentation/Objectives-List.md)
</div>
--8<-- "Tutorials/download-setup-warning.md"
    ```
    Need to be set!!!
    ```
    You can now find all files needed for this tutorial in this location:
    "NEED TO BE SET!!! _YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"

## 1. Creating the folder structure for your first objective
Add a new file to your "_tutorialQuest_" `QuestPackage` named "_objectives.yml_" and after that
add a new file to your "_tutorialQuest_" Conversations folder named "_blacksmith.yml_".
You may asking why we add a new file to conversations folder. This is because our created folder event ends
after the city tour. We now want to talk to the blacksmith and get more instructions.

Here is an overview of what your directory structure should look like now:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: events.yml
    - :material-file: {==objectives.yml==}
    - :material-folder-open: conversations
        - :material-file: jack.yml
        - :material-file: {==blacksmith.yml==}

We now have our file structure ready and can start writing objectives and a new conversation!

## 2. Defining your first objective

To define your first objective open the new created file "_objectives.yml_" and add the following text to it.

``` YAML title="objectives.yml" linenums="1"
objectives: # (1)!
  fishingObj: "fish cod 10 notify hookLocation:100;50;100;world range:20 events:caughtAllFish"
```

1. All objectives must be defined in an `objectives` section.

Let me explain this to you:

* `fishingObj`  is the name of the objective. You can choose any name you want. However, it is  recommended to name
  it after what it does. That just makes it easier to understand your quest.
  * The Objective Instruction.
    - `fish` The first value in the instruction is always the **objective type**.
    - `cod 10` This is an **option** of the objective `fish`. It defines which item you have to fish and which amount
      seperated by a space.
    - `hookLocation:100;50;100;world` This **option** is to define a hook location. Only catched fish in this specific area counts.
    - `range:20` If you use the hook location you also have to define the range. This is the range around the hook location coordinate.
    - `events:caughtAllFish` This event gets triggered after you caught 10 fish at the specific hook location.

As we learned in the previous tutorial we have to define `cod` in the item section because BetonQuest don't know what `cod` is.

## 3. Testing your first objective ingame

The easiest way to do this is by running a command:

Enter `/bq objective NAME tutorialQuest.fishObj` on the server.
This command will add's the objective to the player to fish 10 cod.
If you want to check if you have done it correctly, go to the defined location and fish 10 cod. After you caught 10 cod
you should get a notification.

| Command Part    | Meaning                                                                                                                      |
|-----------------|------------------------------------------------------------------------------------------------------------------------------|
| `/bq objective` | Tells BetonQuest that some event should be executed.                                                                         |
| `NAME`          | A player's name.                                                                                                             |
| `tutorialQuest` | The name of a QuestPackage. This is required because you could have objectives with the same name in different packages.     |
| `fishObj`       | The name of the objective to execute. Don't forget to separate it with a dot from the package `tutorialQuest{==.==}fishObj`. |

You can also run the `/bq objective NAME` to check what objectives a player has.

## 4. Creating the item in the items section

We already know that it's crucial to include an item to the `items` section for specific objective kinds, like `fish`.
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

## 5. Integrating objectives into conversations

Let's run the event from your conversation. In this case we will add some more smalltalk conversation.
In our example we arrived at the end of the city tour so we need a new npc for that.
Let's work with the new created file named "_blacksmith.yml_" in the conversation folder.
You should now already know how to create the npc in
"_package.yml_". If not, repeat the further tutorials!


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
        text: You will have to fish 10 fresh cod for me and bring them to me. After that I will give you the nice new armour! Is that a deal?
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
2. This is the event to start your actual objective task to fish 10 fresh cod.

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.

We've added the conversation to the new created file and also added a `startFishingObj` event to it.
This is necessary because you cannot write the objective name into the conversation. It is important to start
or maybe stop an objective with an event.
No worries! Open your "_events.yml_" and let me show you how simple this is:

``` YAML title="events.yml" hl_lines="8" linenums="1"
events:
  giveFoodToPlayer: "give steak:16"
  townTour: "folder tpLocation1,tpLocation2,tpLocation3,tpBlacksmith delay:2 period:5"
  tpLocation1: "teleport 100;70;100;world"
  tpLocation2: "teleport 200;73;200;world"
  tpLocation3: "teleport 300;71;300;world"
  tpBlacksmith: "teleport 50;70;50;world"
  startFishingObj: "objective start fishingObj" # (1)!
```

1. This is the event to start the objective for the interacting player.

Now that we have finished this, you can type `/q reload` ingame and talk to the blacksmith npc!

!!! danger ""
    If something not working. Try to find out what you have done wrong on your own!
    If you still dont know where the problem is you can simply download our solution.

--8<-- "Tutorials/download-solution.md"
    ```
    link goes skrr
    ```

## Summary

You've learned what objectives are and how to create them. You can now give a player an 
objective to have a more advanced quest! More objectives can be found in the [objectives list](../../../Documentation/Objectives-List.md).
In the next turotial you will learn how **conditions** works and how to use them.
---
[:construction: :construction_worker: ~~Conditions Tutorial~~ ](#summary){ .md-button .md-button--primary}

This is the end of the current basic tutorial, you can find more information in the [old tutorial](../Learn-BetonQuest.md).

