# Notifications

## Overview
A notification is any message that is sent to the player either by BetonQuest itself, one of its plugins, or through a custom notify event.

You can customize what is sent by editing `messages.yml`. This contains a list of languages and all the notifications used by BetonQuest.

If you want to customize how a notification is sent to the player then you can edit `custom.yml` to define a NotifyIO to use to for the specific notification. For example you can send a pop up achievement when the journal is updated and a bossbar notification when breaking blocks in a block objective.

## What is a NotifyIO
A NotifyIO is some method of sending a notification out. BetonQuest provides some by default and third party plugins may register their own.

The following are provided by default:

  * `suppress` - Does not send anything. Good at turning off some notifications.
  * `chat` - (Default) Sends the notification as a chat message
  * `advancement` - Sends a notification via a popup advancement
  * `actionbar`- Sends a notification via the actionbar
  * `bossbar` - Sends a notification via a bossbar
  * `title` - Sends a notification via a title
  * `subtitle` - Sends a notification via a subtitle

## Default NotifyIO
If not set, the default NotifyIO is `chat`. You can change the default to `actionbar` by setting the following in `config.yml':

```YAML
default_notify_IO: actionbar
```

## Configuring Notifications
When a notification is generated it will usually have one or more categories assigned to it. These categories are searched for, in order, in all `custom.yml` files under the section `notifications`. The first category found will be used to configured the notification. If none were found then a category of `default` will be search for.

A typical custom.yml file may have the following:
```YAML
notifications:
  # A new journal entry has been added
  new_journal_entry:
    io: advancement
    frame: challenge
    icon: map

  # All infomation notifications
  info:
    io: chat
    sound: BLOCK_CHEST_CLOSE
```

When a new journal entry is added, it will send a notification with the following categories:

  * new_journal_entry
  * info

In the above file, it will find `new_journal_entry` first so will ignore `info`. This defines the settings for the notification by using the `advancement` notifyIO with a challenge frame and a map for the icon.

When a new changelog occurs it will send a notification using the following categories:

  * changelog
  * info

In the above file, it will find `info` which defines that it should be sent via the chat with a specific sound played as well.

## Categories
Categories are a way of defining settings for a notification. BetonQuest uses many categories itself but you can define your own custom categories and use them through the `notify` event.

In general a BetonQuest notification will use the same category name for a notification as it uses in `messages.yml` to define the language and text of the notification. It will also use more general categories to allow you to more broadly define settings and only provide specific settings for some notifications. Remember the categories are searched for in order so the first category will be used in preference to those later in the list.

A list of categories used by BetonQuest are as follows:

| Notification  | Categories |
|---------------|------------|
| Pullback | pullback, error |
| Command Blocked | command_blocked,error |
| No Permission | no_permission,error |
| Busy | busy,error |
| Changelog | changelog,info |
| Inventory Full | inventory_full,error |
| Language Changed | language_changed,info |
| Mobs to Kill | mobs_to_kill,info |
| Money Given | money_given,info |
| Money Taken | money_taken,info |
| Quest Cancelled | quest_cancelled,quest_canceled,info |
| Items Given | items_given,info |
| New Journal Entry | new_journal_entry,info |
| Items Taken | items_taken,info |
| Blocks to Break | blocks_to_break,info |
| Blocks to Place | blocks_to_place,info |
| Animals to Breed | animals_to_breed,info |
| Mobs to click | mobs_to_click,info |
| Fish to catch | fish_to_catch,info |
| Players to kill | players_to_kill,info |
| Potions to brew | potions_to_brew,info |
| Points given | point_given,info |
| Points taken | point_taken,info |
| Points multiplied | point_multiplied,info |
| Sheep to shear | sheep_to_shear,info |


## Configuring NotifyIO's
Each NotifyIO has its own set of configuration that can be used. None are required. The built in ones will be described in this section.

### Suppress
Does not output anything. Can be used to stop notifications.

### Chat
Writes the notification to the players chat.

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Can be from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html) or the name of a sound including from a custom resource. |

### Advancement
Shows the notification using an achievement popup.

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Can be from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html) or the name of a sound including from a custom resource. |
| frame | What Achievement frame to use. Can be: `challenge`, `goal`, `task` |
| icon | What icon to show. Must be the vanilla name of an item. Example: minecraft:map |

### Actionbar
Shows the notification using the actionbar.

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Can be from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html) or the name of a sound including from a custom resource. |

### Bossbar
Shows the notification using a bossbar.

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Can be from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html) or the name of a sound including from a custom resource. |
| barFlags | What flags to add to bossbar. One of the following from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarFlag.html).
| barColor | What color to draw the bar. One of the following from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html) |
| progress | What progress to show the bar. A floating point number between 0.0 (empty) and 1.0 (full) |
| style | What style bar to use. One of the following from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarStyle.html) |
| stay | How many ticks to keep the bar on screen. Defaults to 70 |
| countdown | If set, will step the progress of the bar by countdown steps. For example, if set to 10, then 10 times during the time it is on the screen the progress will drop by 1/10 |

### Title
Shows the notification using a title.

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Can be from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html) or the name of a sound including from a custom resource. |
| fadeIn | Ticks to fade the title in. Default 10 |
| stay | Ticks to keep title on screen. Default 70 |
| fadeOut | Ticks to fade the title out. Default 20 |
| subTitle | Optional subtitle to show. All _'s are replaced with spaces |

### SubTitle
Shows the notification using a subtitle.

| Option | Description |
|--------|-------------|
| sound | Sound to play. If blank, no sound. Can be from [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html) or the name of a sound including from a custom resource. |
| fadeIn | Ticks to fade the title in. Default 10 |
| stay | Ticks to keep title on screen. Default 70 |
| fadeOut | Ticks to fade the title out. Default 20 |


## Custom Notifications
Using the `notify` event a custom notification can be sent. It can make use of any category defined or can override by directly defining the NotifyIO configuration options. Please refer to the Events-List chapter for more details on this event.

## Examples

### Example 1 - Custom Notifications
Assuming the following custom.yml file:
```YAML
notifications:
    # Test Categories
    test_suppress:
        io: suppress

    test_chat:
        io: chat
        sound: BLOCK_CHEST_CLOSE

    test_advancement:
        io: advancement
        sound: BLOCK_CHEST_CLOSE
        frame: challenge # challenge|goal|task
        icon: map

    test_actionbar:
        io: actionbar
        sound: BLOCK_CHEST_CLOSE

    test_bossbar:
        io: bossbar
        sound: BLOCK_CHEST_CLOSE
        #barFlags: create_fog,darken_sky,play_boss_music
        barColor: purple # blue|green|pink|purple|red|white
        progress: 0.0 # 0.0 - 1.0
        style: solid # segmented_10|segmented_12|segmented_20|segmented_6|solid
        stay: 70

    test_title:
        io: title
        sound: BLOCK_CHEST_CLOSE
        fadeIn: 10
        stay: 70
        fadeOut: 20
        #subtitle:

    test_subtitle:
        io: subtitle
        sound: BLOCK_CHEST_CLOSE
        fadeIn: 10
        stay: 70
        fadeOut: 20
