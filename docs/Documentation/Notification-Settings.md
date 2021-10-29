BetonQuest features a powerful notify system that allows you to display any information to your players.
You can freely choose between many NotifyIO's like simple chat output, (sub)titles, advancements or sounds. They all come
with unique options that allow you to customize them. Just take a look at this example configuration:

<div style="text-align: center">
 <video controls loop
     src="../../_media/content/Documentation/Notifications/NotifySystemOverview.mp4"
     width="780" height="500">
 Sorry, your browser doesn't support embedded videos.
 </video>
</div>


## Sending custom notifications

A truly custom notification can be sent using the [`notify`](Events-List.md#notification-notify) event at any time.
Check out the [events documentation](Events-List.md#notification-notify) to learn how.

## Changing BetonQuest's built-in notifications
  
### General notifications
These can be anything from BetonQuest notifying a player that their language has been changed
to sending a notice about a new changelog to an admin.

For example:    
When BetonQuest fails to add a quest item to a player's inventory it will send `&e*&bYour inventory is full!&e*`.    
This message is defined in *messages.yml* along with other default plugin messages. You can redefine them to your liking.

The Notify System can do much more than just changing messages though:

All notifications will be displayed using the ChatIO and without a sound by default. You need to use
[notification categories](Notification-IO's-&-Categories.md#categories) to change this behaviour.
These categories are pretty much just pre-defined [NotifyIO settings](Notification-IO's-&-Categories.md#notify-ios).    
Each notification in *messages.yml* has a special category with a reserved name assigned to it.

For example: If you would like to have the "language_changed" notification displayed as an actionbar message you would:

  * Add a file named *custom.yml* to your package.
  * Add this to the file: 

```YAML
notifications:        #General header for all notification settings
  language_changed:   #Name of the category, same as in messages.yml
    io: actionbar     #Setting the Notify IO to "actionbar"
``` 

!!! warning
    **A note about the custom.yml: This is a strange file. BetonQuest searches through all packages and just uses the first one it finds.
    Therefore, you should probably create just one custom.yml with all your settings. We will improve this in BQ 2.0.**


You can add any other [Notify IO setting](Notification-IO's-&-Categories.md#notify-ios) to the category like so:
```YAML
notifications:       
  language_changed:   
    io: actionbar     
    sound: entity.blaze.hurt  #Plays a sound while showing the notification

  changelog: #This is another category. They all need to be inside the 'notifications:' section.
    sound: entity.experience_orb.pickup      
```
A full list of all reserved names can be found on the [IO's & Categories](Notification-IO's-&-Categories.md#built-in-categories) page.

This feature can be used to disable build-in notifications:    
Just set `io:` to [`supress`](Notification-IO's-&-Categories.md#suppress) for any notification that you want to remove.


###Objective notifications
Some objectives have a `notify` argument that can be added to their instruction.
If you do so, the objective will send a notification to the player if they progress in the objective.
You can also add an intervall (`notify:5`) - in this case the player will get a notification every 5 steps
towards the completion of the objective.

The messages.yml values of these notifications look a bit strange:
```YAML
blocks_to_break: '&2{1} blocks left to break'
```
`{1}` is just an internal variable (similar to the color codes) that will be replaced with a number based on the
player's progression.

You can customize how these notifications are displayed using exactly the same method as for other built-in notifications.
