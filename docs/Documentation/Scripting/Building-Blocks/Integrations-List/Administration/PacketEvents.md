# [PacketEvents](https://www.spigotmc.org/resources/80279/)

@snippet:versions:minimum@ _2.9.5_

## Chat Interceptor

### `packetevents`

This interceptor works on network package level and is thus much more reliable than the `simple` interceptor
when working with advanced Chat plugins.
It can also reprint the history of the chat after the conversation,
so the conversation can not be seen in the chat history,
see [Plugin Configuration](../../../../Configuration/Plugin-Config.md#conversation-conversation-settings) for more information.

## Actions

### `Freeze`

__Context__: @snippet:action-meta:online@  
__Syntax__: `freeze <ticks>`  
__Description__: Freeze the player for the specified number of ticks.

```YAML title="Example"
actions:
  freezeMe: "freeze 100" #Freezes the player for 5 seconds (20 ticks = 1 second)
```
