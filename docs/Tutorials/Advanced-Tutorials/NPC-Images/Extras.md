---
icon: material/star
tags:
  - Extras
  - NPC Images
---

This chapter features additional tips and tricks for npc images. 

<div class="grid" markdown>
!!! example "Related Docs"
    * [Conversations](../../../Documentation/Features/Conversations.md)
</div>

## Multiple Images at different Stages

You will likely want to create multiple conversations to use various quester names or images. This is useful if you want
to change the border to a "New Task" border or display different images when a task is expired, started, completed, or
canceled. There are many use cases for utilizing this approach.

In this case we create five conversations in one package and link to them using the first option in our Conversation.

``first: "npc_text_1,npc_text_4,npc_text_6,conv_4.npc_text_5,conv_3.npc_text_7"``

You now have to use conditions to make the correct image appear at the right time.
A working example to download and try out is available in our [Discord](https://discord.com/channels/407221862980911105/1260617713802285207).
