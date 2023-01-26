---
icon: material/party-popper
---

Parties are very simple. So simple, that they are hard to understand if you already know some other party system.
Basically, they don't even have to be created before using them. 
Parties are defined directly in conditions/events (`party` event, `party` conditions, check them out in the reference 
lists below). In such instruction strings the first argument is a number - range. It defines the radius where the party
members will be looked for. Second is a list of conditions. Only the players that meet those conditions will be 
considered as members of the party. It's most intuitive for players, as they don't have to do anything to be in a party
- no commands, no GUIs, just starting the same quest or having the same item - you choose what and when makes the party.

To understand better how it works I will show you an example of `party` event. Let's say that every player has an 
objective of pressing a button. When one of them presses it, this event is fired:

```YAML
party_reward: party 50 quest_started cancel_button,teleport_to_dungeon
```

Now, it means that all players that: are in radius of 50 blocks around the player who pressed the button AND meet `quest_started` condition will receive `cancel_button` and `teleport_to_dungeon` events. The first one will cancel the quest for pressing the button for the others (it's no longer needed), the second one will teleport them somewhere. Now, imagine there is a player on the other side of the world who also meets `quest_started` condition - he won't be teleported into the dungeon, because he was not with the other players (not in 50 blocks range). Now, there were a bunch of other players running around the button, but they didn't meet the `quest_started` condition. They also won't be teleported (they didn't start this quest).
