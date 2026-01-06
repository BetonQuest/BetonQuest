---
icon: material/cancel
---
# :material-cancel: Quest Cancelers
<video controls autoplay muted loop src="../../../_media/content/Documentation/Features/QuestCanceler.mp4" width="100%">
          Sorry, your browser doesn't support embedded videos.
</video>   

You can easily let players cancel their quests using the cancel option in the quest backpack (or `/cancelquest`). 
Cancelers also provide an easy way to clean up all the data that was created during the quest. They can also be triggered by actions.
     
## Setup 
Define a `cancel` section anywhere in your quest package. This section will contain all cancelers. Each canceler has an identifier. 

```YAML title="Example"
cancel:
  woodQuest:
    name: "&2Wood for the Innkeeper" #(1)!
    conditions: "wood_started,!wood_paid" #(2)!
    objectives: "farmWood" #(3)!
    tags: "wood_started,wood_done,wood_paid" #(4)!
    points: "wood" #(5)!
    journal: "wood_started,wood_done,wood_paid" #(6)!
    actions: "punishPlayer,sendMessage" #(7)!
    location: "100;200;300;world" #(8)!
  dragonQuest:
    name: "&4Dragon Slayer"
    conditions: "dragon_started,!dragon_done"
    objectives: "killDragon"
```

1. Display name that will be shown in the GUI.The name can be translated with this syntax:
   ```YAML
   name:
     en-US: '&2Wood for Innkeeper' # English translation
     de-DE: '&2Holz für den Gastwirt' # German translation
   ```
2. A list of conditions separated by commas. The player needs to meet all those conditions to be able to cancel this quest. Place there the ones which detect that the player has started the quest, but he has not finished it yet. 
3. A list of all objectives used in this quest. They will be canceled without firing their completion actions.
4. A list of tags that will be deleted. Place here all tags that you use during the quest.
5. A list of all points that will be entirely deleted from the player.
6. These journal entries will be removed from the player's journal.
7. You can run any action when the player cancels a quest. For example, if you want to punish the player for canceling a quest list the related actions here.
8. This is a location to which the player will be teleported when canceling the quest. Use the [ULF](../Scripting/Data-Formats.md#unified-location-formating) format.

## Related Actions

@snippet:actions:cancel@
