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

## 2. Defining the event in the events file

Now that we have created the `events.yml`, open it up, and we will bring the event `giveFoodToPlayer` to live!
The event is defined with a `give` event followed by the item and amount separated by a colon.

``` YAML title="events.yml" hl_lines="1-2" linenums="1"
events:
  giveFoodToPlayer: give steak:16
```

An event exists of three basic things:
  - `giveFoodToPlayer: ` is the Name of the event. You can name it whatever you want to. We recommend to name it properly
        so you still know what the event does, and it is easier to recognise
