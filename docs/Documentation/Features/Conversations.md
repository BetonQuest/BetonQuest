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
    conversationIO: "menu" #(7)!
    interceptor: "simple" #(8)!
    interceptor_delay: 70 #(9)!
    NPC_options: #(10)!
      welcome:
        text: "Good day, dear %player%! Welcome back to my town." #(11)!
        events: "playSound,giveMoney" #(12)!
        conditions: "firstVisit,!criminal" #(13)!
        pointers: "friendly,hostile" #(14)!
      blacksmithReminder:
        text: "Please visit the blacksmith, he has a task for you."
        conditions: "!criminal"
      howDareYou:
        text: "How dare you to talk to me like that?! Get out of my sight!"
    player_options: #(15)!
      friendly:
        text: "Thank you your honor, I'm happy to be here."
        events: "givePresent"
        pointers: "blacksmithReminder"
      hostile:
        text: "Your Honor, I come bearing a ultimatum letter from the people. They have grown tired of your corruption and greed."
        conditions: 'hasUltimatumLetter'
        pointers: "howDareYou"
```
    

1. All conversation must be defined in a `conversations` section.
2. `mayorHans` is the name of the conversation, which is used to reference the conversation. 
3. `Hans` is the visual name of NPC that is displayed during the conversation.
4. `first` are pointers to options the NPC will use at the beginning of the conversation. He will choose the first 
    one that meets all conditions. You 
    define these options in `npc_options` branch.
5. `stop` determines if player can move away from an NPC while in this conversation (false) or if he's stopped 
    every time he tries to (true). If enabled, it will also suspend the conversation when the player quits, and 
    resume it after he joins back in. This way he will have to finish his conversation no matter what. You can modify
    the distance at which the conversation is automatically stopped / player is teleported back with 
    `max_conversation_distance` option in "_config.yml_".
6. `final_events` are events that will fire when the conversation ends, no matter how it ends (so you can create 
    e.g. guards attacking the player if he tries to run). You can leave this option out if you don't need any final 
    events.
7. `conversationIO` optionally set the conversation style for this conversation. Multiple styles can be provided in 
    acomma-separated list with the first valid one used. It's better to set this as a global config setting in
    "_config.yml_".
8. `interceptor` optionally set a chat interceptor for this conversation. Multiple interceptors can be provided in a 
    comma-separated list with the first valid one used. It's better to set this as a global config setting in
    "_config.yml_".
9. `interceptor_delay` optionally set a delay (in ticks) after the conversation ends and before the interceptor is 
    displayed. This can also be set globally in "_config.yml_".
10. `NPC_options` is a branch with texts said by the NPC.
11. `text` defines what will display on screen. If you don't want to set any events/conditions/pointers to the 
    option, just skip them. Only `text` is always required.
12. `events` is a list of event names that will fire when an option is chosen (either by NPC or a player), defined 
    similar to conditions.
13. `conditions` are names of conditions which must be met for this option to display, separated by commas.
14. `pointers` is list of pointers to the opposite branch (from NPC branch it will point to options player can 
    choose from when answering, and from player branch it will point to different NPC reactions).
15. `player_options` is a branch with options the player can choose from.

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

Conversations can be assigned to [NPCs](NPCs.md). This is done in the `npc_conversations` section:

```YAML title="Example"
npc_conversations:
  Hans: mayorHans #(1)!
