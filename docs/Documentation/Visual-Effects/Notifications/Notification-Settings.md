---
icon: material/volume-high
---
BetonQuest features a powerful notification system that allows you to display any information to your players.
You can freely choose between many NotifyIO's like simple chat output, (sub)titles, advancements or sounds. They all come
with unique options that allow you to customize them. Just take a look at this example configuration:

<video controls loop src="../../../../_media/content/Documentation/Notifications/NotifySystemOverview.mp4" width="100%">
  Sorry, your browser doesn't support embedded videos.
</video>


## Sending custom notifications

A truly custom notification can be sent using the [`notify`](../../Scripting/Building-Blocks/Events-List.md#sending-notifications-notify) event at any time.
Check out the [event's documentation](../../Scripting/Building-Blocks/Events-List.md#sending-notifications-notify) to learn how.

## Changing BetonQuest's built-in notifications
Almost every notification built into BetonQuest is customizable in some way. A list of the notifications can be found in
the *lang* directory. BQ natively supports [a number of languages](/../Documentation/Configuration/Plugin-Config/#language-default-plugin-language).
Within the *lang* directory, you can find BetonQuest's built-in notifications in their respective languages. They can have
their text changed, formatted or even have sounds added to them. 
  
### General notifications

General notifications can be anything from BetonQuest notifying a player that their language has been changed
to sending a notice about a new changelog to an admin.

For example:    
When BetonQuest fails to add a quest item to a player's inventory, it will send `&e*&bYour inventory is full!&e*`.    
This message, for English-speaking users, is defined in `en-US.yml` along with other default plugin messages.
You can redefine them to your liking.

The Notify System can do much more than just changing messages, though:

By default, all notifications will be displayed using the ChatIO, without a sound. You need to use
[notification categories](./Notification-IO's-&-Categories.md#categories) to change this behaviour.
These categories are pretty much just pre-defined [NotifyIO settings](./Notification-IO's-&-Categories.md#available-notifyios).    
Every notification in *en-US.yml* has a unique category with a reserved name assigned to it.

For example, If you would like to have the "language_changed" notification displayed as an actionbar message, you define the following:

```YAML
notifications:        #General header for all notification settings
  language_changed:   #Name of the category, same as in messages.yml
    io: actionbar     #Setting the Notify IO to "actionbar"
``` 

!!! warning
    **A note about the `notifications` section: BetonQuest searches through all packages and just uses the first one it finds.
    Therefore, you should probably create just one `notifications` section. We will improve this in BQ 2.0.**


You can add any other [Notify IO setting](./Notification-IO's-&-Categories.md#available-notifyios) to the category like so:
```YAML
notifications:       
  language_changed:   
    io: actionbar     
    sound: entity.blaze.hurt  #Plays a sound while showing the notification

  changelog: #This is another category. They all need to be inside the 'notifications:' section.
    sound: entity.experience_orb.pickup      
```
A full list of all reserved names can be found on the [IO's & Categories](./Notification-IO's-&-Categories.md#built-in-categories) page.

This feature can also be used to disable built-in notifications:    
Just set `io:` to [`suppress`](./Notification-IO's-&-Categories.md#suppress) for any notification that you want to remove.


###Objective notifications
Some objectives have a `notify` argument that can be added to their instruction.
If you do so, the objective will send a notification to the player if they progress in the objective.
You can also add an interval (`notify:5`) - in this case the player will get a notification every 5 steps
towards the completion of the objective.

The values for these notifications look a bit strange:
```YAML
blocks_to_break: '@[legacy]&2{amount} blocks left to break'
```
`@[legacy]` is a [text formatter](/../Documentation/Features/Text-Formatting/).  
`{amount}` is just an internal variable (similar to the color codes) that will be replaced with a number based on the
player's progression.

You can customize how these notifications are displayed using exactly the same method as for other built-in notifications.
