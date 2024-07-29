---
icon: material/message-processing
tags:
  - Conversation
  - NPC Images
---

Since we have modified our resource pack with all our pixel art images, we can now proceed with updating our QuestPackage
with all the necessary changes. 

<div class="grid" markdown>
!!! example "Related Docs"
    * [Conversations](../../../Documentation/Features/Conversations.md)
</div>

## Changing the menu_conv_io

Let's modify our QuestPackage to use a different conversation style. 
Paste this code into your `package.yml`:

``` YAML title="package.yml" linenums="1"
    menu_conv_io:
        npc_wrap: '&f              '
        npc_text: '&f              {npc_text}'
        npc_name_align: left
        npc_name_format: '{npc_name}'
        npc_name_newline_separator: true #Personal Preference
        option_text: '&e              {option_text}'
        option_selected: '&e              {option_text}'
 
        control_select: jump,left_click 
        control_cancel: sneak 
```

What does this do?

This wraps the text a certain amount of spaces to the right, allowing some space for our new image. 
This space might vary upon personal preference and can easily be extended by adding some more spaces.

!!! danger
    Changing this in one **QuestPackage** does change it globally!

## Modify the conversation


``` YAML title="conversations.yml" hl_lines="3 8" linenums="1"
conversations:
  conv_0:
    quester: "ʩ"
    first: "npc_text_1"
    final_events: "cancelled_conversation"
    NPC_options:
      npc_text_1:
        text: "&fꎼ &f&lJustus &8(1/2)&r
        \n&7Hej! Did u see those Mountains? I could watch the sunset behind them all day long!"
        pointer: point1,point2
        conditions: "!finished,!started,!step_1,!step_2,!step_3"
        events: "started_conversation"
   
```

So what do we see here? In line three, we replace the usual quester name with our Unicode glyph. In line eight, we define
our text as usual. We don't need to add spaces here since we already included them with our menu_conv_io. In our example,
we also use another Unicode character to add a small chat icon.

### Optional: Invisible Player-Option

Sometimes these layouts look better if we only give the player on option, for example called ``next``, which is invisible.
Look at the following example:

``` YAML title="conversations.yml" hl_lines="9-10 15-16" linenums="1"
conversations:
  conv_0:
    quester: "ʩ"
    first: "npc_text_1"
    final_events: "cancelled_conversation"
    NPC_options:
      npc_text_1:
        text: "&fꎼ &f&lJustus &8(1/2)&r
        \n&7Hej! Did u see those Mountains? I could watch the sunset behind them all day long! &7→ &fꐘ"
        pointer: next
        conditions: "!finished,!started,!step_1,!step_2,!step_3"
        events: "started_conversation"
   
    player_options:
      next:
        text: ''
        pointer: ...
```

Here, we added a pointer to next with no text and instead extended our npc_text_1 to add a ``&7→ &fꐘ``, which resolves 
as an arrow pointing to a mouse-click icon. You could also just write ``-> NEXT`` if you prefer.
