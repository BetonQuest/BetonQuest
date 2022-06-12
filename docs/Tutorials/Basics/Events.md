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

``` YAML title="jack.yml" hl_lines="26-29 32-33" linenums="1" 
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      ...
      foodAnswer:
        text: Your welcome! Take it... &7*gives food*
        events: giveFoodToPlayer
        pointer: thankYou
    player_options:
      ...
      thankYou:
        text: Oh that smells really good!
```

Now that we've added the event to the right place in the `foodAnswer` conversation we continue with defining the event
in the `event section`.

!!! danger ""
    Think about where you put your events! In this example case it is in `foodAnswer` so it is triggered when the
    conversation starts. If you placed it in `thankYou` option then the player have to answer him to get the food.

## 2. Defining the event in the events file

