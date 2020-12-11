## Notify IO's
### Chat
Writes the notification in the players chat.

??? info "Preview"
    ![chat image](../media/content/User-Documentation/Notifications/chat.png)

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Either <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html" target="_blank"> vanilla Minecraft sounds</a> or the name of a sound from a resource pack. |

### Advancement
Shows the notification using an achievement popup. Unfortunately Minecraft does play the default advancement sound here. It can only be disabled
by removing it from your ressource pack. You can still add your own additional sound to this notification though.

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

The location the sound will be played at is moved away from the player towards the `soundlocation` using the number from
`soundplayeroffset`.
The sound will be at the actual location if the player is closer to the soundlocation then the offset would allow.

??? info "Visual Explanation"  
    ![offset image](../media/content/User-Documentation/Notifications/offset.png)

    This shows how the sound will be placed at the `soundlocation` if the distance between the player and the `soundlocation`
    is smaller then the `playeroffset`:    
    ![offsetBiggerThanDistance image](../media/content/User-Documentation/Notifications/offsetBiggerThanDistance.png)

*Example usage*:

You could make a "sound compass" that will play a sound in the direction of a point of interest.


**Vector**:     
A vector has to be in the format`(x;y;z)`. This system will use the players relative coordinate system.
This means that the vectors x axis is right / left from the players head, the y axis is up or down from where ever the players face is
and the z axis is before / behind the players face. It will move along the players  head.

??? info "Visual Explanation"  
    ![relativeAxis image](../media/content/User-Documentation/Notifications/relativeAxis.png)

This makes it possible to go crazy with sounds. Just one example: A halloween special
where the player hears a :ghost: whispering into his left ear - no matter where he is or how he turns his head... 🎃

Here is a small example:

blue line = direction the player is looking in    
🟢 = soundlocation argument    
🔴  = the actual location the sound is played at    
*soundplayeroffset = (0,0,5)*

<video controls loop
src="../../media/content/User-Documentation/Notifications/RelativeVectorExample.mp4"
width="780" height="500">
Sorry, your browser doesn't support embedded videos.
</video>



The sound is always played 5 block away from the soundlocation. The direction is however based on where the player is looking.

### Suppress
Does not output any sound or text 🔕. Can be used to remove "miscellaneous" notifications.


###Categories
These are all notification categories. The categories `error` and `info` are super-categories that allow you to change
the default values for all notification with just two entries in your *custom.yml*. You can still override these settings by providing a specific
setting for any normal category.

This means you can show all notifications using the actionbar while having a bossbar
IO for the "language changed" category with just three entries in you custom.yml!
<div style="text-align: center">

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

</div>
