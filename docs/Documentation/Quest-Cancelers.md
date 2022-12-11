---
icon: material/cancel
hide:
  - footer
---

If you want to let your players cancel their quest there is a function for that. In _package.yml_ file there is `cancel`
branch. You can specify there quests, which can be canceled, as well as actions that need to be done to actually cancel
them. The arguments you can specify are:

* `name` - this will be the name displayed to the player. All `_` characters will be converted to spaces. If you want to include other languages you can add here additional options (`en` for English etc.)
* `conditions` - this is a list of conditions separated by commas. The player needs to meet all those conditions to be able to cancel this quest. Place there the ones which detect that the player has started the quest, but he has not finished it yet. 
* `objectives` - list of all objectives used in this quest. They will be canceled without firing their events.
* `tags` - this is a list of tags that will be deleted. Place here all tags that you use during the quest.
* `points` - list of all categories that will be entirely deleted.
* `journal` - these journal entries will be deleted when canceling the quest.
* `events` - if you want to do something else when canceling the quest (like punishing the player), list the events here.
* `loc` - this is a location to which the player will be teleported when canceling the quest (defined as in teleport event);

To cancel the quest you need to open your backpack and select a "cancel" button. There will be a list of quests which can be canceled. Just select the one that interests you and it will be canceled.