```

And the following events run in order:
```YAML
# Test of Category
notify_cat_suppress: notify Test Notify category:test_suppress
notify_cat_chat: notify Test Notify category:test_chat
notify_cat_advancement: notify Test Notify category:test_advancement
notify_cat_actionbar: notify Test Notify category:test_actionbar
notify_cat_bossbar: notify Test Notify category:test_bossbar
notify_cat_title: notify Test Notify category:test_title
notify_cat_subtitle: notify Test Notify category:test_subtitle

# Test of Category + some custom
notify_catcus_title_sub: notify Test Notify category:test_title subtitle:"My SubTitle"
notify_catcus_bossbar_red: notify Test Notify category:test_bossbar barColor:red sound:ENTITY_BAT_TAKEOFF style:segmented_10 progress:0.3

# Test of totally custom, needs an io
notify_cus_bossbar_green: notify Test Notify io:bossbar barColor:green stay:120
notify_cus_advancement: notify Test Notify io:advancement icon:hopper frame:goal
```

A video can be found [here](https://www.youtube.com/watch?v=mmEfIXp4dxA)

## Example 2 - Bossbar Countdown
Assuming the following events run in order:
```YAML
notify_cus_bossbar_countdown1: notify Countdown Test Bossbar io:bossbar stay:120 countdown:10 progress:1
notify_cus_bossbar_countdown2: notify Countdown Test Bossbar io:bossbar stay:120 countdown:120 progress:1
```

A video can be found [here](https://i.imgur.com/x6Ihe7W.mp4)

## Example 3 - Using suppress on a builtin notification
If you don't want to see the `changelog` notifications then you can use the suppress notifyio for the category `changelog`.

Example custom.yml
```YAML
notifications:
  changelog:
    io: suppress
```

