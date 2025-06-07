---
icon: simple/apachenetbeanside
---

## Hiding Players

<video controls loop src="../../../../_media/content/Documentation/Compatibility/PlayerHider.mp4" width="100%">
  Sorry, your browser doesn't support embedded videos.
</video>

You can also hide players for specific players in the `player_hider` section of your package. When the `source_player` meets the conditions,
every player that meets the `target_player` conditions will be completely hidden from them. 
This is really useful if you want a lonely place on your server 
or your quests break when multiple players can see or affect each other.
You can configure the interval which checks the conditions with the [`player_update_interval`](../../Configuration/Plugin-Config.md#hider-entity-hider-settings) setting.

Special behaviour:

* A player that meets the `source_player`conditions can no longer be pushed by other players.
* By leaving the e.g. `source_player` argument empty it will match all players.

```YAML
player_hider:
  example_hider:  #All players in a special region cannot see any other players in that region. If a player is outside the region, they can still see the `target_player`.
    source_player: in_StoryRegion
    target_player: in_StoryRegion
  another_hider: #No one can see any players inside a secret room.
    #The source_player argument is left out to match all players.    
    target_player: in_secretRoom
  empty_hider: #in_Lobby is a world condition. Therefore, the lobby world appears empty for everyone that is in it.
    source_player: in_Lobby
    #The target_player argument is left out to match all players.
```
