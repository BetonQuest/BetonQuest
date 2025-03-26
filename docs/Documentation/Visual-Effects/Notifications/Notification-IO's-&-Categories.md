---
icon: material/party-popper
---

A NotifyIO is a method of displaying a notification to the player. Here's a demo video showing an example configuration
of all NotifyIO's:

<video controls loop src="../../../../_media/content/Documentation/Notifications/NotifySystemOverview.mp4" width="100%">
  Sorry, your browser doesn't support embedded videos.
</video>

Most NotifyIO's have unique settings that somehow change how a notification is displayed.    
**Additionally, they all allow any setting from the SoundIO to be used!**
This is because every NotifyIO has an internal SoundIO. 
Therefore, you can play a sound whenever a notification is shown.

The actual message is either defined in the event that triggers the NotifyIO or
in the *messages.yml* for all built-in notifications. 

@snippet:events:notify@

## Available NotifyIOs

There are a bunch of notify IOs available. They all have their own settings and are listed below.


### Chat
Writes the notification in the player's chat.

??? info "Preview"
    ![chat image](../../../_media/content/Documentation/Notifications/chat.png)

| Option          | Description                            |
|-----------------|----------------------------------------|
| [Sound](#sound) | Any option from the [SoundIO](#sound). |

### Advancement
Shows the notification using an achievement popup.
Unfortunately, Minecraft will play the default advancement sound here. 
It's not possible to stop this sound from playing—if you want to get rid of it, you would have to override / remove
that sound from your server's resource pack.
You can still add your own additional sound as usual though.
It will then be played together with the default advancement sound.

??? info "Preview"
    ![advancement image](../../../_media/content/Documentation/Notifications/advancement.png)

| Option          | Description                                                                    |
|-----------------|--------------------------------------------------------------------------------|
| frame           | What Achievement frame to use. Can be: `challenge`, `goal`, `task`             |
| icon            | What icon to show. Must be the vanilla name of an item. Example: minecraft:map |
| [Sound](#sound) | Any option from the [SoundIO](#sound).                                         |

### Actionbar
Shows the notification using the actionbar.

??? info "Preview"
    ![actionbar image](../../../_media/content/Documentation/Notifications/actionbar.png)

| Option          | Description                            |
|-----------------|----------------------------------------|
| [Sound](#sound) | Any option from the [SoundIO](#sound). |

### Bossbar
Shows the notification using a bossbar at the top of the players screen.

??? info "Preview"
    <div style="text-align: center">
    ![bossbar image](../../../_media/content/Documentation/Notifications/bossbar.png)
    </div>
| Option | Description |
|--------|-------------|
| barFlags | What [flags](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarFlag.html) to add to the bossbar. `PLAY_BOSS_MUSIC` seems to be broken in either server or the game itself.
| barColor | What [color](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html) to draw the bar. |
| progress | What progress to show in the bar. A floating point number between 0.0 (empty) and 1.0 (full). Supports variables. |
| style | What bar [style](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarStyle.html) to use. |
| stay | How many ticks to keep the bar on screen. Defaults to 70. Supports variables. |
| countdown | Animates the progress of the bar if set. The value determines how often the bar is updated. Formula: $TimeBetweenUpdates = \frac{stay}{countdown}$ |
| [Sound](#sound) | Any option from the [SoundIO](#sound). |

### Title
Shows the notification using a title.
A subtitle can be played simultaneously by adding `\n` to the notification text.
Anything after these characters will be shown in the subtitle.

??? info "Preview"     
    ![title](../../../_media/content/Documentation/Notifications/title.png)

| Option          | Description                               |
|-----------------|-------------------------------------------|
| fadeIn          | Ticks to fade the title in. Default 10    |
| stay            | Ticks to keep title on screen. Default 70 |
| fadeOut         | Ticks to fade the title out. Default 20   |
| [Sound](#sound) | Any option from the [SoundIO](#sound).    |

### SubTitle
Shows the notification using a subtitle.

??? info "Preview"
    ![subtitle](../../../_media/content/Documentation/Notifications/subtitle.png)

| Option          | Description                               |
|-----------------|-------------------------------------------|
| fadeIn          | Ticks to fade the title in. Default 10    |
| stay            | Ticks to keep title on screen. Default 70 |
| fadeOut         | Ticks to fade the title out. Default 20   |
| [Sound](#sound) | Any option from the [SoundIO](#sound).    |

### Totem
Shows a totem with a "customModelData" NBT tag. This allows you to replace the totem with a custom texture or model 
during the animation.

??? info "Preview"
    <video controls loop src="../../../../_media/content/Documentation/Notifications/TotemIO.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>

| Option          | Description                            |
|-----------------|----------------------------------------|
| customisation   | This CustomModelData will be used.     |
| [Sound](#sound) | Any option from the [SoundIO](#sound). |

### Sound
This IO just plays a sound. You can use its options in any other IO.
You should read the [wiki page](https://minecraft.wiki/w/Commands/playsound) of the playsound command
as Minecraft's sound system is kinda strange. Just one example: Sound never moves in Minecraft. It's totally static.
Keep that in mind when creating sounds close to a player. They can move around the sound and make it louder or quieter by walking towards / away from it.

| Option            | Description                                                                                                                                                |
|-------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| sound             | Sound to play. If blank, no sound. Either vanilla Minecraft sounds (get them using /playsound autocompletion) or the name of a sound from a resource pack. |
| soundcategory     | The [category](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html) in which the sound will be played.                                  |
| soundvolume       | Minecraft's [special sound volume](https://minecraft.wiki/w/Commands/playsound#Arguments). Default: _1_                                                    |
| soundpitch        | Pitch of the sound. Default: _1_ Min: _0_ Max: _2_                                                                                                         |
| soundlocation     | Default: The player's location. A location using the BetonQuest [ULF](../../Scripting/Data-Formats.md#unified-location-formating). Can include variables.  |
| soundplayeroffset | This option is special. See below.                                                                                                                         |

<h3>soundplayeroffset:</h3>
This option can be a number or a vector.

**Number**:

The location the sound will be played at is moved away from the player towards the `soundlocation` using the value of
`soundplayeroffset`.
The sound will be at the actual location if the player is closer to the soundlocation
then the `soundplayeroffset` would allow.

??? info "Visual Explanation"  
    <div style="text-align: center">
    ![offset image](../../../_media/content/Documentation/Notifications/offset.png)
    </div>

    This shows how the sound will be played at the `soundlocation` if the `soundplayeroffset` is bigger then the current
    distance between the player and the `soundlocation` 
    <div style="text-align: center">
    ![offsetBiggerThanDistance image](../../../_media/content/Documentation/Notifications/offsetBiggerThanDistance.png)
    </div>

*Example usage*:

You could make a "sound compass" that will play a sound in the direction of a point of interest.

**Vector**:     
A vector has to be in the format`(x;y;z)`. This system will use the players relative coordinate system.
This means that the vectors x axis is right / left from the players head, the y axis is up or down from where ever the players face is
and the z axis is before / behind the players face. It will move along the players  head.

@snippet:general:relativeAxisExplanation@

This makes it possible to go crazy with sounds. Just one example: A halloween special
where the player hears a :ghost: whispering into his left ear—no matter where he is or how he turns his head... 🎃

Here is a small example:
??? info "Video Example"
    blue line = direction the player is looking in    
    🟢 = soundlocation argument    
    🔴  = the actual location the sound is played at    
    *soundplayeroffset = (0,0,5)*

    <video controls loop src="../../../../_media/content/Documentation/Notifications/RelativeVectorExample.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    The sound is always played 5 block away from the soundlocation. The direction is however based on where the player is looking.

### Suppress
Does not output any sound or text 🔕. Can be used to remove built-in notifications.


## Categories

Notify Categories are pre-defined [NotifyIO settings](#categories). They can be applied to any notify event and are used
by BetonQuest's built-in notifications.
All categories must be defined in a section called `notifications`.

!!! warning
    **A note about the `notifications` section: BetonQuest searches through all packages and just uses the first one it finds.
    Therefore, you should probably create just one `notifications` section. We will improve this in BQ 2.0.**

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
This allows you to change all build-in notifications with just two entries in your *notifications* section:
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
<div class="grid" markdown>

| Notifications           | Categories                                       | 
|-------------------------|--------------------------------------------------| 
| Command Blocked         | command_blocked, *error*                         | 
| No Permission           | no_permission, *error*                           | 
| Inventory Full Backpack | inventory_full_backpack, inventory_full, *error* | 
| Inventory Full Drop     | inventory_full_drop, inventory_full, *error*     | 
| Language Changed        | language_changed, *info*                         | 
| Money Given             | money_given, *info*                              | 
| Money Taken             | money_taken, *info*                              | 
| Quest Cancelled         | quest_cancelled, *info*                          | 
| Items Given             | items_given, *info*                              | 
| New Journal Entry       | new_journal_entry, *info*                        | 
| Conversation blocked    | busy, *error*                                    | 


| Notifications     | Categories               |
|-------------------|--------------------------|
| Animals to Breed  | animals_to_breed, *info* |
| Blocks to Break   | blocks_to_break, *info*  |
| Blocks to Place   | blocks_to_place, *info*  |
| Mobs to click     | mobs_to_click, *info*    |
| Mobs to Kill      | mobs_to_kill, *info*     |
| Fish to catch     | fish_to_catch, *info*    |
| Players to kill   | players_to_kill, *info*  |
| Potions to brew   | potions_to_brew, *info*  |
| Points given      | point_given, *info*      |
| Points taken      | point_taken, *info*      |
| Points multiplied | point_multiplied, *info* |
| Sheep to shear    | sheep_to_shear, *info*   |

</div>
