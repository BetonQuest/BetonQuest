---
hide:
  - footer
---
BetonQuest's quests do not have a predefined structure but can be freely designed.
This is made possible by a powerful quest scripting language.

<div class="grid" markdown>
!!! example "Traditional Quest Plugin Quest Structure"
    ```mermaid
      flowchart TD
          A[Quest Starts] --> B[Bring X Iron to NPC Y]
          B --> C[Quest Ends]
          
          style C fill:#16a349,stroke:#16a349
    ```
    
!!! example "BetonQuest Quest Structure"
    === "Explanation"
        BetonQuest's quests can have any structure you want! You imagination is the only limit!
        
        You can make anything from simple grind quests to complex story quests with dozends of player decisions and side
        quests. You can easily create different quest outcomes or story endings!
    === "Rebel Quest"
        ```mermaid
           flowchart TD
              A[Quest Starts] --> B[Spy on Rebels]
              B--> C[Decision: Inform King]
              B--> D[Decision: Betray King]
              
              C --> E[King rewards you]
              
              D --> F[Hunt the King down]
              F --Too slow--> H[Quest Fails]
              F --In Time--> G[You become King]
              
              style G fill:#16a349,stroke:#16a349
              style E fill:#16a349,stroke:#16a349
              style H fill:#ed1c24,stroke:#ed1c24
        ```
    === "Dragon Hunter Quest"
        ```mermaid
           flowchart TD
              A[Quest Starts] --> B[Gather Information about the Dragon]
              B--> C[Ignore wounded NPC]
              B--> D[Help wounded NPC to\n recieve secret information]
              
              C--> E[Harder Dragon Fight]
              D--> X[Easier Dragon Fight]
              
              E--You Die--> H[Quest Fails]
              X--You Die--> H[Quest Fails]
              
              E--Dragon Killed--> W[Quest Completed]
              X--Dragon Killed--> W[Quest Completed]
              
              style W fill:#16a349,stroke:#16a349
              style H fill:#ed1c24,stroke:#ed1c24
        ```      
</div>

## Building Blocks
The BetonQuest scripting language is based on a few basic building blocks which are outlined in the following sections.
They can be freely combined to create any quest you want.
All of these are defined using an _instruction text_.

```YAML title="Intstruction Text Example"
conditions: #(1)!
  myCondition: "health 10" #(2)!
events:
  myEvent: "hunger set 20"
objectives:
  myObjective: "mobkill ZOMBIE 10"
```

1. Every building block is defined in its own section. In this case, the sections contents is a condition.
2. `myCondition` is the name of this condition. The instruction text is `health 10`. 
   This is a condition which checks if the player has 10 health.

### Conditions

Conditions are the most versatile and useful tools in creating advanced quests. They allow you to control what options are available to player in conversations, how the NPC responds or if the objective will be completed. The reference of all possible conditions is down below.

You can negate the condition (revert its output) by adding an exclamation mark (`!`) at the beginning of it's name (in the place you use it, i.e. in conversations, not in the _conditions_ section).

You can use conversation variables instead of numeric arguments in conditions. If the variable fails to resolve (i.e. it will return an empty string) BetonQuest will use 0 instead.

### Events

In certain moments you will want something to happen. Updating the journal, setting tags, giving rewards, all these are done using events. You define them just like conditions, by specifying a name and instruction string. You can find instruction strings to all events in the event reference. At the end of the instruction string you can add `conditions:` or `condition:` (with or without `s` at the end) attribute followed by a list of condition names separated by commas, like `conditions:angry,!quest_started`. This will make an event fire only when these conditions are met.

You can use conversation variables instead of numeric arguments in events. If the variable fails to resolve (i.e. it will return an empty string) BetonQuest will use 0 instead.

### Objectives

Objectives are the main things you will use when creating complex quests. You start them with a special
event, `objective`. You define them in the _objectives.yml_ file, just as you would conditions or events. At the end of
the instruction string you can add conditions and events for the objective. Conditions will limit when the objective can
be completed (e.g. killing zombies only at given location in quest for defending city gates), and events will fire when
the objective is completed (e.g. giving a reward, or setting a tag which will enable collecting a reward from an NPC).
You define these like that: `conditions:con1,con2 events:event1,event2` at the end of instruction string . Separate them
by commas and never use spaces! You can also use singular forms of these arguments: `condition:` and `event:`.

If you want to start an objective right after it was completed (for example `die` objective: when you die, teleport you
to a special spawnpoint and start `die` objective again), you can add `persistent` argument at the end of an instruction
string. It will prevent the objective from being completed, although it will run all its events. To cancel such
objective you will need to use `objective delete` event.

Objectives are loaded at start-up, but they do not consume resources without player actually having them active. This
means that if you have 100 objectives defined, and 20 players doing one objective, 20 another players doing second
objective, and the rest objectives are inactive (no one does them), then only 2 objectives will be consuming your server
resources, not 100, not 40.


#### Global objectives

If you want an objective to be active for every player right after joining you can create a global objective.
This is done by adding `global` argument to the instruction of the objective.
When you then reload BetonQuest it is started for all online players and also will be started for every player who joins.

To prevent the objective from being started every time a player joins a tag is set for the player whenever the objective
is started and as long as the player has the tag the objective won't be started again if the player joins.  
These tags follow syntax `<package>.global-<id>`, where `<id>` is the objectives id and `<package>` the package where
the objective is located.

Possible use cases would be a quest which starts if a player reaches a specific location or breaks a specific block.

```YAML title="Example"
objectives:
  start_quest_mine: 'location 100;200;300;world 5 events:start_quest_mine_folder {++global++}'
```

## Tags

Tags are little pieces of text you can assign to player and then check if he has them. They are particularly useful to determine if player has started or completed quest. They are given with `tag` event and checked with `tag` condition. All tags are bound to a package, so if you add `beton` tag from within a package named `example`, the tag will look like `example.beton`. If you're checking for `beton` tag from within `example` package, you're actually checking for `example.beton`. If you want to check a tag from another package, then you just need to prefix it's name with that package, for example `quest.beton`.

## Points

Points are like tags, but with amount. You can earn them for doing quest, talking with NPC’s, basically for everything you want. You can also take the points away, even to negative numbers. Points can be divided to categories, so the ones from _beton_ category won’t mix with points from _quests_ group. Of course then you can check if player has (or doesn't have) certain amount and do something based on this condition. They can be used as counter for specific number of quest done, as a reputation system in villages and even NPC’s attitude to player.
