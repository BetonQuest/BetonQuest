---
icon: material/party-popper
---

A NotifyIO is a method of displaying notifications to the player. Here's a demo video showing an example configuration
of all NotifyIO's:

<video controls loop src="../../../../_media/content/Documentation/Notifications/NotifySystemOverview.mp4" width="100%">
  Sorry, your browser doesn't support embedded videos.
</video>


!!! tip annotate inline end "Sounds in Notifications"
    All NotifyIO's support any setting from the [`SoundIO`](#sound) type.
    Therefore, you can play a sound whenever a notification is shown. (1)
    
 1. In other words, every NotifyIO also supports every option from `SoundIO`. 


Most NotifyIO's have unique settings that somehow change how a notification is displayed. The actual message is 
either defined in the event that triggers the NotifyIO or the appropriate language file 
in the *lang* directory for all built-in notifications. 

@snippet:events:notify@

## Available NotifyIOs

There are a bunch of notify IOs available. Below is a list of all available notifyIOs and their possible options.
??? info "Notify Syntax"
    ```YAML
    events:
      notifyExample: notify <message> io:<NotifyIO_Type> <option_1>:<option_1_value> <option_2>:<option_2_value> 
        <category>:<category_Name>
    ```


### Chat
Writes the notification in the player's chat.

??? info "Preview"
    ![chat image](../../../_media/content/Documentation/Notifications/chat.png)

