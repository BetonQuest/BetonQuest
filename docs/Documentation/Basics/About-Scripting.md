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
