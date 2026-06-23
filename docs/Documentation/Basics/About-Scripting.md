---
icon: octicons/info-16
---

BetonQuest does not have a predefined structure for quests or scripts in general and therefore can be freely designed.
This is made possible by a powerful quest scripting language.

The difference is easier shown than just explained.
The following graph compares the two quest structures, but please keep in mind that BetonQuest is much more powerful than shown in this example.

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

Instead of a linear quest structure, or any other predefined structure that you fill out to create a quest,
you can combine basic elements of quests to create complex directives and form the world according to your imagination.

The freedom begins at creating the file structure with almost no restrictions.

## Quest Packages

Inside BetonQuest plugin folder, you will find a folder called `Quest Packages`.
This folder will contain all the packages you are creating yourself.
You may create a package per quest or questline, or even combine multiple packages into one. It's up to you!

### Scripting files

Inside those Quest Packages, besides the mandatory `package.yml` file,
you are free to create any number of other files that will be loaded, merged and interpreted by the engine.
Files with the `.yml` extension are recognized as quest scripts.
All those files will follow similar rules as the `package.yml` file and may contain any selection of element sections to define elements in.
Typically, users divide their files based on the element types or purpose.

<div class="grid" markdown>

!!! info "By element type"
    ```mermaid
    ---
    config:
      treeView:
        rowIndent: 30
      themeVariables:
        treeView:
          labelFontSize: '20px'
          labelColor: '#5d6cc0'
          lineColor: '#EEEEEE'
    ---
    treeView-beta
        "Quest Packages"
            "KingRebellion"
                "package.yml"
                "actions.yml"
                "conditions.yml"
                "objectives.yml"
                "items.yml"
                "journal.yml"
    ```
!!! info "By purpose"
    ```mermaid
    ---
    config:
      treeView:
        rowIndent: 30
      themeVariables:
        treeView:
          labelFontSize: '20px'
          labelColor: '#5d6cc0'
          lineColor: '#EEEEEE'
    ---
    treeView-beta
        "Quest Packages"
            "RescueChildren"
                "package.yml"
                "accept_the_quest.yml"
                "find_the_bandits.yml"
                "kill_the_bandits.yml"
                "rescue_children.yml"
                "claim_the_rewards.yml"
    ```
</div>

[Quest Package Guide](./Packages-&-Templates.md){ .md-button }

## Element Sections

An element section is a configuration section grouping elements of the same type together.
Each element section has a unique name and can contain any number of elements. 
It may also be defined in multiple files without conflicting with each other as long as keys with values are unique across all files of a package.
Every [type of element](../Reference/Definition-Encyclopedia.md#element-types) has its own section.

### Subsections

Any _single line instruction_ can also be set in a subsection to better organize them by a common prefix.  
To address them you simply use separators as in YAML itself:

```YAML title="Instructions in subsections"
actions:
  startPart1: "folder part1.addTag,part1.teleport"
  part1:
    addTag: "tag add part1_active"
    teleport: "teleport 100;200;300;world"
  part2:
    step1:
      notifyStart: "notify Deeply nested action!"
      startObjective: "objective start part2.step1.objective"
objectives:
  part2:
    step1:
      objective: "jump 1"
```

More complex features utilizing sections can't be nested in such a manner.

[Scripting Elements](./Scripting-Elements.md){ .md-button }
