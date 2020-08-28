## General Information
Each conversation must define name of the NPC 
(some conversations can be not bound to any NPC, so it’s important to specify it even though an NPC will have a name) and his initial options.
```YAML
quester: Name
first: option1, option2
stop: 'true'
final_events: event1, event2
interceptor: simple
NPC_options:
  option1:
    text: Some text in default language
    events: event3, event4
    conditions: condition1, !condition2
    pointers: reply1, reply2
  option2:
    text: '&3This ends the conversation'
player_options:
  reply1:
    text:
      en: Text in English
      pl: Tekst po polsku
    event: event5
    condition: '!condition3'
    pointer: option2
  reply2:
    text: 'Text containing '' character'
```

!!! note
    Configuration files use YAML syntax. Google it if you don't know anything about it. Main rule is that you must use two spaces instead of tabs when going deeper into the hierarchy tree. If you want to write `'` character, you must double it and surround the whole text with another `'` characters. When writing `true` or `false` it also needs to be surrounded with `'`. If you want to start the line with `&` character, the whole line needs to be surrounded with `'`. You can check if the file is correct using [this tool](http://www.yamllint.com).

* `quester` is name of NPC. It should be the same as name of NPC this conversation is assigned to for greater immersion, but it's your call.
* `first` are pointers to options the NPC will use at the beginning of the conversation. He will choose the first one that meets all conditions. You define these options in `npc_options` branch.
* `final_events` are events that will fire on conversation end, no matter how it ends (so you can create e.g. guards attacking the player if he tries to run). You can leave this option out if you don't need any final events.
* `stop` determines if player can move away from an NPC while in this conversation (false) or if he's stopped every time he tries to (true). If enabled, it will also suspend the conversation when the player quits, and resume it after he joins back in. This way he will have to finish his conversation no matter what. _It needs to be in `''`!_ You can modify the distance at which the conversation is ended / player is moved back with `max_npc_distance` option in the _config.yml_.
* `interceptor` optionally set a chat interceptor for this conversation. Multiple interceptors can be provided in a comma-separated list with the first valid one used.
* `NPC_options` is a branch with texts said by the NPC.
* `player_options` is a branch with options the player can choose.
* `text` defines what will display on screen. If you don't want to set any events/conditions/pointers to the option, just skip them. Only `text` is always required.
* `conditions` are names of conditions which must be met for this option to display, separated by commas.
* `events` is a list of events that will fire when an option is chosen (either by NPC or a player), defined similarly to conditions.
* `pointer` is list of pointers to the opposite branch (from NPC branch it will point to options player can choose from when answering, and from player branch it will point to different NPC reactions).

When an NPC wants to say something he will check conditions for the first option (in this case `option1`). If they are met, he will choose it. Otherwise, he will skip to next option (note: conversation ends when there are no options left to choose). After choosing an option NPC will execute any events defined in it, say it, and then the player will see options defined in `player_options` branch to which `pointers` setting points, in this case `reply1` and `reply2`. If the conditions for the player option are not met, the option is simply not displayed, similar to texts from NPC. Player will choose option he wants, and it will point back to other NPC text, which points to next player options and so on.

If there are no possible options for player or NPC (either from not meeting any conditions or being not defined) the conversations ends. If the conversation ends unexpectedly, check the console - it could be an error in the configuration.

This can and will be a little confusing, so you should name your options, conditions and events in a way which you will understand in the future. Don't worry though, if you make some mistake in configuration, the plugin will tell you this in console when testing a conversation. Also, study the default conversation included with the plugin to fully understand how powerful this system can be.

## Cross-conversation pointers

If you want to create a conversation with multiple NPCs at once or split a huge conversation into smaller, more focused files, you can point to NPC options in other conversation. Just type the pointer as `conversation.npc_option`.

Keep in mind that you can only cross-point to NPC options. It means that you can use those pointers only in `first` starting options and in all player options. Using them in NPC options will throw errors.

## Conversation variables

You can use variables in the conversations. They will be resolved and displayed to the player when he starts a conversation. A variable generally looks like that: `%type.optional.arguments%`. Type is a mandatory argument, it defines what kind of variable it is. Optional arguments depend on the type of the variable, i.e. `%npc%` does not have any additional arguments, but `%player%` can also have `display` (it will look like that: `%player.display%`). You can find a list of all available variable types in the "Variables List" chapter.

!!! note
    If you use a variable incorrectly (for example trying to get a property of an objective which isn't active for the player, or using %npc% in `message` event), the variable will be replaced with empty string ("").

## Translations

As you can see in default conversation, there are additional messages in other languages. That's because you can translate your conversations into multiple languages. The players will be albe to choose their preferred one with **/questlang** command. You can translate every NPC/player option and quester's name. You do this like this:

```YAML
quester:
  en: Innkeeper
  pl: Karczmarz
  de: Gastwirt
```

As said before, the same rule applies to all options and quester's name. The player can choose only from languages present in _messages.yml_, and if there will be no translation to this language in the conversation, the plugin will fall back to the default language, as defined in _config.yml_. If that one is not defined, there will be an error.

You can also translate journal entries, quest cancelers and `message` events, more about that later.

## Conversation displaying

By default BetonQuest uses the most native and safe way of displaying a conversation, which is the Minecraft chat. You choose the option by typing their number in. You can however change it with `default_conversation_IO` option in _config.yml_ file. Default value is `simple`. By changing it to `tellraw` you will add a possibility to click on options. Keep in mind that if the chat is quickly flowing, players will sometimes "miss" an option and click another one. There is a display type that doesn't suffer from this problem at all, it's called `chest`. It will display the conversation in an inventory GUI, where the NPC's text and options will be shown as item lore. Alternatively use `slowtellraw` which provides the npc responses line by line delayed by 0.5 seconds. If you have `protocollib` then you can use `menu`.

You can control the colors of conversation elements in the _config.yml_ file, in `conversation_colors` section. Here you must use names of the colors.

If you're using the `chest` display method you can change the option's item to something else than Ender Pearl by adding a prefix to that option's text. The prefix is a name of the material (like in _items.yml_) inside curly brackets, with optional damage value after a colon. Example of such option text: `{diamond_sword}I want to start a quest!` or `{wool:10}Purple!`

In case you want to use a different type of conversation display for a specific conversation you can add `conversationIO: <type>` setting to the conversation file at the top of the YAML hierarchy, which is the same level as `quester` or `first` options).

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
Conversation also supports the concept of inheritance. Any option can include the key `extends` with a comma delimited list of other options of the same time. The first option that does not have any false conditions will have it's text, pointers and events merged with the extending option. The extended option may itself extend other options. Infinite loops are detected.

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

