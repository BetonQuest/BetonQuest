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


  changelog: #This is another category. They all need to be inside the 
    sound: entity.experience_orb.pickup      #'notifications:' section.
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


###Categories 
These are all notification categories. The categories `error` and `info` are super-categories that allow you to change
the default values for all notification with just two entries in your *custom.yml*. You can still override these settings by providing a specific
setting for any normal category.

This means you can show all notifications using the actionbar while having a bossbar 
IO for the "language changed" category with just three entries in you custom.yml!

| Notification  | Categories |                 
|---------------|------------|
| Pullback | pullback, *error* |
| Command Blocked | command_blocked, *error* |
| No Permission | no_permission, *error* |
| Busy | busy, *error* |
| Changelog | changelog, *info* |
| Inventory Full | inventory_full, *error* |
| Language Changed | language_changed, *info* |
| Mobs to Kill | mobs_to_kill, *info* |
| Money Given | money_given, *info* |
| Money Taken | money_taken, *info* |
| Quest Cancelled | quest_cancelled, *info* |
| Items Given | items_given, *info* |
| New Journal Entry | new_journal_entry, *info* |
| Items Taken | items_taken, *info* |
| Blocks to Break | blocks_to_break, *info* |
| Blocks to Place | blocks_to_place, *info* |
| Animals to Breed | animals_to_breed, *info* |
| Mobs to click | mobs_to_click, *info* |
| Fish to catch | fish_to_catch, *info* |
| Players to kill | players_to_kill, *info* |
| Potions to brew | potions_to_brew, *info* |
| Points given | point_given, *info* |
| Points taken | point_taken, *info* |
| Points multiplied | point_multiplied, *info* |
| Sheep to shear | sheep_to_shear, *info* |

## Adding custom Notifications using events

A truly custom notification can be sent using the `notify` event. You can either directly define all Notify IO options like so:
```YAML
myEvent: "notify This is a custom message. io:bossbar barColor:red sound:BLOCK_CHEST_CLOSE"
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
   
    

Configuring groups

default groups

misc:
show supress example
show notify option in objective

## Notify IO's
### Chat
Writes the notification in the players chat.

??? info "Preview"
    ![chat image](../media/content/User-Documentation/Notifications/chat.png)

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html" target="_blank"> vanilla Minecraft sounds</a> or the name of a sound from a resource pack. |

### Advancement
Shows the notification using an achievement popup. Unfortunately Minecraft does play a sound here that cannot be disabled 
unless you remove it from your ressource pack. You can still add your own additional sound to this notification though.

??? info "Preview" 
    ![advancement image](../media/content/User-Documentation/Notifications/advancement.png)

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html" target="_blank"> vanilla Minecraft sounds</a> or the name of a sound from a resource pack. |
| frame | What Achievement frame to use. Can be: `challenge`, `goal`, `task` |
| icon | What icon to show. Must be the vanilla name of an item. Example: minecraft:map |

### Actionbar
Shows the notification using the actionbar.

??? info "Preview"
     ![actionbar image](../media/content/User-Documentation/Notifications/actionbar.png)

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html" target="_blank"> vanilla Minecraft sounds</a> or the name of a sound from a resource pack. |

### Bossbar
Shows the notification using a bossbar at the top of the players screen.

??? info "Preview"
     ![bossbar image](../media/content/User-Documentation/Notifications/bossbar.png)

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html" target="_blank"> vanilla Minecraft sounds</a> or the name of a sound from a resource pack. |
| barFlags | What <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarFlag.html" target="_blank">flags</a> to add to the bossbar. `PLAY_BOSS_MUSIC` seems to be broken in either Spigot or the game itself. 
| barColor | What <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html" target="_blank">color</a> to draw the bar. |
| progress | What progress to show the bar. A floating point number between 0.0 (empty) and 1.0 (full) |
| style | What bar <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarStyle.html" target="_blank">style</a> to use. |
| stay | How many ticks to keep the bar on screen. Defaults to 70 |
| countdown | If set, will step the progress of the bar by countdown steps. For example, if set to 10, then 10 times during the time it is on the screen the progress will drop by 1/10 |

### Title
Shows the notification using a title. A subtitle can be played simultaneously by adding `\n` to the notification text. 
Anything after these characters will be shown in the subtitle.

??? info "Preview"     
     ![title](../media/content/User-Documentation/Notifications/title.png)

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html" target="_blank"> vanilla Minecraft sounds</a> or the name of a sound from a resource pack. |
| fadeIn | Ticks to fade the title in. Default 10 |
| stay | Ticks to keep title on screen. Default 70 |
| fadeOut | Ticks to fade the title out. Default 20 |
| subTitle | Optional subtitle to show. All _'s are replaced with spaces |

### SubTitle
Shows the notification using a subtitle.

??? info "Preview"
     ![subtitle](../media/content/User-Documentation/Notifications/subtitle.png)

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html" target="_blank"> vanilla Minecraft sounds</a> or the name of a sound from a resource pack. |
| fadeIn | Ticks to fade the title in. Default 10 |
| stay | Ticks to keep title on screen. Default 70 |
| fadeOut | Ticks to fade the title out. Default 20 |


### Sound
This IO just plays a sound. You should read the <a href="https://minecraft.gamepedia.com/Commands/playsound" target="_blank">wiki page</a> of the playsound command
as Minecraft's sound system is kinda strange. 

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either vanilla Minecraft sounds (get them using /playsound autocompletion) or the name of a sound from a resource pack. |
| soundcategory | The <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html" target="_blank">category</a> in which the sound will be played. |
| soundvolume | Minecraft's <a href="https://minecraft.gamepedia.com/Commands/playsound#Arguments" target="_blank">special sound volume</a>. Default: _1_ |
| soundpitch | Pitch of the sound. Default: _1_ Min: _0_ Max: _2_ |
| soundlocation | Default: The player's location. A location using the BetonQuest [ULF](../Reference/#unified-location-formating). Can include variables. |
| soundplayeroffset | A vector `(x;y;z)`. The location the sound will be played at is %soundplayeroffset% blocks away from the player towards the soundlocation. The sound will be at the actual location if the player is closer to the soundlocation then the offset would allow. If no soundlocation is set the sound will just be offset using Minecraft's coordinate system. Crazy stuff will happen if the soundlocation is already a ULF with a vector and this option is set too. Then the players relative coordinate system will be used which means that the vectors x axis is right / left from the players head, the y axis is up or down from where ever the players face is and the z axis is before / behind the players face.   |

### Suppress
Does not output anything. Can be used to remove "miscellaneous" notifications.
