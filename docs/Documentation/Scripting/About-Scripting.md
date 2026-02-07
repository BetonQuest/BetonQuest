---
icon: octicons/info-16
---

BetonQuest's quests do not have a predefined structure and can be freely designed.
This is made possible by a powerful quest scripting language.

<div class="grid" markdown>
!!! example "Quest Structure with a Traditional Quest Plugin"
    === "Rebel Quest" 
        ```mermaid
           flowchart TD
               A[Quest Starts] --> B[Spy on Rebels]
               B --> C[Inform King]
               C --> D[Quest Ends]
               
             style D fill:#16a349,stroke:#16a349
        ```
    === "Explanation"
        This is the most common quest structure. The player is given a linear quest and has to complete it.
        There is no way to influence the outcome of the quest.
        
        The capabilities of the quest plugin cannot be used outside of these linear quests.
    
!!! example "Quest Structure with BetonQuest"
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
    === "Explanation"
        BetonQuest's quests can have any structure you want! Your imagination is the only limit!
        
        You can make anything from simple grind quests to complex story quests with dozends of player decisions and side
        quests. You can easily create different quest outcomes or story endings!
        
        Additionally, BetonQuest's features can be used outside of accepted quests. You can use them to create a world
        that feels alive and reacts to a player's actions.
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

```YAML title="Instruction Text Example"
conditions: #(1)!
  myCondition: "health 10" #(2)!
actions:
  myAction: "hunger set 20"
objectives:
  myObjective: "mobkill ZOMBIE 10"
```

1. Every building block is defined in its own section. In this case, the section's content is a condition.
2. `myCondition` is the name of this condition. The instruction text is `health 10`. 
   This is a condition which checks if the player has 10 health.

### Actions

In certain moments you will want something to happen. Updating the journal, setting tags, giving rewards, all these are
done using actions. You define them by specifying a name and instruction string like shown above.
At the end of the instruction string you can add the `conditions:` (with or without `s` at the end)
attribute followed by a list of condition names separated by commas, 
like `conditions:angry,!quest_started`. This will make an action fire only when these conditions are met.

[Explore all Actions](./Building-Blocks/Actions-List.md){ .md-button }


### Objectives

Objective are goals that player must complete. At first, they must be started for a player with the `objective` action.
When the player completes the objective, all defined actions are run. For example, you could reward the player by giving
them an item.

You define them in the `objectives` section as shown above. At the end of the instruction text you can add conditions
and actions for the objective. Conditions will limit when the objective can be completed (e.g. killing zombies only at
given location), and actions will fire when the objective is completed (e.g. giving a reward, or setting a tag which
will enable collecting a reward from an NPC). You define these like that: `conditions:con1,con2 actions:action1,action2`
at the end of instruction text. Separate them by commas and never use spaces!

If you want to start an objective right after it was completed you can add the `persistent` argument at the end of its instruction string.
For example, you could create a custom respawn system with a `die` objective. When the player dies, they will be
teleported to the spawnpoint and the `die` objective will be started again.
The `persistent` argument prevents the objective from being completed, although it will run all its actions. To cancel such
an objective you need to use `objective delete` action.

```YAML title="Example"
objectives:
  mineDiamonds: 'block DIAMONDS -10 actions:reward'
  die: 'die cancel respawn:100;200;300;world;90;0 actions:sendRespawnMessage conditions:hasCustomTotem'
```

#### Global objectives

If you want an objective to be active for every player right after joining, you can create a global objective.
This is done by adding `global` argument to the instruction of the objective.
When you then reload BetonQuest it is started for all online players and also will be started for every player who joins.

Possible use cases would be a quest which starts if a player reaches a specific location or breaks a specific block.

To prevent the objective from being started every time a player joins, a tag is set for the player whenever the objective
is started. With this tag, the objective will not be started again.  
These tags follow the syntax `<package>.global-<id>`, where `<id>` is the objectives id and `<package>` the package where
the objective is located.

```YAML title="Example"
objectives:
  startQuestByMining: 'location 100;200;300;world 5 actions:start_quest_mine_folder {++global++}'
```

#### Placeholders

!!! warning "Use with caution!"
    The updating behaviour of already started objectives might change in BetonQuest 3. Perhaps placeholder changes will
    be reflected in the amount of an active objective. This is not the case right now.

Objectives support placeholders for their amount options.
When the objective is started for a player, the amount is set to the placeholder's current value. The amount of an active objective will
not be updated if the placeholder changes.
Also, when the placeholder contains an invalid value for the given objective (e.g. a negative value) a default value of `1` is used.

```YAML title="Examples"
objectives:
  killMonsters: 'mobkill ZOMBIE %math.calc:(100-{point.reputation.amount})*2% actions:endSiege'
  breakObsidian: 'block OBSIDIAN %randomnumber.whole.-60~-40% actions:dailyReward'
  eatSteak: 'consume steak amount:%randomnumber.whole.2~6% actions:health_boost'
```

[Explore all Objectives](./Building-Blocks/Objectives-List.md){ .md-button }


### Conditions
Conditions allow you to control what options are available to players in conversations, how the NPC responds or if the objective
will be completed. They check if a given in-game state is present and return `true` or `false` as a result.

You can negate the condition (revert its output) by adding an exclamation mark (`!`) at the beginning of its name. 
This only works in the place where conditions are used (i.e. in conversations, not in the _conditions_ section).
If you do so, make sure to enclose the condition in quotes, otherwise YAML will give you a syntax error.
```YAML title="Example"
conditions:
  hasFullHealth: "health 20"
actions:
  helpWithHealing: "hunger set 20 conditions:!hasFullHealth"
```

[Explore all Conditions](./Building-Blocks/Conditions-List.md){ .md-button }

## Tags

Tags are little pieces of text you can assign to player. They are particularly useful to 
determine if player has started or completed quest. They are given with [`tag` action](./Building-Blocks/Actions-List.md#tag-manage-tags)
and checked with [`tag` condition](./Building-Blocks/Conditions-List.md#tag-has-tag).
All tags are bound to a package, so if you add the `questCompleted` tag from within a package named `monsterQuest`,
the tag will look like `monsterQuest.questCompleted`.

Read [working across packages](./Packages-&-Templates.md#working-across-packages) to learn how to work with tags across packages.

## Points

Points are numbers that can be assigned to a player. You can set them with the [`point` action](./Building-Blocks/Actions-List.md#point-manage-a-point-category).
you want. You can also take the points away, even to negative numbers. 
Of course then you can check if player has (or doesn't have) certain amount with the [`point` condition](./Building-Blocks/Conditions-List.md#point-has-point). 
They can be used as counter for specific number of quest done, as a reputation system in villages or even an NPC's 
attitude to player.