| Option          | Description                            |
|-----------------|----------------------------------------|
| [Sound](#sound) | Any option from the [SoundIO](#sound). |

### Advancement
Shows the notification using an achievement popup.
!!! danger inline end
    Minecraft will always play the default advancement sound for this NotifyIO type. 
    It's not possible to stop this sound from playing. 
    ??? warning "Workarounds"
        To stop this behaviour, you would have to override / remove
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

| Option          | Description                                                                                                                                                                          |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| barFlags        | What [flags](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarFlag.html) to add to the bossbar. `PLAY_BOSS_MUSIC` seems to be broken in either server or the game itself. |
| barColor        | What [color](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html) to draw the bar.                                                                                |
| progress        | What progress to show in the bar. A floating point number between 0.0 (empty) and 1.0 (full).                                                                                        |
| style           | What bar [style](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarStyle.html) to use.                                                                                     |
| stay            | How many ticks to keep the bar on screen. Defaults to 70.                                                                                                                            |
| countdown       | Animates the progress of the bar if set. The value determines how often the bar is updated. Formula: $TimeBetweenUpdates = \frac{stay}{countdown}$                                   |
| [Sound](#sound) | Any option from the [SoundIO](#sound).                                                                                                                                               |

### Title
Shows the notification using a title.
A subtitle can be sent simultaneously by adding `\n` to the notification text.
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

| Option          | Description                                                     |
|-----------------|-----------------------------------------------------------------|
| customModelData | The CustomModelData to use. Prefere to use `itemModel` instead. |
| itemModel       | @snippet:versions:mc-1.21.4@ The ItemModel to use               |
| [Sound](#sound) | Any option from the [SoundIO](#sound).                          |

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
| soundlocation     | Default: The player's location. A location using the BetonQuest [ULF](../../Scripting/Data-Formats.md#unified-location-formating).                         |
| soundplayeroffset | This option is special. See below.                                                                                                                         |

<h4> soundplayeroffset </h4>
`soundplayeroffset` is an option to move the location of the sound based off the player's location as well 
as the `soundlocation` option. This option can be a number or a vector. (1)
{ .annotate }

1. For more information about how BetonQuest processes location data, see [Unified location formatting](../../Scripting/Data-Formats.md).

=== "Number" 
    !!! tip inline end "Example Usage"
        You could make a "sound compass" that will play a sound in the direction of a point of interest.
    
    This is only useful if you set the `soundlocation` option to a location that isn't the player's. 
    Using a number will move the "source" of the sound so that it "points" towards the `soundlocation` 
    option relative to the player's current location using the value that you set as distance increments.
    The sound will be at the actual location if the player is closer to the `soundlocation`
    then the `soundplayeroffset` would allow. (1)
    { .annotate }
    
    1.  It can help to imagine it as a line of rope with knots spaced out equally stretched between the player's location 
    and the `soundlocation`. The `soundplayeroffset` value determines the distance between the knots and will play 
    sounds at those knots.  
          
        
    ??? info "Visual Explanation"  
        <div style="text-align: center">
        ![offset image](../../../_media/content/Documentation/Notifications/offset.png)
        </div>
    
        This shows how the sound will be played at the `soundlocation` if the `soundplayeroffset` is bigger then the current
        distance between the player and the `soundlocation` 
        <div style="text-align: center">
        ![offsetBiggerThanDistance image](../../../_media/content/Documentation/Notifications/offsetBiggerThanDistance.png)
        </div>



=== "Vector"

    !!! tip inline end "Example Usage"
        A Halloween event where the player hears a :ghost: whispering into his left ear, no matter where he is or how 
        he turns his head... 🎃
    A vector has to be in the format `(x;y;z)`. This system will use the player's relative coordinate system.
    This means that the vectors' x-axis is left/right from the players head(1), the y-axis is up/down from the player's 
    head location(2) and the z-axis is in-front/behind the player's head; it will move along the player's
    head(3).
    { .annotate}

    1. Positive numbers are to the left of the player, negative numbers to the right. 
    2. Positive numbers are above the player's head, negative numbers are below.
    3. Positive numbers are in-front of the player, negative numbers are behind.
    @snippet:general:relativeAxisExplanation@


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

Notify Categories are pre-defined [NotifyIO settings](#categories). They can be applied to any notify event and are 
also used by BetonQuest's built-in notifications.
All categories must be defined in a section called `notifications`.

!!! warning
    **A note about the `notifications` section: BetonQuest searches through all packages and just uses the first one it finds.
    Therefore, you should probably create just one `notifications` section. We will improve this in BQ 2.0.**

### Custom Categories
!!! tip inline end 
    Categories are very useful for notifications that you are going to 
    be sending players multiple times and want to create a unified, consistent look and sound.
Custom categories are user-created presets for any notify event. They shorten your events and enable you to change
how a notification of a certain category looks in one central place. They do not allow you to set a message though as 
the message is an argument of the `notify` event!

```YAML title="Custom Categories Example Configuration"
notifications:
  money: #(1)!
   io: advancement #(2)!
   icon: gold_ingot #(3)!
   sound: entity.item.pickup #(4)!
```

1. ID of the category.
2. Sets the NotifyIO type to be used.
3. An option of the advancementIO.
4. An option of the soundIO.
Now, any `notify` event with `money` as its category will use the `advancement` io, with a `gold_ingot` for its 
icon and play the `entity.item.pickup` sound without you having to define all the options multiple times.    

!!! warning 
    The only thing you must be careful with is the name of your custom categories. You could end up using a 
    reserved name
    - these stem from BetonQuest's built-in notification categories. Changing these are a [different feature](#built-in-categories).
    A full list of all reserved names can be found below.

### Built-in Categories
!!!tip inline end "Default Categories"
    By default, only 2 built-in categories exist: `error`/`info`.
The table below contains all built-in notification categories.

You may notice that the "Categories" column lists two categories: One that matches the name of the `notification message`
and `error`/`info`.


These work exactly like the user-made categories in the `notify` event. The first existent category (from left to right) will be used.
This allows you to change all built-in notifications with just two entries in your *notifications* section:
```YAML
notifications:
  info:
    io: chat #(1)!
  error:
    io: actionbar #(2)!
``` 

1. Wouldn't actually change anything as `chat` is the default IO type.
2. Any message using the `error` category will now be displayed through the actionbar rather than chat.

You can override the settings from the `info`/`error` category for any specific notification by adding it to the 
`notifications` section.
When you create a category with a name that matches a `notification message` name, BetonQuest then defaults to that option
over `error`/`info`

```YAML title="Example"
notifications:
  info:
    io: actionbar
  error:
    io: actionbar
  new_journal_entry:  
    io: subtitle #(1)!
```

1. Since `new_journal_entry` notification is now defined, the `info` category settings are overridden because the first
   existent category (from left to right) will be used.
<div class="grid" markdown>

| Notifications           | Categories                                       | 
|-------------------------|--------------------------------------------------| 
| Command Blocked         | command_blocked, *error*                         | 
| No Permission           | no_permission, *error*                           | 
| Inventory Full Backpack | inventory_full_backpack, inventory_full, *error* | 
| Inventory Full Drop     | inventory_full_drop, inventory_full, *error*     | 
| Language Changed        | language_changed, *info*                         | 
| Quest Cancelled         | quest_cancelled, *info*                          | 
| New Journal Entry       | new_journal_entry, *info*                        | 
| Conversation start      | conversation_start, *info*                       |
| Conversation end        | conversation_end, *info*                         |
| Conversation blocked    | busy, *error*                                    | 
| Money Given             | money_given, *info*                              | 
| Money Taken             | money_taken, *info*                              | 
| Items given             | items_given, *info*                              | 
| Items taken             | items_taken, *info*                              |
| Points given            | point_given, *info*                              |
| Points taken            | point_taken, *info*                              |
| Points set              | point_set, *info*                                |


| Notifications      | Categories                 |
|--------------------|----------------------------|
| Points multiplied  | point_multiplied, *info*   |
| Animals to Breed   | animals_to_breed, *info*   |
| Blocks to Break    | blocks_to_break, *info*    |
| Blocks to Place    | blocks_to_place, *info*    |
| Mobs to click      | mobs_to_click, *info*      |
| Mobs to Kill       | mobs_to_kill, *info*       |
| Fish to catch      | fish_to_catch, *info*      |
| Players to kill    | players_to_kill, *info*    |
| Potions to brew    | potions_to_brew, *info*    |
| Sheep to shear     | sheep_to_shear, *info*     |
| Times to jump      | times_to_jump, *info*      |
| Animals to bread   | animals_to_tame, *info*    |
| Payment to receive | payment_to_receive, *info* |
| Levels to gain     | level_to_gain, *info*      |
| Items to enchant   | items_to_enchant, *info*   |
| Items to craft     | items_to_craft, *info*     |
| Items to smelt     | items_to_smelt, *info*     |
| Items to pickup    | items_to_pickup, *info*    |

</div>
