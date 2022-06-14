## About
:octicons-clock-24:  30 minutes · :octicons-tag-16: Basics

After the [conversations tutorial](Conversations.md) we will now investigate in events. These allow you to create events
and make the quest more authentic through the different types. Therefore these are the basic tool for story telling.



!!! danger "Requirements"
    There are no further requirements for this part of the tutorial, but it is advisable to first look at the 
    [conversations tutorial](Conversations.md) if not already done.

## 1. Create your first event!

To successfully create an event in a conversation it is important to implement it at the right place.
You have to create a `event` category in your conversation, and you have to define what exactly the event should do
in the `events section`. Okay so lets start with actually creating the event and a new conversation to say `thankYou`.

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

Now that we've added the event to the right place in the `foodAnswer` conversation we continue with defining the event
in the `event section`.

!!! danger ""
    Think about where you put your events! In this example case it is in `foodAnswer` so it is triggered when the
    conversation starts. If you placed it in `thankYou` option then the player have to answer him to get the food.

## 2. Defining the event in the events file

We continue with creating a file named `events.yml` in our `tutorialQuest` folder.
The structure after creating the file should look like this:

* :material-folder-open: tutorialQuest
    - :material-file: package.yml
    - :material-file: events.yml
    - :material-folder-open: conversations
        - :material-file: jack.yml  

Now that we have created the `events.yml`, open it up, and we will bring the event `giveFoodToPlayer` to live!
The event is defined with a `give` event followed by the item and amount separated by a colon.

``` YAML title="events.yml" hl_lines="1-2" linenums="1"
events:
  giveFoodToPlayer: give steak:16
```

In this step we have created an event in the `events section` with the name `giveFoodToPlayer` and specified what the
event should do.
In the next step we will create the `items section` in our recently created `package.yml`. That is the last step before
we can test our event inGame!

!!! info ""
    There a lot of event types to use! You can look it up on [events documentation](../../Documentation/Events-List.md)

## 3. Creating the item in the items section

For some event types like the `give` event you need to specify an item in the `items section` because BetonQuest need
to refer to a vanilla item.
We create the event and events section in the `package.yml`, so open it up:

``` YAML title="package.yml" hl_lines="4-5" linenums="1"
npcs:
  '1': Jack

items: # (1)!
  steak: minecraft:cooked_beef # (2)!
```

1. This is the `items section`. In this section you define all the items you want to create/use in your quest.
2. `steak:` is the name of the item. You can name it as whatever you want. `minecraft:cooked_beef` is the vanilla item name that is needed to specify what item it should be.

Now it is time to test it if it works inGame. Don't forget to save the files and do `/bq reload`

--8<-- "Tutorials/download.md"
    `Download Link`


## Further Information
!!! info ""
    You want some more information about events? [Events Reference](../../Documentation/Reference.md#events)

## Summary

!!! abstract ""
    You've learned the basics to create events and where to define them. You can now give a player some food with an event.  
    In the next part of the basics tutorial you will learn how to give the food to the player with **events**!



## What`s next?
[Objectives Tutorial :octicons-arrow-right-16:](#){ .md-button }
