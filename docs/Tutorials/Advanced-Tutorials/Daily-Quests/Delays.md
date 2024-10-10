---
icon: material/folder-play-outline
tags:
  - Delay
  - Daily Quest
---

The next tutorial focuses on creating a delay that resets our daily quest after 24 hours. This will ensure players can 
complete another quest after a full day has passed. Follow along to implement this feature seamlessly.

<div class="grid" markdown>
!!! example "Related Docs"
    * [Events List](../../../Documentation/Scripting/Building-Blocks/Events-List.md)
    * [Objectives List](../../../Documentation/Scripting/Building-Blocks/Objectives-List.md)
</div>

## 1. Start a delay event

Open the `events.yml` from your QuestPackage and add the following content to it

``` YAML title="events.yml" linenums="1"
events: # (1)!
  start_reset_timer: "objective add reset_timer"
```

1. All events must be defined in an `events` section.

Afterwards we add a new objective into our `objectives.yml` where we start the delay.

``` YAML title="events.yml" hl_lines="11" linenums="1"
events:
  pickdaily: "pickrandom 50%daily1,50%daily2"
  
  daily1: 'objective add mine_diamonds'
  daily2: 'objective add mine_iron'
  
  dailyComplete: 'folder addQuestDoneTag,NotifyPlayer'
  notifyPlayer: 'notify You mined enough!\nReturn to the Blacksmith! io:Title sound:firework_rocket'
  addQuestDoneTag: 'tag add questDone'
  
  start_reset_timer: "objective add reset_timer"
```

Before we move on to our `objectives.yml`, let's add a folder event and several smaller events to remove all tags and
objectives that the player might have. In this example, we assume the player has either a `startedQuest` or a `questDone` 
tag. We will remove both and all possible objectives in case a player did not finish the quest within 24 hours.


``` YAML title="events.yml" hl_lines="5 7 12 14-15 19" linenums="1"
events:
  pickdaily: "pickrandom 50%daily1,50%daily2"
  
  daily1: 'objective add mine_diamonds'
  daily1_r: 'objective remove mine_diamonds'
  daily2: 'objective add mine_iron'
  daily2_r: 'objective remove mine_iron'
  
  dailyComplete: 'folder addQuestDoneTag,NotifyPlayer'
  notifyPlayer: 'notify You mined enough!\nReturn to the Blacksmith! io:Title sound:firework_rocket'
  addQuestDoneTag: 'tag add questDone'
  addQuestDoneTag_r: 'tag add questDone'
  
  addQuestStartedTag: 'tag remove questStarted' # (1)!
  addQuestStartedTag_r: 'tag remove questStarted'
  
  start_reset_timer: 'objective add reset_timer'
  
  folderReset: 'folder daily1_r,daily2_r,addQuestStartedTag_r,addQuestDoneTag_r'
```

1. It works the same if you don't use points instead of tags.

So what do we see here?

* `dailyX_r`  is reversing the dailyX event, essentially removing the objective.
* `addQuestXXXXTag_r` is reversing the tag event, removing the tag from the player
* `folderReset` will be used as folder event that runs once 24 hours are over

## 2. Create the delay objective

In the next step we will create our delay objective to reset the quest 24 hours after the player finished the daily
quest on his part.

``` YAML title="objectives.yml" hl_lines="4" linenums="1"
objectives:
  mine_diamonds: "block DIAMOND_ORE -16 events:dailyComplete" 
  mine_iron: "block IRON_ORE -16 events:dailyComplete"
  reset_timer: "delay 86400 seconds events:folderReset"
```

This creates a timer that runs 24 hours and fires the folderReset event-folder once complete. You can also use schedules
to reset the quests for all players at exactly midnight, but this would screw players that just recently started their
quest.

Your Quest should now follow this structure:

!!! info "Daily-Quest Cycle"
    ``` mermaid
    graph LR
    X{Player accepts Quest} --> A
    X --> B
    A[Add StartedTag] --> D[Player executes objective];
    B[Pickrandom Task] --> D[Player executes objective];
    D --> E
    E[Player finished Quest] --> F[Start Reset-Timer]
    F --> |Time is up|G[ResetFolder]
    G --> Y
    Y{Player can accept a new Quest}
    ```
    
## Summary

After completing this tutorial, you will have learned how to create advanced daily quests using conversations, events, 
and personal player timers. In the next section you can find some useful tips and tricks to enhance your daily quest system.
---
[:octicons-arrow-right-16: Tips and Tricks ](./Extras.md){ .md-button .md-button--primary}