```

1. The key is the NpcID, the value a ConversationID.

A NPC will only react to right clicks by default. 
This can be changed by setting `npcs.accept_left_click` in the "_config.yml_" to `true`.

You can assign the same conversation to multiple NPCs.
It is not possible to assign multiple conversations to one NPC. For this
purpose, have a look at 
[cross-conversation-pointers](#cross-conversation-pointers) though.

## Conversation displaying

BetonQuest provides different conversation styles, so called "conversationIO's". They differ in their visual style
and the way the player interacts with them.

BetonQuest uses the `menu` style by default if the server runs at least Minecraft version 1.21.4.
If PacketEvents is installed it will use the `packetevents` style as fallback, otherwise the `tellraw` style will be used.
You can change this setting globally by changing the [`default_io`](../Configuration/Plugin-Config.md#conversation-conversation-settings) option in the "_config.yml_" file.

It is also possible to override this setting per conversation. Add a `conversationIO: <type>` setting
to the conversation file at the top of the YAML hierarchy (which is the same level as `quester` or `first` options).

In both cases, you can choose from the following conversation styles:

!!! example "Conversation Styles"
    === "`menu`"
        A modern conversation style that works with some of Minecraft's native controls.
        
        @snippet:versions:mc-1.21.4@
        
        When `set_speed` is disabled the player won't be able to be moved by external sources and get "rubber banding"
        like effect when moving/selecting options.
        
        ??? "Customizing the Menu Style"
            The formatting of this style can be configured with the [`menu` config option](../Configuration/Plugin-Config.md/#io-conversation-io-settings).
        <video controls loop src="../../../_media/content/Documentation/Conversations/MenuConvIO.mp4" width="100%">
          Sorry, your browser doesn't support embedded videos.
        </video>
        The blue overlay shows the player's key presses.
    === "`packetevents`"
        Similar to `menu`, but it mounts the player client side on a fake entity instead.
        
        **Requires [PacketEvents](https://www.spigotmc.org/resources/80279/)**
        
        It uses the same Customization as `menu`.
    === "`chest`"
        A chest GUI with clickable buttons where the NPC's text and options will be shown as item lore.
        ??? "Customizing the Chest Style"
            The colors of this style can be configured with the [`color` config option](../Configuration/Plugin-Config.md/#conversation-conversation-settings).
            
            The formatting of this style can be configured with the [`chest` config option](../Configuration/Plugin-Config.md/#io-conversation-io-settings).
            
            You can change the option's item to something else than ender pearls by adding adding a new section
            `properties` to the `player_options` and reference an item by its ID. This will then look like this:
            
            ```YAML
            player_options:
              exampleOption:
                text: "This is an example option"
                properties:
                  item: "diamond" #(1)!
            ```
            
            1. The item ID of the item you want to use for the option.
        <video controls loop src="../../../_media/content/Documentation/Conversations/ChestIO.mp4" width="100%">
          Sorry, your browser doesn't support embedded videos.
        </video>
    === "`combined`"
        The same as the chest style but the conversation is also displayed in the chat.
    === "`simple`"
        A chat output. The user has to write a number into their chat to select an option.
        ??? "Customizing the Simple Style"
            The colors of this style can be configured with the [`color` config option](../Configuration/Plugin-Config.md/#conversation-conversation-settings).
        ![SimpleIO](../../_media/content/Documentation/Conversations/SimpleIO.png)
    === "`tellraw`"
        The same as the simple style but the user can also click the numbers instead of writing them in the chat.
        ??? "Customizing the Simple Style"
            The colors of this style can be configured with the [`color` config option](../Configuration/Plugin-Config.md/#conversation-conversation-settings).
        ![SimpleIO](../../_media/content/Documentation/Conversations/SimpleIO.png)
    === "`slowtellraw`"
        The same as tellraw style but the NPC's text is printed line by line, delayed by 0.5 seconds.
        ??? "Customizing the Simple Style"
            The colors of this style can be configured with the [`color` config option](../Configuration/Plugin-Config.md/#conversation-conversation-settings).
            
            The delay between lines (in ticks) can be configured with the [`slowtellraw`](../Configuration/Plugin-Config.md/#io-conversation-io-settings) config option.
        ![SimpleIO](../../_media/content/Documentation/Conversations/SimpleIO.png)


## Cross-Conversation Pointers

If you want to create a conversation with multiple NPCs at once or split a huge conversation into smaller, 
more focused files, you can point to both NPC and player options in other conversations. Use the 
[cross-package syntax](../Scripting/Packages-&-Templates.md#working-across-packages) to do so.

There is one special case when you want to refer to the starting options of another conversation. In this case you do not specify
an option name after the point (`package>conversation.`).

```YAML title="Cross-conversation Pointers Examples"
myConversationOption:
  text: "Look carefully at that guard over there..."
  pointers: "lookCareful,guardConv.lookDetected,mainStory>Mirko.interrupt" #(1)!
