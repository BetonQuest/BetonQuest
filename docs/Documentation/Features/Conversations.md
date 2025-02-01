---
icon: material/message-text
tags:
  - Conversation
---

Conversations are the main way to interact with players in BetonQuest. They are used to display text, ask questions and 
execute commands. This page contains the reference documentation for all conversation related features. Consider doing the
[conversation tutorial](../../Tutorials/Getting-Started/Basics/Conversations.md) if you are just getting started.


## General Information
A conversation is a sequence of questions and answers. It is started by a NPC and can be ended by both the player or
the NPC.

```YAML title="Example conversation"
conversations: #(1)!
  mayorHans: #(2)!
    quester: "Hans the Mayor" #(3)!
    first: "welcome,blacksmithReminder" #(4)!
    stop: "true"  #(5)!
    final_events: "setCityState" #(6)!
    interceptor: "simple" #(7)!
    npcs: "Hans" #(14)!
    NPC_options: #(8)!
      welcome:
        text: "Good day, dear %player%! Welcome back to my town." #(10)!
        events: "playSound,giveMoney" #(12)!
        conditions: "firstVisit,!criminal" #(11)!
        pointers: "friendly,hostile" #(13)!
      blacksmithReminder:
        text: "Please visit the blacksmith, he has a task for you."
        conditions: "!criminal"
      howDareYou:
        text: "How dare you to talk to me like that?! Get out of my sight!"
    player_options: #(9)!
      friendly:
        text: "Thank you your honor, I'm happy to be here."
        event: "givePresent"
        pointer: "blacksmithReminder"
      hostile:
        text: "Your Honor, I come bearing a ultimatum letter from the people. They have grown tired of your corruption and greed."
        condition: 'hasUltimatumLetter'
        pointers: "howDareYou"
```
    

1. All conversation must be defined in a `conversations` section.
2. `mayorHans` is the name of the conversation, which is used to reference the conversation. 
3. `Hans` is the visual name of NPC that is displayed during the conversation.
4. `first` are pointers to options the NPC will use at the beginning of the conversation. He will choose the first one that meets all conditions. You 
    define these options in `npc_options` branch.
5. `stop` determines if player can move away from an NPC while in this conversation (false) or if he's stopped every time
    he tries to (true). If enabled, it will also suspend the conversation when the player quits, and resume it after he 
    joins back in. This way he will have to finish his conversation no matter what. You can modify
    the distance at which the conversation is automatically stopped / player is teleported back with `max_npc_distance` option in _config.yml_.
6. `final_events` are events that will fire when the conversation ends, no matter how it ends (so you can create e.g. guards attacking
    the player if he tries to run). You can leave this option out if you don't need any final events.
