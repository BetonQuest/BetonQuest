---
icon: fontawesome/solid/play
tags:
- Objectives
---
In the last part of the basics tutorials, you've learned how to write your own events
and how to test them on the server directly without any conversation as well as built
in a conversation.
In this part you will learn how to create your very first objectives and how to test
them as well as triggering this objective with an event in a conversation.

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
add a new file to your "_tutorialQuest_" `Conversations` folder named "_blacksmith.yml_".
You may asking why we add a new file to conversations folder. This is because our created folder event ends
at the blacksmith. Well, we want to talk to him and get more instructions.

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

To define your first objective open the new created file `objectives.yml` and add the following text to it.

``` YAML title="objectives.yml" linenums="1"
objectives: # (1)!
  fishingObj: "fish cod 10 hookLocation:100;50;100;world range:20 events:caughtAllFish"
```

1. All objectives must be defined in an `objectives` section.

Let me explain this to you:

* `fishingObj`  is the name of the objective. You can choose any name you want. However, it is  recommended to name
  it after what it does. That just makes it easier to understand your quest.
*  The Objective Instruction.
  - `fish` The first value in the instruction is always the **objective type**.
  - `cod 10` This is an **option** of the objective `fish`. It defines which item you have to fish and which amount
    seperated by a space.

As we learned in the previous tutorial we have to define `cod` in the item section because BetonQuest don't know what `cod` is.

## 3. Creating the item in the items section

We already know that it's crucial to include an item to the `items` section for specific objective kinds, like `fish`.
To add the item to the list, let's reopen the "_package.yml_" file.

``` YAML title="package.yml" hl_lines="4-5" linenums="1"
npcs:
  '1': "Jack"

items:
  steak: "COOKED_BEEF"
  cod: "COD" # (1)!
```

1. Links the `cod` item name from your BetonQuest configs to the ingame `minecraft:COD` item.

Now, `cod` is a defined item that can be utilized throughout the entire quest.

## 4. Integrating objectives into conversations

Let's run the event from your conversation.

!!! question ""
    **Tip:** Highlighted lines in {==blue==} are new compared with the previous example.

``` YAML title="jack.yml" hl_lines="9-10 13-14" linenums="1" 
conversations:
  Jack:
    quester: "Jack"
    first: "firstGreeting"
    NPC_options:
      #... (1)
      
    player_options:
      #...
      
```

1. The tutorial will only show relevant parts of the examples from now on.
2. The event argument must contain one or multiple event names. These events are executed when the corresponding
   option is shown to the player.<br>This argument can be used on both player and npc options.