specialOption:
  text: "This option points to the starting options of the conversation 'guardConv' in the package 'myPackage'."
  pointers: "myPackage>guardConv."
```

1. `lookCareful` refers to another option in the same conversation named `lookCareful`.    
   `guardConv.lookDetected` refers to the option `lookDetected` in the conversation `guardConv` in the same package.    
   `mainStory>Mirko.interrupt` refers to the option `interrupt` in the conversation `Mirko` in the package `mainStory`.    

## Conversation Variables

You can use variables in the conversations. They will be resolved and displayed to the player when he starts a conversation.
Check the [variables list](../Scripting/Building-Blocks/Variables-List.md) for more information about which variables exist.

!!! note
    If you use a variable incorrectly (for example trying to get a property of an objective which isn't active for the player, or using %quester% in `message` event), the variable will be replaced with empty string ("").

## Translations

Conversation can be fully translated into multiple languages. A players can choose their preferred language with the **/questlang** command. You can translate every NPC option, player option and the NPC's name. This is how it's done:

```YAML
quester:
  en-US: "Innkeeper"
  pl-PL: "Karczmarz"
  de-DE: "Gastwirt"
first: "example1" 
NPC_options:
  example1:
    text:
      en-US: "Good day, dear %player%! Welcome back to my town."
      de-DE: "Guten Tag, lieber %player%! Willkommen zurück in meiner Stadt." 
player_options:
  example2:
    text:
      en-US: "Thank you your honor, I'm happy to be here."
      de-DE: "Danke, Euer Ehren, ich bin froh, hier zu sein."
```
`en-US` and `de-DE` are identifiers of languages present in the lang folder. If the conversation is not translated in the players' language, the plugin will fall back to the default language, as defined in "_config.yml_".

The same syntax can be applied in a few other features, e.g. the journal entries, quest cancelers and `notify` events.

## Chat Interceptors
While engaged in a conversation, it can be distracting when messages from other players or system messages interfere with the dialogue.
A chat interceptor provides a method of intercepting those messages and then sending them after the conversation has ended.

You can specify the default chat interceptor by setting `default_interceptor` inside the "_config.yml_".
Additionally, you can overwrite the default for each conversation by setting the `interceptor` key inside your conversation file.

The default configuration of BetonQuest sets the `default_interceptor` option to `packetevents,simple`.
This means that it first tries to use the `packetevents` interceptor. If that fails it falls back to using the `simple` interceptor.

BetonQuest adds following interceptors: `simple`, `packetevents` and `none`:
  
The `simple` interceptor works with every server but only supports very basic functionality and may not work with plugins like Herochat.

The `packetevents` interceptor requires the PacketEvents plugin to be installed. It will work well in any kind of situation.

The `none` interceptor is an interceptor that won't intercept messages. That sounds useless until you have a conversation
that you want to be excluded from interception. In this case you can just set `interceptor: none` inside your conversation file.

## Advanced: Extends
Conversations also support the concept of inheritance. Any option can include the key `extends` with a comma delimited list of other options of the same time. The first option that does not have any false conditions will have its text, pointers and events merged with the extending option. The extended option may itself extend other options. Infinite loops are detected.

```YAML
NPC_options:

  ## Normal Conversation Start
  start:
    text: 'What can I do for you'
    extends: tonight,today
    
  ## Useless addition as example
  tonight:
    # Always false
    conditions: random_0-1
    text: ' tonight?'
    extends: main_menu

  today:
    text: ' today?'
    extends: main_menu

  ## Main main_menu
  main_menu:
    pointers: i_have_questions,bye
```
In the above example, the option _start_ is extended by both _tonight_ and _today_, both of whom are extended by _main_menu_. As _tonight_ has a false condition the _today_ option will win. The _start_ option will have the pointers in main_menu added to it just as if they were defined directly in it and the text will be joined together from _today_. If you structure your conversation correctly you can make use of this to minimize duplication.
