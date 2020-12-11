BetonQuest features a powerful notify system that allows you to display any information to your players.
You can freely choose between many options like simple chat output, (sub)titles, advancements or sounds. 
Here is an example video:
 <video controls loop
     src="../../media/content/User-Documentation/Notifications/NotifySystemOverview.mp4"
     width="780" height="500">
 Sorry, your browser doesn't support embedded videos.
 </video>

**These "styles" are called "Notify IO's".**

The Notify system gets used by BetonQuest itself too. Therefore, you cannot only send custom messages but also
configure each and every message the plugin sends to your liking.

## Adding custom Notifications using events

A truly custom notification can be sent using the `notify` event. You can either directly define all Notify IO options like so:
```YAML
myEvent: "notify This is a custom message. io:bossbar barColor:red sound:block.anvil.use"
```

Or you can use categories from the *custom.yml* like so:
```YAML
myEvent: "notify This is a custom message! category:info"
```

You can also override category settings:
```YAML
myEvent: "notify Another messsage! category:info io:advancement frame:challenge"
```

These custom notifications also allow custom categories. You can go wild here:
```YAML
notifications:
  money: 
    io: bossbar
    icon: gold_ingot  
```

!!! warning
All colons (`:`) in your notification messages need to be escaped with one backslash (`\`) when using single quotes
(`''`) and with two backslashes (`\\`) when using double quotes (`""`). Example:  `'Peter:Heya %player%!' -> 'Peter{++\++}:Heya %player%!'` `"Peter:Heya %player%!" -> "Peter{++\\++}:Heya %player%!"`

## Configuring default plugin messages
  
### Misc Notifications
Miscellaneous Notifications can be anything from BetonQuest notifying a player that their language has been changed
to sending a notice about a new changelog to any admins.

For example: 
When BetonQuest fails to add a quest item to a player's inventory it will send `&e*&bYour inventory is full!&e*`.
This message is defined in *messages.yml* along with other default plugin messages. You can redefine it to your liking.

They are all activated by default - you have to explicitly disable them using the `supress` notify IO.
This is the key difference to the Objective Progress Notifications as these are off by default. More about them later on.

There is more to it then just the text though. BetonQuest supports quite a lot of ways to send notifications as seen in the video.
All of these "miscellaneous" notifications have a **category** assigned to them. All categories will be displayed in chat and without a sound 
by default. You can override this setting in the *custom.yml* file.

For example: If you would like to have the "language_changed" notification displayed as an actionbar message you would:

  * Add a file named *custom.yml* to your package.
  * Add this to the file: 
  
```YAML
notifications:        #General header for all notification settings
  language_changed:   #Name of the category, same as in messages.yml
    io: actionbar     #Setting the Notify IO to "actionbar"
``` 

You can add any other [Notify IO setting]() to the category like so:

```YAML
notifications:       
  language_changed:   
    io: actionbar     
    sound: entity.blaze.hurt  #Plays a sound while showing the notification


  changelog: #This is another category. They all need to be inside the 'notifications:' section.
    sound: entity.experience_orb.pickup      
```
  

**A note about the custom.yml: This is a strange file. BetonQuest searches through all packages and just uses the first one it finds.
Therefore, you should probably create just one custom.yml with all your settings. We will improve this in BQ 2.0.**

A list of all categories can be found below.

###Objective Progress Notifications
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
 
You can customize how these notifications are displayed using exactly the same method as for "miscellaneous" notifications.

Remember the difference between Objective Progress Notifications and "miscellaneous" notifications:
The latter have to explicitly disabled while the former ones have to be enabled per objective using the `notify` keyword in 
the objectives instruction.
