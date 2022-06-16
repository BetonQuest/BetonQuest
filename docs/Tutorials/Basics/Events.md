## About
:octicons-clock-24:  30 minutes · :octicons-tag-16: Basics

After the [conversations tutorial](Conversations.md) we will now investigate in events. These allow you to create events
and make the quest more authentic through the different types.



!!! danger "Requirements"
    There are no further requirements for this part of the tutorial, but it is advisable to first look at the
    [conversations tutorial](Conversations.md) if not already done.

## 1. Creating the folder structure for your first event

We recommend adding a new file to your `QuestPackage` named `events.yml`.
So that you get a better and clearer structure.
Here is an overview of what your structure should look like now:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: events.yml
    - :material-folder-open: conversations
        - :material-file: jack.yml

We now have our file structure ready and can start with the events. More on that in the next step.

## 2. Defining your first event

Now that we have created the `events.yml`, open it up, and we will bring the event `giveFoodToPlayer` to live!
The event is defined with a `give` event followed by the item and amount separated by a colon.

``` YAML title="events.yml" linenums="1"
events:
  giveFoodToPlayer: give steak:16
```

This looks good. Now before we can test if the event works ingame we have to create the item `steak` because
BetonQuest don't know what a `steak``is right now. So we have to add this item with the vanilla name.

??? info "In this tutorial part we are using the `give` event so there are some things to know about it:"

    * `giveFoodToPlayer: `  is the Name of the event. You can name it whatever you want to. We recommend to name it properly so you still know what the event do. So it is easier to recognise.
    * `give` is the second argument and defines the event type.
    * `steak:16` this is the third argument. This defines which item you want to give and which amount sperated with a colon.

## 3. Creating the item in the items section

For some event types like the `give` event you need to specify an item in the `items section` because BetonQuest need
to refer to a vanilla item.
We will create the item in the `package.yml` file. We added the lines 4 and 5 to define our item.

``` YAML title="package.yml" hl_lines="4-5" linenums="1"
npcs:
  '1': Jack

items:
  steak: minecraft:cooked_beef
```

This is the `items section`. In this section you define all the items you want to create/use in your quest.
`steak:` is the name of the item. You can name it as whatever you want. `minecraft:cooked_beef` is the vanilla item name that is needed to specify what item it should be.

## 4. Testing your first event inGame

The goal is that you will learn how to test your events independently of your conversations or something.
This is also a good method to figure out where some errors come from.

You don't need any conversations to test your events. It is really simple if you follow these steps.
Everytime you edited something, and you want to test it, it is very important to save all your files.
After that you can type the command `/bq reload` on your server. There should be no errors from BetonQuest if so then start at the beginning from this tutorial.

Now we can enter `/bq event player tutorialQuest.giveFoodToPlayer` on the server.
This command should give you the specific amount of steak if you've done everything right!

!!! info "More information about the command"

    * `/bq event` defines what you want to do. In this example do something with the event section.
    * `player` here goes the player name. You can trigger it for yourself or other players.
    * `tutorialQuest` is the name of the QuestPackage. This is helpful because you can have events with the same name in different packages.
    * `giveFoodToPlayer` is the event name that you want to trigger. (Dont forget to separate it with a dot. `tutorialQuest.giveFoodToPlayer`)

!!! hint "This does not work? Download the example here"
    `Download Link`

## 5. Integration of the event into the conversation

To successfully create an event in a conversation it is important to integrate it at the right line.
Let's start with creating a new conversation to `giveFoodToPlayer` and to say `thankYou`.

``` YAML title="jack.yml" hl_lines="9-10 13-14" linenums="1" 
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
    player_options:
      #...
      thankYou:
        text: Oh that smells really good!
```

After that you can test your conversation. The NPC should now give the player some food at some point.

!!! danger ""
Think about where you put your events! In this example case it is in `foodAnswer` so it is triggered when the
conversation starts. If you placed it in `thankYou` option then the player have to answer him to get the food.

## 6. Creating your first folder event

Now we will create a folder event for a nice guiding tour through the city. We need some more conversations and a new
events for that.
The goal is that you know how to trigger multiple events with just one main event. Here some more detailed information
about the [folder event](../../Documentation/Events-List.md#folder-folder).

``` YAML title="jack.yml" hl_lines="11-18 24-29" linenums="1" 
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
        text: Yeah thats true. You know what also smells good except the food? The fresh air in this town here! Would you like to take a little tour through the nicest places here?
        pointer: enoughTime,noTimeForThat
      startTheTour:
        text: That sounds great! It is a honor for me to guide you through the town..
        events: townTourEvent # (1)!
      noProblem:
        text: Thats fine! Maybe you have time another day... Just talk to me again. See you!
    player_options:
      #...
      thankYou:
        text: Oh that smells really good!
        pointer: townTour
      enoughTime:
        text: Yes of course! Show me everything.
        pointer: startTheTour
      noTimeForThat:
        text: Sorry but I dont have time now...
        pointer: noProblem
```

1. This is the event name that we will create in the events section. It gets triggered on the `startTheTour` NPC option.

So we added some new conversations to make everything authentic. We also added a new event to a conversations that's called
`townTourEvent`.
As we already know, it is not enough to add a event to the conversation. We still need to define what exactly the event should do.

To define the event we need to open the `events.yml` again.

``` YAML title="events.yml" hl_lines="3-7" linenums="1"
events:
  giveFoodToPlayer: give steak:16
  townTourEvent: folder location1,location2,location3,blacksmith delay:2 period:5
  location1: teleport 100;70;100;world
  location2: teleport 200;73;200;world
  location3: teleport 300;71;300;world
  blacksmith: teleport 50;70;50;world
```

--8<-- "Tutorials/download.md"
    `Download Link`

The folder event `townTourEvent` triggers the events in order to what's first. The `delay:2` is the delay of the main folder event so
the main event is starting after 2 seconds delay. The argument `period:5` is for a period between every single event in this folder event.
In this case we have four events in one main event and the next event gets triggered after the next five seconds.


In our example case we only use the `teleport` event for every single event to teleport to a new location every five seconds
until we get to our final destination `blacksmith`. After the `blacksmith` event the folder event is finished.

!!! info "Nice to know"

    * You can have as many events as you want defined in a folder event.
    * It's not neccessary to have a delay or a period, these are optional arguments.
    * Every event in a folder event must be separated with a comma.



## Further Information
!!! info ""
    You want some more information about events? [Events Reference](../../Documentation/Reference.md#events)

## Summary

!!! abstract ""
    You've learned the basics to create events and where to define them. You can now give a player some food with an event
    and even teleport him through the whole city place by place!
    In the next part of the basics tutorial you will learn how to give the food to the player with **events**!



## What`s next?
[Objectives Tutorial :octicons-arrow-right-16:](#){ .md-button }
