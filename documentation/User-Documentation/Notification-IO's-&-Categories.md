## Notify IO's

A NotifyIO is a method of displaying a notification to the player. Here's a demo video showing an example configuration
of all NotifyIO's.

<div style="text-align: center">
 <video controls loop
     src="../../media/content/User-Documentation/Notifications/NotifySystemOverview.mp4"
     width="780" height="500">
 Sorry, your browser doesn't support embedded videos.
 </video>
</div>

Most NotifyIO's have unique settings that somehow change how a notification is displayed.    
**Additionally, they all allow each setting of the SoundIO to be used!**
This is the case because every NotifyIO has an internal SoundIO. 
Therefore, you can play a sound whenever a notification is shown.

The actual message is either defined in the event that triggers the NotifyIO or
in the *messages.yml* for all built-in notifications. 

### ChatIO
Writes the notification in the players chat.

??? info "Preview"
    ![chat image](../media/content/User-Documentation/Notifications/chat.png)

| Option  | Description                 |
|---------|-----------------------------|
| [SoundIO](#soundio) | Any option from the [SoundIO](#soundio). |

### AdvancementIO
Shows the notification using an achievement popup. Unfortunately Minecraft does play the default advancement sound here. 
It's not possible to stop this sound from playing - if you want to get rid of it you would have to override / remove
that sound from your server's ressource pack.
You can still add your own additional sound as usual though.
It will then be played together with the default advancement sound.

??? info "Preview"
    ![advancement image](../media/content/User-Documentation/Notifications/advancement.png)

| Option | Description |
|--------|-------------|
| frame | What Achievement frame to use. Can be: `challenge`, `goal`, `task` |
| icon | What icon to show. Must be the vanilla name of an item. Example: minecraft:map |
| [SoundIO](#soundio) | Any option from the [SoundIO](#soundio). |

### ActionbarIO
Shows the notification using the actionbar.

??? info "Preview"
    ![actionbar image](../media/content/User-Documentation/Notifications/actionbar.png)

| Option | Description |
|--------|-------------|
| [SoundIO](#soundio) | Any option from the [SoundIO](#soundio). |

### BossbarIO
Shows the notification using a bossbar at the top of the players screen.

??? info "Preview"
    <div style="text-align: center">
    ![bossbar image](../media/content/User-Documentation/Notifications/bossbar.png)
    </div>
| Option | Description |
|--------|-------------|
| barFlags | What <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarFlag.html" target="_blank">flags</a> to add to the bossbar. `PLAY_BOSS_MUSIC` seems to be broken in either Spigot or the game itself.
| barColor | What <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html" target="_blank">color</a> to draw the bar. |
| progress | What progress to show the bar. A floating point number between 0.0 (empty) and 1.0 (full) |
| style | What bar <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarStyle.html" target="_blank">style</a> to use. |
| stay | How many ticks to keep the bar on screen. Defaults to 70 |
| countdown | If set, will step the progress of the bar by countdown steps. For example, if set to 10, then 10 times during the time it is on the screen the progress will drop by 1/10 |
| [SoundIO](#soundio) | Any option from the [SoundIO](#soundio). |

### TitleIO
Shows the notification using a title. A subtitle can be played simultaneously by adding `\n` to the notification text.
Anything after these characters will be shown in the subtitle.

??? info "Preview"     
    ![title](../media/content/User-Documentation/Notifications/title.png)

| Option | Description |
|--------|-------------|
| fadeIn | Ticks to fade the title in. Default 10 |
| stay | Ticks to keep title on screen. Default 70 |
| fadeOut | Ticks to fade the title out. Default 20 |
| subTitle | Optional subtitle to show. All _'s are replaced with spaces |
| [SoundIO](#soundio) | Any option from the [SoundIO](#soundio). |

### SubTitleIO
Shows the notification using a subtitle.

??? info "Preview"
    ![subtitle](../media/content/User-Documentation/Notifications/subtitle.png)

| Option | Description |
|--------|-------------|
| fadeIn | Ticks to fade the title in. Default 10 |
| stay | Ticks to keep title on screen. Default 70 |
| fadeOut | Ticks to fade the title out. Default 20 |
| [SoundIO](#soundio) | Any option from the [SoundIO](#soundio). |

### TotemIO
Shows a totem with a "customModelData" NBT tag. This allows you to replace the totem with a custom texture or model 
during the animation.

??? info "Preview"
    <div style="text-align: center">
        <video controls loop
        src="../../media/content/User-Documentation/Notifications/TotemIO.mp4"
        width="780" height="500">
        Sorry, your browser doesn't support embedded videos.
    </video>
    </div>

| Option | Description |
|--------|-------------|
| custommodeldata | This CustomModelData will be used. |
| [SoundIO](#soundio) | Any option from the [SoundIO](#soundio). |

### SoundIO
This IO just plays a sound. You can use it's options in any other IO.
You should read the <a href="https://minecraft.gamepedia.com/Commands/playsound" target="_blank">wiki page</a> of the playsound command
as Minecraft's sound system is kinda strange. Just one example: Sound never moves in Minecraft. It's totally static.
Keep that in mind when creating sounds close to a player. They can move around the sound and make it louder or quieter by walking towards / away from it.

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either vanilla Minecraft sounds (get them using /playsound autocompletion) or the name of a sound from a resource pack. |
| soundcategory | The <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html" target="_blank">category</a> in which the sound will be played. |
| soundvolume | Minecraft's <a href="https://minecraft.gamepedia.com/Commands/playsound#Arguments" target="_blank">special sound volume</a>. Default: _1_ |
| soundpitch | Pitch of the sound. Default: _1_ Min: _0_ Max: _2_ |
| soundlocation | Default: The player's location. A location using the BetonQuest [ULF](../Reference/#unified-location-formating). Can include variables. |
| soundplayeroffset | This option is special. See below.

<h3>soundplayeroffset:</h3>
This option can be a number or a vector.

**Number**:

The location the sound will be played at is moved away from the player towards the `soundlocation` using the value of
`soundplayeroffset`.
The sound will be at the actual location if the player is closer to the soundlocation
then the `soundplayeroffset` would allow.

??? info "Visual Explanation"  
    <div style="text-align: center">
    ![offset image](../media/content/User-Documentation/Notifications/offset.png)
    </div>

    This shows how the sound will be played at the `soundlocation` if the `soundplayeroffset` is bigger then the current
    distance between the player and the `soundlocation` 
    <div style="text-align: center">
    ![offsetBiggerThanDistance image](../media/content/User-Documentation/Notifications/offsetBiggerThanDistance.png)
    </div>

*Example usage*:

You could make a "sound compass" that will play a sound in the direction of a point of interest.


**Vector**:     
A vector has to be in the format`(x;y;z)`. This system will use the players relative coordinate system.
This means that the vectors x axis is right / left from the players head, the y axis is up or down from where ever the players face is
and the z axis is before / behind the players face. It will move along the players  head.

??? info "Visual Explanation"
    In contrast to their global counterparts, relative x,y,z axes do not change their orientation relative to the player.
    Example: The positive x-axis will always point left from the perspective of the player.
    <div style="text-align: center">
    ![relativeAxis image](../media/content/User-Documentation/Notifications/relativeAxis.png)
    </div>    

This makes it possible to go crazy with sounds. Just one example: A halloween special
where the player hears a :ghost: whispering into his left ear - no matter where he is or how he turns his head... 🎃

Here is a small example:
??? info "Video Example"
    blue line = direction the player is looking in    
    🟢 = soundlocation argument    
    🔴  = the actual location the sound is played at    
    *soundplayeroffset = (0,0,5)*

    <div style="text-align: center">
    <video controls loop
    src="../../media/content/User-Documentation/Notifications/RelativeVectorExample.mp4"
    width="780" height="500">
    Sorry, your browser doesn't support embedded videos.
    </video>
    </div>
    The sound is always played 5 block away from the soundlocation. The direction is however based on where the player is looking.

### SuppressIO
Does not output any sound or text 🔕. Can be used to remove built-in notifications.


## Categories

Notify Categories are pre-defined [NotifyIO settings](#notify-ios). They can be applied to any notify event and are used
by BetonQuests built-in notifications.
All categories must be defined in the *custom.yml* file in a section called `notifications`.

!!! warning
    **A note about the custom.yml: This is a strange file. BetonQuest searches through all packages and just uses the first one it finds.
    Therefore, you should probably create just one custom.yml with all your settings. We will improve this in BQ 2.0.**

### Custom Categories

Custom categories are user defined presets for any notify event. They shorten your events and enable you to change
how a notification of a certain category looks in one central place. They do not allow you to set a message though as 
the message is an argument of the notify event! 

This is how a custom category looks:
```YAML
notifications:
  money:            # Category name
   io: advancement  # Set's the used NotifyIO
   icon: gold_ingot # A setting of the bossbarIO
```

The only thing you must be careful with is the name of your custom categories. You could end up using a reserved name
- these stem from BetonQuest's build-in notification categories. Changing these is a [different feature](#built-in-categories).
A full list of all reserved names can be found below.

### Built-in Categories
The table below contains all build-in notification categories.

You may notice that the "Categories" column lists two categories.
These work exactly like the one in the `notify` event. The first existent category (from left to right) will be used.
This allows you to change all build-in notifications with just two entries in your *custom.yml*:
```YAML
notifications:
  info:
    io: actionbar
  error:
    io: actionbar
``` 
You can override the settings from the info/error category for any specific notification by adding it to the 
`notifications` section. Example:
```YAML
notifications:
  info:
    io: actionbar
  error:
    io: actionbar
  new_journal_entry:  # The info categories settings are overridden for the new_journal_entry notification
    io: subtitle
```


| Notifications       | Categories                |  | Notifications     | Categories               |
|---------------------|---------------------------|  |-------------------|--------------------------|
| Command Blocked     | command_blocked, *error*  |  | Animals to Breed  | animals_to_breed, *info* |
| No Permission       | no_permission, *error*    |  | Blocks to Break   | blocks_to_break, *info*  |
| New Changelog       | changelog, *info*         |  | Blocks to Place   | blocks_to_place, *info*  |
| Inventory Full      | inventory_full, *error*   |  | Mobs to click     | mobs_to_click, *info*    |
| Language Changed    | language_changed, *info*  |  | Fish to catch     | fish_to_catch, *info*    |
| Mobs to Kill        | mobs_to_kill, *info*      |  | Players to kill   | players_to_kill, *info*  |
| Money Given         | money_given, *info*       |  | Potions to brew   | potions_to_brew, *info*  |
| Money Taken         | money_taken, *info*       |  | Points given      | point_given, *info*      |
| Quest Cancelled     | quest_cancelled, *info*   |  | Points taken      | point_taken, *info*      |
| Items Given         | items_given, *info*       |  | Points multiplied | point_multiplied, *info* |
| New Journal Entry   | new_journal_entry, *info* |  | Sheep to shear    | sheep_to_shear, *info*   |
| Conversation blocked| busy, *error*             |  |                   |                          |
