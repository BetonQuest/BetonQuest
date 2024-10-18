---
icon: fontawesome/brands/buromobelexperte
tags:
  - Pickrandom
  - Daily Quest
---

In this tutorial, you will learn about advanced daily quests. These help you create detailed dialogues between players 
and NPCs, which are key for great storytelling. You'll discover how to build complex storylines and interactive dialogues
that make daily quests more engaging and exciting for players.

<div class="grid" markdown>
!!! example "Related Docs"
    * [Events Reference](../../../Documentation/Scripting/About-Scripting.md#events)
    * [Events List](../../../Documentation/Scripting/Building-Blocks/Events-List.md)
    * [Objectives List](../../../Documentation/Scripting/Building-Blocks/Objectives-List.md)
</div>

## 1. Adding a `pickrandom` event

Open the `events.yml` from your QuestPackage and add the following content, to add the ability to pick randomly between
multiple tasks.

``` YAML title="events.yml" linenums="1"
events: # (1)!
  pickdaily: "pickrandom 50%daily1,50%daily2"
  
  daily1: 'objective add mine_diamonds'
  daily2: 'objective add mine_iron'
```

1. All events must be defined in an `events` section.

So what do we see here?

* `pickrandom` to pick a random event to be executed. For our example we will pick between two different daily tasks.
 You can always expand this with more options.
* Two `objective-events` called daily1 and daily2 that will be randomly picked.

Before we can test if the event works in game we have to create both objectives in the `objectives.yml`.

## 2. Creating the objectives

In the `objectives.yml` create a new section where you add both (or more if you want more daily tasks) objectives.


``` YAML title="objectives.yml" linenums="1"
objectives: # (1)!
  mine_diamonds: "block DIAMOND_ORE -16 events:dailyComplete" 
  mine_iron: "block IRON_ORE -16 events:dailyComplete"
```

1. All objectives must be defined in an `objectives` section.

Since we defined a new event in the objective, we need to add it to our `events.yml` before running a test command in-game
to check if the randomizing works. In this example, I'll add a folder to notify the player that they finished the quest
and to add the tag "questDone".

``` YAML title="events.yml" hl_lines="7-9" linenums="1"
events: # (1)!
  pickdaily: "pickrandom 50%daily1,50%daily2"
  
  daily1: 'objective add mine_diamonds'
  daily2: 'objective add mine_iron'
  
  dailyComplete: 'folder addQuestDoneTag,NotifyPlayer'
  notifyPlayer: 'notify You mined enough!\nReturn to the Blacksmith! io:Title sound:firework_rocket'
  addQuestDoneTag: 'tag add questDone'
```

1. All events must be defined in an `events` section.

!!! question ""
    **Tip:** Highlighted lines in {==blue==} are new compared with the previous example.


If everything works correctly, feel free to move on to the next section, which will focus on creating delays for resetting
the tags and other elements related to the player's progress.

---
[:octicons-arrow-right-16: Adding Delays to the Quest ](./Delays.md){ .md-button .md-button--primary}

