## PacketEvents[](https://www.spigotmc.org/resources/80279/)

### Actions

#### Freeze players: 'freeze'

This action allows you to freeze player for the given amount of ticks:

```YAML title="Example"
actions:
  freezeMe: "freeze 100" #Freezes the player for 5 seconds
```

### Chat Interceptor

#### Packet interceptor: `packetevents`

This interceptor works on network package level and is thus much more reliable than the `simple` interceptor
when working with advanced Chat plugins.
It can also reprint the history of the chat after the conversation,
so the conversation can not be seen in the chat history,
see [Plugin Configuration](../../../../Configuration/Plugin-Config.md#conversation-conversation-settings) for more information.
