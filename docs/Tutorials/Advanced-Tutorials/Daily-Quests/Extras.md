---
icon: material/star
tags:
  - Extras
  - Daily Quest
---

This chapter features additional tips and tricks for daily quests. Learn how to optimize quest design and improve player
engagement. Discover advanced techniques to make your daily quests even more compelling.

<div class="grid" markdown>
!!! example "Related Docs"
    * [Events List](../../../Documentation/Scripting/Building-Blocks/Events-List.md)
    * [Objectives List](../../../Documentation/Scripting/Building-Blocks/Objectives-List.md)
</div>

## Expiration Timer: `expire_timer`

This event/objective works similarly to our reset_timer but is triggered once the player accepts the quest. It 
automatically resets a quest once the timer expires, allowing the player to receive a new quest even if they chose not 
to complete the daily quest.


``` YAML title="events.yml" linenums="1"
events:
  start_expire_timer: 'objective add expire_timer'
  folderReset: 'folder daily1_r,daily2_r,addQuestStartedTag_r,addQuestDoneTag_r'
```

``` YAML title="objectives.yml"linenums="1"
objectives:
  expire_timer: "delay 86400 seconds events:folderReset"
```

## Variables: `title` and `count`

Instead of writing npc_options for each and every task we can also just use one npc_option that mentions the task to 
the player by using variables. Let's start by creating a variable event in our `events.yml`

``` YAML title="events.yml" linenums="1"
events:
  mine_iron_title: "variable task title Iron"
  mine_iron_count: "variable task count 16"
  mine_iron_subtext: "variable task subtext Mine"
  
  mine_diamond_title: "variable task title Diamonds"
  mine_diamond_count: "variable task count 32"
  mine_diamond_subtext: "variable task subtext Mine"
  
  gather_wood_title: "variable task title Spruce Wood"  # (1)!
  gather_wood_title_count: "variable task count 128"
  gather_wood_subtext: "variable task subtext Cut"
```

1. Example using wood

Before we can use our new events we need to define the variable objective in our `objectives.yml`...

``` YAML title="objectives.yml" linenums="1"
objectives:
  task: "variable no-chat"
```

... and rework our `events.yml` to take usage in our new variables ...


``` YAML title="events.yml" hl_lines="2-3 5-6 8-9 13-15 19-21 33" linenums="1"
events:
  folderpick: 'folder start_task,pickdaily' # (1)!
  pickdaily: 'pickrandom 50%folderdaily1,50%folderdaily2'
  
  start_task: 'objective add task'  # (2)!
  start_task_r: 'objective remove task'
  
  folderdaily1: 'folder daily1_title,daily1_count,daily1_subtext,daily1'
  folderdaily2: 'folder daily2_title,daily2_count,daily2_subtext,daily2'
  
  daily1: 'objective add mine_diamonds'
  daily1_r: 'objective remove mine_diamonds'
  daily1_title: 'variable task title Diamonds'
  daily1_count: 'variable task count 32'
  daily1_subtext: 'variable task subtext Mine'
  
  daily2: 'objective add mine_iron'
  daily2_r: 'objective remove mine_iron'
  daily2_title: 'variable task title Iron'
  daily2_count: 'variable task count 16'
  daily2_subtext: 'variable task subtext Mine'
  
  dailyComplete: 'folder addQuestDoneTag,NotifyPlayer'
  notifyPlayer: 'notify You got enough!\nReturn to the Blacksmith! io:Title sound:firework_rocket'
  addQuestDoneTag: 'tag add questDone'
  addQuestDoneTag_r: 'tag add questDone'
  
  addQuestStartedTag: 'tag remove questStarted'
  addQuestStartedTag_r: 'tag remove questStarted'
  
  start_reset_timer: 'objective add reset_timer'
  
  folderReset: 'folder daily1_r,daily2_r,addQuestStartedTag_r,addQuestDoneTag_r,start_task_r'
  
```

1. This is our new event that we start in the conversation or manually
2. This is the variable objective we defined before

... and finally modify our `conversations.yml` to use the variables when the NPC gives the task to the player:

``` YAML title="conversations.yml" linenums="1"
conversations:
  conv_2:
    quester: "Blacksmith"
    first: "npc_text_3"
    NPC_options:
      npc_text_3:
        text: "%objective.task.subtext% %objective.task.count%x %objective.task.title% for me. It is needed 
        to keep doing what I do."
```

If the conversation would now pick the "Mine Iron" Task the Text would automatically translate to:
`Mine 16 Iron for me. It is needed to keep doing what I do.`

!!! warning "Tip"
    You need to execute the folderpick event from your `events.yml` before you show this text to the player, 
    otherwise the variables are empty.
