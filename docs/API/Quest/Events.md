---
icon: material/application-export
---
@snippet:api-state:unfinished@

## Listening to BetonQuest (Bukkit) events

BetonQuest exposes some of its actions as Bukkit events.  
You can find these events in `org.betonquest.betonquest.api.bukkit.event` package.
[Use them as you would use any other Bukkit event](https://bukkit.fandom.com/wiki/Event_API_Reference#The_Basics).

The event API itself is stable as the API state tells, but some events will change from time to time or get renamed/replaced.
If you need any additional events, create a discussion or pull request on GitHub.

## Event List

| Event                         | Description                                                                                                   |
|-------------------------------|---------------------------------------------------------------------------------------------------------------|
| ConversationOptionEvent       | Signals that a profile has selected an option in a conversation.                                              |
| LoadDataEvent                 | Fires before and after BetonQuest loading or reloading all events, conditions, objectives, conversations etc. |
| PlayerConversationEndEvent    | Fires when a profile ends a conversation.                                                                     |
| PlayerConversationStartEvent  | Fires when profile starts a conversation.                                                                     |
| PlayerJournalAddEvent         | Fires when new content is added to a profile's journal.                                                       |
| PlayerJournalDeleteEvent      | Fires when content is removed from a profile's journal.                                                       |
| PlayerObjectiveChangeEvent    | Fires when a profile's objectives change.                                                                     |
| PlayerTagAddEvent             | Fired when a tag is added to a profile.                                                                       |
| PlayerTagRemoveEvent          | Fired when a tag is removed from a profile.                                                                   |
| PlayerUpdatePointEvent        | Fired when a profile's points are updated.                                                                    |
| ProfileEvent (abstract)       | Represents a profile related event.                                                                           |
| QuestCompassTargetChangeEvent | Fired when the compass calls the setCompassTarget method.                                                     |
| QuestDataUpdateEvent          | Fired when the quest data updates.                                                                            |
| Npc/NpcInteractEvent          | Event for interaction with BetonQuest Npcs.                                                                   |
| Npc/NpcVisibilityUpdateEvent  | Event to call if a Npc is externally modified and its BetonQuest features needs recalibration.                |
