---
icon: material/message-cog
---

!!! warning "Work in Progress"
    This feature is still in development and does not work for every feature at the moment.
    Some features are marked as limited, that means that things like hover and click events are not supported.
    Currently supported are:
    
    - Notify and NotifyAll Event
    - Compass Names
    - NPC Name Variable (limited)
    - Journal (limited)
    - Quest Cancler (limited)
    - Conversation (limited)
    - Plugin Messages / Translations (limited)

Every string in BetonQuest can be formatted with a formatter.
A formatter is a way to format a string with colors, styles, and more, while each formatter has its own syntax.

In the "_config.yml_" file, you can set the default formatter with the `text_parser` setting.
The default formatter is `legacyminimessage`.

Anyway each string can set an individual formatter by prefixing the string with `@[FormatterName]`.

## Formatter

### Legacy

```yaml
legacy
```

The legacy formatter is the old common way to format strings. It's a really simple formatter that has a lot of 
limitations, but it is still used by the community as it is the most known one.
It uses the `&` or `§` character followed by a color code or a style code character.

You can read everything about minecraft formatting [here](https://minecraft.wiki/w/Formatting_codes).

This formatter actually can parse a bit more as normally, like links get clickable,
and colors in the adventure format `§#a25981` or the BungeeCord RGB format `§x§a§2§5§9§8§1`.

You can read everything about these formats [here](https://docs.advntr.dev/serializer/legacy.html#rgb-support).

!!! example
    ```yaml
    text1: '&cHello &e&lWorld'
    text2: '@[legacy]&cHello &e&lWorld'
    ```

### MiniMessage

````yaml
minimessage
````

MiniMessage is the new standard for formatting strings. It's a really advanced formatter that has a lot of features.
The formatting is based on tags like `<red>` and `<bold>`. You don't need to close them like `</red>`,
but that sometimes make it clear what exactly you are formatting.

Everything about this format can be read [here](https://docs.advntr.dev/minimessage/format.html).

!!! example
    ```yaml
    text1: '<red>Hello <yellow><bold>World</bold>'
    text2: '@[minimessage]<red>Hello <yellow><bold>World</bold>'
    ```

### Legacy & MiniMessage

````yaml
legacyminimessage
````

This formatter is a combination of the legacy and MiniMessage formatter. It allows you to use both formats.
In that way, you can use the format that fits the best for every string.
You can also use both formats in one string, but you need to be careful with that, as it can lead to unexpected results.

!!! example
    ```yaml
    text1: '&cHello <yellow><bold>World</bold>'
    text2: '@[legacyminimessage]&cHello <yellow><bold>World</bold>'
    ```

### MineDown

````yaml
minedown
````

This formatter is a perfect alternative to MiniMessage.
Mainly, it still supports the old legacy format, but also the new RGB format, as well as some more simple formatting.
You don't need to write these tags like in MiniMessage, instead you write more advanced formats like this `[Text](format)`.

You can read everything about this format [here](https://wiki.phoenix616.dev/library/minedown/syntax).

!!! example
    ```yaml
    text1: '[Hello](red) [World](yellow bold)'
    text2: '@[minedown][Hello](red) [World](yellow bold)
    ```
