---
icon: fontawesome/solid/play
---
## About
:octicons-clock-24:  30 minutes · :octicons-tag-16: Basics

After you learned how to create conversations we will now take a look at events. These allow you to change the game 
world.

!!! danger "Requirements"
    Knowledge and setup of:
    
    * [Conversations Tutorial](Conversations.md)

## 1. What is an event?
Events are BetonQuest's way to change the game world. This can be anything from modifying a player's inventory
to placing blocks. 
In this tutorial, we will give the player items and teleport them to different locations using events.

## 2. Creating the folder structure for your first event

Add a new file to your `QuestPackage` named `events.yml`.
Here is an overview of what your directory structure should look like now:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: {==events.yml==}
    - :material-folder-open: conversations
        - :material-file: jack.yml

We now have our file structure ready and can start writing events!

## 3. Defining your first event

Open the `events.yml` now that we have created it and add the following content: 

``` YAML title="events.yml" linenums="1"
events: # (1)!
  giveFoodToPlayer: "give steak:16"
```

1. All events must be defined in an `events` section.

So what do we see here?

* `giveFoodToPlayer`  is the name of the event. You are free to choose any name. However, it is  recommended to name 
   it after what it does. That just makes it easier to understand your quest.
* After the name follows the event instruction. 
    - `give` The first value in the instruction is always the event type.
    - `steak:16` This is an option of the give event. It defines which item you want to give and which amount 
      seperated by a colon.

Before we can test if the event works ingame we have to create the item `steak` because BetonQuest doesn't know what a `steak` is.

## 4. Creating the item in the items section

For some event types like `give` you need to specify an item in the `items` section.
It holds definitions of all items you want to create/use in your quest.
We will create the item section in the "_package.yml_" file. 

``` YAML title="package.yml" hl_lines="4-5" linenums="1"
npcs:
  '1': Jack

items:
  steak: COOKED_BEEF # (1)!
```

1. Links the `steak` item name from your BetonQuest configs to the ingame `minecraft:COOKED_BEEF` item.

Now `steak` is an item name that can be used throughout BetonQuest.

## 5. Testing your first event ingame

Now let's see how to run events.

!!! warning ""
    It is very important to save all files everytime you test something!
    Type `/bq reload` on your server after saving.     
 
The easiest way to do this is by running a command:

Enter `/bq event YOUR-NAME-HERE tutorialQuest.giveFoodToPlayer` on the server.
This command will give you the specified amount of steak if you've done everything right!

| Command Part       | Meaning                                                                                                                           |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `/bq event`        | Tells BetonQuest that some event should be executed.                                                                              |
| `player`           | A player's name.                                                                                                                  |
| `tutorialQuest`    | The name of a QuestPackage. This is required because you could have events with the same name in different packages.              |
| `giveFoodToPlayer` | The name of the event to execute. Don't forget to separate it with a dot from the package `tutorialQuest{==.==}giveFoodToPlayer`. |

You can also run this command from the console (without the slash at the start). 

--8<-- "Tutorials/download.md"
    `Download Link`


## 6. Integrating events into conversations

Events can also be run from conversations.

``` YAML title="jack.yml" hl_lines="9-10 13-14" linenums="1" 
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      #... (1)
      foodAnswer:
        text: Your welcome! Take it... &7*gives food*
        events: giveFoodToPlayer # (2)!
        pointer: thankYou
    player_options:
      #...
      thankYou: 
        text: Oh that smells really good!
```

1. The tutorial will only show relevant parts of the examples from now on. 
2. The event argument must contain one or multiple event names. These events are executed when the corresponding
   option is shown to the player.<br>This argument can be used on both player and npc options.


Make these changes to your conversation, reload and test! The NPC should now give the player food.

## 7. Creating folder events

Now we will create a tour through the mayors city. Meanwhile, we will learn about the teleport and folder events.

 [folder event](../../Documentation/Events-List.md#folder-folder).

``` YAML title="jack.yml" hl_lines="11-18 23-29" linenums="1" 
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      #...
      foodAnswer:
        text: Your welcome! Take it... &7*gives food*
        events: giveFoodToPlayer
        pointer: thankYou
      townTour:
        text: Yeah that's true. You know what also smells good? The fresh air in this town! Would you like to take a little tour?
        pointer: enoughTime,noTimeForThat # (1)!
      startTheTour:
        text: That sounds great! It is a honor for me to guide you through the town.
        events: townTourEvent # (2)!
      noProblem:
        text: That's fine! Maybe you have time another day... Just talk to me again. See you!
    player_options:
      #...
      thankYou:
        text: Oh that smells really good!
        pointer: townTour # (3)!
      enoughTime:
        text: Yes of course! Show me everything.
        pointer: startTheTour
      noTimeForThat:
        text: Sorry, but I don't have time now...
        pointer: noProblem
```

1. The player once again has a choice.
2. This is the event name for the new event that we will create. It gets triggered when the `startTheTour` NPC option is shown.
3. This extends the existing conversation.

These modifications allow the player to choose whether they want to take a city tour.
We also added a new event to a conversation option that's called `townTourEvent`.
As we already know, isn't enough to add an event to the conversation. We also need to define the event in the `events` section.

Open the "_events.yml_" file and add these lines:

``` YAML title="events.yml" hl_lines="3-7" linenums="1"
events:
  giveFoodToPlayer: give steak:16
  townTourEvent: folder location1,location2,location3,blacksmith delay:2 period:5
  location1: teleport 100;70;100;world
  location2: teleport 200;73;200;world
  location3: teleport 300;71;300;world
  blacksmith: teleport 50;70;50;world
```

As you can see, there are a few new events of the types `folder` and `teleport`. 
Every event type is documented in the events list, read what the [folder](../../Documentation/Events-List.md#run-multiple-events-folder)
and [teleport](../../Documentation/Events-List.md#teleport-teleport) events do there. 

You probably want to adjust the coordinates and the world name to your test server's world.

Running the townTourEvent will teleport you to a new location every five seconds
until we get to our final destination, the blacksmith. After the `blacksmith` event is run, the folder event is finished.

Now speak with the NPC again to take the city tour.

--8<-- "Tutorials/download.md"
    `Download Link`


## Further Information
!!! info ""
    * You can find more information about what events are in the [events reference](../../Documentation/Reference.md#events)
    * All events can be found in the [events list](../../Documentation/Events-List.md)

## Summary

!!! abstract ""
    You've learned what events are and how to create them. You can now give a player some food or
    even teleport him through the whole city!
    Next you will learn how to give tasks to the player using **objectives**. 

## What`s next?
[Objectives Tutorial :octicons-arrow-right-16:](#){ .md-button .md-button--primary}