7. `interceptor` optionally set a chat interceptor for this conversation. Multiple interceptors can be provided in a comma-separated list with the first valid one used. It's better to set this as a global config setting in _config.yml_.
8. `NPC_options` is a branch with texts said by the NPC.
9. `player_options` is a branch with options the player can choose from.
10. `text` defines what will display on screen. If you don't want to set any events/conditions/pointers to the option, just skip them. Only `text` is always required.
11. `conditions` are names of conditions which must be met for this option to display, separated by commas.
12. `events` is a list of event names that will fire when an option is chosen (either by NPC or a player), defined similar to conditions.
13. `pointer` is list of pointers to the opposite branch (from NPC branch it will point to options player can choose from when answering, and from player branch it will point to different NPC reactions).
14. `npcs` is a list of in world npcs, [read more](#binding-conversations-to-npcs).

When an NPC wants to say something he will check conditions for the first option (in this case `welcome`). If they are met,
he will choose it. Otherwise, he will skip to next option (note: conversation ends when there are no options left to choose).
After choosing an option the NPC will execute any events defined in it and say it's text. Then the player will see options
defined in the `player_options` branch to which the `pointers` setting points, in this case `friendly` and `hostile`. If
the conditions for a player options is not met, the option is simply not displayed, similar to texts from NPC. The player
will choose the option they want, and it will point back to other NPC text, which points to next player options and so on.

If there are no possible options for player or NPC (either from not meeting any conditions or being not defined) the
conversations ends. If the conversation ends unexpectedly, check the console - it could be an error in the configuration.

This can and will be a little confusing, so you should name your options, conditions and events in a way which you will
understand in the future. Don't worry though, if you make some mistake in configuration, the plugin will tell you this when running `/q reload`.


## Binding Conversations to NPCs

Conversations can assign Npcs that will start them on interaction.
See [Npcs](Npcs.md) for Npc definitions.

A Npc will only react to right clicks by default. 
This can be changed by setting `acceptNPCLeftClick` in the config.yml to `true`.

You can assign the same conversation to multiple NPCs.
It is not possible to assign multiple conversations to one npc. For this
purpose, have a look at 
[cross-conversation-pointers](#cross-conversation-pointers) though.

## Conversation displaying

BetonQuest provides different conversation styles, so called "conversationIO's". They differ in their visual style
and the way the player interacts with them.

BetonQuest uses the `menu` style by default. If ProtocolLib is not installed, the `chest` style will be used.
You can change this setting globally by changing the [`default_conversation_IO`](../Configuration/Configuration.md#default-conversation-style) option in the _config.yml_ file.

It is also possible to override this setting per conversation. Add a `conversationIO:
<type>` setting to the conversation file at the top of the YAML hierarchy (which is the same level as `quester` or `first` options).

In both cases, you can choose from the following conversation styles:

!!! example "Conversation Styles"
    === "`menu`"
        A modern conversation style that works with some of Minecraft's native controls.
        
        **Requires [ProtocolLib](https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/)**
            
        ??? "Customizing the Menu Style"
            Customize the look of the menu style by adding the following lines to any of your quest packages. These
            are global settings that currently cannot be changed on a NPC level.
            
            ```YAML
            menu_conv_io:
              start_new_lines: 10 # (1)!
              line_length: 50 # (2)!
              refresh_delay: 180 # (3)!
              selectionCooldown: 10 # (4)!
            
              npc_wrap: '&l &r' # (5)!
              npc_text: '&l &r&f{npc_text}' # (6)!
              npc_text_reset: '&f' # (7)!
              option_wrap: '&r&l &l &l &l &r' # (8)!
              option_text: '&l &l &l &l &r&8[ &b{option_text}&8 ]' # (9)!
              option_text_reset: '&b' # (10)! 
              option_selected: '&l &r &r&7»&r &8[ &f&n{option_text}&8 ]' # (11)!
              option_selected_reset: '&f' # (12)!
              option_selected_wrap: '&r&l &l &l &l &r&f&n' # (13)!
            
              control_select: jump,left_click # (14)!
              control_cancel: sneak # (15)! 
              control_move: scroll,move # (16)! 
            
              npc_name_type: chat # (17)!
              npc_name_align: center # (18)!
              npc_name_format: '&e{npc_name}&r' # (19)!
              npc_name_newline_separator: true # (20)!
              npc_text_fill_new_lines: true # (21)!
            ```
            
            1. How many empty lines should be printed before the conversation starts.
            2. Maximum size of a line till its wrapped.
            3. Specify how many ticks to auto update display. Default 180.
            4. The cooldown for selecting another option after selecting an option. Measured in ticks. 20 ticks = 1 second.
            5. What text to prefix each new line in the NPC text that wraps.
            6. How to write the NPC text. Replaces {1} with the npcs text.
            7. When a color reset is found, what to replace it with.
            8. What text to prefix each new line in an option that wraps.
            9. How to write an option. Replaces {1} with the option text.
            10. When a color reset is found, what to replace it with.
            11. How to write a selected option. Replaces {1} with the option text.
            12. When a color reset is found, what to replace it with.
            13. What text to prefix each new line in a selected option that wraps.
            14. Comma separated actions to select options. Can be any of `jump`, `left_click`, `sneak`.
            15. Comma separated actions to cancel the conversation. Can be any of `jump`, `left_click`, `sneak`.
            16. Comma separated actions to move the selection. Can be any of `move`, `scroll`.
            17. Type of NPC name display. Can be one of: `none`, `chat`.
            18. For npc name type of `chat`, how to align name. One of: `left`, `right`, `center`.
            19. How to format the npc name.
            20. Whether an empty line is inserted after the NPC's name if there is space leftover.
            21. Weather to fill new lined between the NPC text and the player answer options
                                    
            | Text Variable   | Meaning               |
            |-----------------|-----------------------|
            | `{npc_text`     | The text the NPC says |
            | `{option_text}` | The option text       |
            | `{npc_name}`    | The name of the NPC   |
            
        <video controls loop src="../../../_media/content/Documentation/Conversations/MenuConvIO.mp4" width="100%">
          Sorry, your browser doesn't support embedded videos.
        </video>
        The blue overlay shows the player's key presses.
    === "`chest`"
        A chest GUI with clickable buttons where the NPC's text and options will be shown as item lore.
        ??? "Customizing the Chest Style"
            The colors of this style can be configured with the [`conversation_colors` config option](../Configuration/Configuration.md#conversation-colors).
            
            The formatting of this style can be configured with the [`conversation_IO_config.chest` config option](../Configuration/Configuration.md#conversation-settings-chestio-slowtellrawio).
            
            You can change the option's item to something else than ender pearls by adding a prefix to that option's text.
            The prefix is a name of the material (like in the _items_ section) inside curly braces, with an optional damage value after a colon. Custom Model Data is not supported yet.
            Example of such option text: `{diamond_sword}I want to start a quest!`.
        <video controls loop src="../../../_media/content/Documentation/Conversations/ChestIO.mp4" width="100%">
          Sorry, your browser doesn't support embedded videos.
        </video>
    === "`combined`"
        The same as the chest style but the conversation is also displayed in the chat.
    === "`simple`"
        A chat output. The user has to write a number into their chat to select an option.
        ??? "Customizing the Simple Style"
            The colors of this style can be configured with the [`conversation_colors` config option](../Configuration/Configuration.md#conversation-colors).
        ![SimpleIO](../../_media/content/Documentation/Conversations/SimpleIO.png)
    === "`tellraw`"
        The same as the simple style but the user can also click the numbers instead of writing them in the chat.
        ??? "Customizing the Simple Style"
            The colors of this style can be configured with the [`conversation_colors` config option](../Configuration/Configuration.md#conversation-colors).
        ![SimpleIO](../../_media/content/Documentation/Conversations/SimpleIO.png)
    === "`slowtellraw`"
        The same as tellraw style but the NPC's text is printed line by line, delayed by 0.5 seconds.
        ??? "Customizing the Simple Style"
            The colors of this style can be configured with the [`conversation_colors` config option](../Configuration/Configuration.md#conversation-colors).
            
            The delay between lines (in ticks) can be configured with the [`conversation_IO_config.slowtellraw` config option](../Configuration/Configuration.md#conversation-settings-chestio-slowtellrawio).
        ![SimpleIO](../../_media/content/Documentation/Conversations/SimpleIO.png)


## Cross-Conversation Pointers

If you want to create a conversation with multiple NPCs at once or split a huge conversation into smaller, 
more focused files, you can point to both npc and player options in other conversations. Use the 
[cross-package syntax](../Scripting/Packages-&-Templates.md#working-across-packages) to do so.

There is one special case when you want to refer to the starting options of another conversation. In this case you do not specify
an option name after the second point (`package.conversation.`).

```YAML title="Cross-conversation Pointers Examples"
myConversationOption:
  text: "Look carefully at that guard over there..."
  pointers: "lookCareful,guardConv.lookDetected,mainStory.Mirko.interrupt" #(1)!
specialOption:
  text: "This option points to the starting options of the conversation 'guardConv' in the package 'myPackage'."
  pointers: "myPackage.guardConv."
```

1. `lookCareful` refers to another option in the same conversation named `lookCareful`.    
   `guardConv.lookDetected` refers to the option `lookDetected` in the conversation `guardConv` in the same package.    
   `mainStory.Mirko.interrupt` refers to the option `interrupt` in the conversation `Mirko` in the package `mainStory`.    

## Conversation Variables

You can use variables in the conversations. They will be resolved and displayed to the player when he starts a conversation.
Check the [variables list](../Scripting/Building-Blocks/Variables-List.md) for more information about which variables exist.

!!! note
    If you use a variable incorrectly (for example trying to get a property of an objective which isn't active for the player, or using %npc% in `message` event), the variable will be replaced with empty string ("").

## Translations

Conversation can be fully translated into multiple languages. A players can choose their preferred language with the **/questlang** command. You can translate every NPC option, player option and the NPC's name. This is how it's done:

```YAML
quester:
  en: "Innkeeper"
  pl: "Karczmarz"
  de: "Gastwirt"
first: "example1" 
NPC_options:
  example1:
    text:
      en: "Good day, dear %player%! Welcome back to my town."
      de: "Guten Tag, lieber %player%! Willkommen zurück in meiner Stadt." 
player_options:
  example2:
    text:
      en: "Thank you your honor, I'm happy to be here."
      de: "Danke, Euer Ehren, ich bin froh, hier zu sein."
```
`en` and `de` are identifiers of languages present in the _messages.yml_ config. If the conversation is not translated in the players' language, the plugin will fall back to the default language, as defined in _config.yml_.

The same syntax can be applied in a few other features, e.g. the journal entries, quest cancelers and `notify` events.

## Chat Interceptors
While engaged in a conversation, it can be distracting when messages from other players or system messages interfere with the dialogue.
A chat interceptor provides a method of intercepting those messages and then sending them after the conversation has ended.

You can specify the default chat interceptor by setting `default_interceptor` inside the `config.yml`.
Additionally, you can overwrite the default for each conversation by setting the `interceptor` key inside your conversation file.

The default configuration of BetonQuest sets the `default_interceptor` option to `packet,simple`.
This means that it first tries to use the `packet` interceptor. If that fails it falls back to using the `simple` interceptor.

BetonQuest adds following interceptors: `simple`, `packet` and `none`:
  
The `simple` interceptor works with every Spigot server but only supports very basic functionality and may not work with plugins like Herochat.

The `packet` interceptor requires the ProtocolLib plugin to be installed. It will work well in any kind of situation.

The `none` interceptor is an interceptor that won't intercept messages. That sounds useless until you have a conversation
that you want to be excluded from interception. In this case you can just set `interceptor: none` inside your conversation file.

## Advanced: Extends
Conversations also support the concept of inheritance. Any option can include the key `extends` with a comma delimited list of other options of the same time. The first option that does not have any false conditions will have its text, pointers and events merged with the extending option. The extended option may itself extend other options. Infinite loops are detected.

```YAML
NPC_options:

  ## Normal Conversation Start
  start:
    text: 'What can I do for you'
    extends: tonight, today
    
  ## Useless addition as example
  tonight:
    # Always false
    condition: random 0-1
    text: ' tonight?'
    extends: main_menu

  today:
    text: ' today?'
    extends: main_menu

  ## Main main_menu
  main_menu:
    pointers: i_have_questions, bye
```
In the above example, the option _start_ is extended by both _tonight_ and _today_, both of whom are extended by _main_menu_. As _tonight_ has a false condition the _today_ option will win. The _start_ option will have the pointers in main_menu added to it just as if they were defined directly in it and the text will be joined together from _today_. If you structure your conversation correctly you can make use of this to minimize duplication.
