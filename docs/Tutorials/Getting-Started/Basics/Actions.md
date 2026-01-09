---
icon: fontawesome/solid/play
tags:
  - Action-Tutorials
---
After you learned how to create conversations we will now take a look at actions. These allow you to change the game 
world. This can be anything from modifying a player's inventory to placing blocks.  
In this tutorial, we will give the player items and teleport them to different locations using actions.

<div class="grid" markdown>
!!! danger "Requirements"
    * [Conversations Tutorial](Conversations.md)

!!! example "Related Docs"
    * [Actions Reference](../../../Documentation/Scripting/About-Scripting.md#actions)
    * [Actions List](../../../Documentation/Scripting/Building-Blocks/Actions-List.md)
</div>
@snippet:tutorials:download-setup-warning@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Conversations/1-DirectoryStructure /tutorialQuest
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"

## 1. Creating the folder structure for your first action

Add a new file to your "_tutorialQuest_" `QuestPackage` named "_actions.yml_".
Here is an overview of what your directory structure should look like now:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: {==actions.yml==}
    - :material-folder-open: conversations
        - :material-file: jack.yml

We now have our file structure ready and can start writing actions!

## 2. Defining your first action

Open the `actions.yml` now that we have created it and add the following content: 

``` YAML title="actions.yml" linenums="1"
actions: # (1)!
  giveFoodToPlayer: "give steak:16"
```

1. All actions must be defined in an `actions` section.

So what do we see here?

* `giveFoodToPlayer`  is the name of the action. You are free to choose any name. However, it is  recommended to name 
   it after what it does. That just makes it easier to understand your quest.
*  The Action Instruction. 
    - `give` The first value in the instruction is always the **action type**.
    - `steak:16` This is an **option** of the give action. It defines which item you want to give and which amount 
      separated by a colon.

Before we can test if the action works ingame we have to create the item `steak` because BetonQuest doesn't know what a `steak` is.

## 3. Creating the item in the items section

For some action types like `give` you need to specify an item in the `items` section.
It holds definitions of all items you want to create/use in your quest.
We will create the item section in the "_package.yml_" file. 

``` YAML title="package.yml" hl_lines="4-5" linenums="1"
npcs:
  JackNpc: "citizens 1"
npc_conversations:
  JackNpc: "Jack"

items:
  steak: "simple COOKED_BEEF" # (1)!
```

1. Links the `steak` item name from your BetonQuest configs to the ingame `minecraft:COOKED_BEEF` item.

Now `steak` is an item name that can be used throughout your quest.

## 4. Integrating actions into conversations

Let's run the action from your conversation.

!!! question ""
    **Tip:** Highlighted lines in {==blue==} are new compared with the previous example. 

``` YAML title="jack.yml" hl_lines="9-10 13-14" linenums="1" 
conversations:
  Jack:
    quester: "Jack"
    first: "firstGreeting"
    NPC_options:
      #... (1)
      foodAnswer:
        text: "Your welcome! Take it... &7*gives food*"
        actions: "giveFoodToPlayer" # (2)!
        pointers: "thankYou"
    player_options:
      #...
      thankYou: 
        text: "Oh that smells really good!"
```

1. The tutorial will only show relevant parts of the examples from now on. 
2. The action argument must contain one or multiple action names. These actions are executed when the corresponding
   option is shown to the player.<br>This argument can be used on both player and npc options.


Make these changes to your conversation, reload and test! The NPC should now give the player food.

## 5. Testing your first action ingame

You can also run actions using commands.

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.     
 
The easiest way to do this is by running a command:

Enter `/bq action NAME tutorialQuest>giveFoodToPlayer` on the server.
This command will give you the specified amount of steak if you've done everything right!

| Command Part       | Meaning                                                                                                                                     |
|--------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `/bq action`       | Tells BetonQuest that some action should be executed.                                                                                       |
| `NAME`             | A player's name.                                                                                                                            |
| `tutorialQuest`    | The name of a QuestPackage. This is required because you could have actions with the same name in different packages.                       |
| `giveFoodToPlayer` | The name of the action to execute. Don't forget to separate it with the `>` symbol from the package `tutorialQuest{==>==}giveFoodToPlayer`. |

You can also run this command from the console (without the slash at the start). 

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Actions/1-FirstAction /tutorialQuest overwrite
    ```


## 6. Creating folder actions

Now we will create a tour through the mayors city. Meanwhile, we will learn about the teleport and folder actions.

Open the "_actions.yml_" file and add these lines:                                                                                      
``` YAML title="actions.yml" hl_lines="3-7" linenums="1"
actions:
  giveFoodToPlayer: "give steak:16"
  townTour: "folder tpLocation1,tpLocation2,tpLocation3,tpBlacksmith delay:2 period:5"
  tpLocation1: "teleport 100;70;100;world" # (1)!
  tpLocation2: "teleport 200;73;200;world" # (2)!
  tpLocation3: "teleport 300;71;300;world" # (3)!
  tpBlacksmith: "teleport 50;70;50;world" # (4)!
```

1. Adjust the coordinates and world name to your world. It must be in the [unified location format](../../../Documentation/Scripting/Data-Formats.md#unified-location-formating)
2. Adjust the coordinates and world name to your world. It must be in the [unified location format](../../../Documentation/Scripting/Data-Formats.md#unified-location-formating)
3. Adjust the coordinates and world name to your world. It must be in the [unified location format](../../../Documentation/Scripting/Data-Formats.md#unified-location-formating)
4. Adjust the coordinates and world name to your world. It must be in the [unified location format](../../../Documentation/Scripting/Data-Formats.md#unified-location-formating)

As you can see, there are a few new actions of the types `folder` and `teleport`.
The folder action wraps multiple actions inside itself. Once triggered, it simply executes its actions. 
Every action type is documented in the actions list, read more about the [folder](../../../Documentation/Scripting/Building-Blocks/Actions-List.md#run-multiple-actions-folder)
and [teleport](../../../Documentation/Scripting/Building-Blocks/Actions-List.md#teleport-teleport) actions there. 

Running the `townTour` action will teleport you to a new location every five seconds
until we get to our final destination, the blacksmith. The folder action is done after the `tpBlacksmith` action was run.

!!! danger
    Make sure you are in creative mode when testing this action. Otherwise, you might die from fall- or suffocation damage.
    Running `/gamemode creative` will change your game mode to creative.

Now we will add the folder action to Jack's conversation.
``` YAML title="jack.yml" hl_lines="11-18 23-29" linenums="1" 
conversations:
  Jack:
    quester: "Jack"
    first: "firstGreeting"
    NPC_options:
      #...
      foodAnswer:
        text: "You're welcome! Take it... &7*gives food*"
        actions: "giveFoodToPlayer"
        pointers: "thankYou"
      townTour:
        text: "Yeah that's true. You know what also smells good? The fresh air in my town! Would you like to take a little tour?"
        pointers: "enoughTime,noTimeForThat" # (1)!
      startTheTour:
        text: "Great! It is a honor for me to guide you through the town."
        actions: "townTour" # (2)!
      noProblem:
        text: "That's fine! Maybe you have time another day... Just talk to me again. See you!"
    player_options:
      #...
      thankYou:
        text: "Oh that smells really good!"
        pointers: "townTour" # (3)!
      enoughTime:
        text: "Yes of course! Show me everything."
        pointers: "startTheTour"
      noTimeForThat:
        text: "Sorry but I don't have time now..."
        pointers: "noProblem"
```

1. The player once again has a choice.
2. This is the action name for the new action that we will create. It gets triggered when the `startTheTour` NPC option is shown.
3. This extends the existing conversation.

These modifications allow the player to choose whether they want to take a town tour or not.

Now speak with the NPC again to take the tour.

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Actions/2-TownTour /tutorialQuest overwrite
    ```

## Summary

You've learned what actions are and how to create them. You can now give a player some food or
even teleport him through the whole town! More actions can be found in the [actions list](../../../Documentation/Scripting/Building-Blocks/Actions-List.md).
Next you will learn how to give tasks to the player using **objectives**. 
---
[:octicons-arrow-right-16: Objectives Tutorial](./Objectives.md){ .md-button .md-button--primary}
