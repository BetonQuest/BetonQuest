---
icon: material/message-cog
---

Every string in BetonQuest can be formatted.

## Formatter

### Legacy - `legacy`

The legacy formatter is the old common way to format strings. It's a really simple formatter that has a lot of 
limitations, but it is still used by the community as it is the most known one.
It uses the `&` or `§` character followed by a color code or a style code character.

You can read everything about minecraft formatting [here](https://minecraft.wiki/w/Formatting_codes).

This formatter actually can parse a bit more as normally, like links get clickable,
and colors in the adventure format `§#a25981` or the BungeeCord RGB format `§x§a§2§5§9§8§1`.

You can read everything about these formats [here](https://docs.advntr.dev/serializer/legacy.html#rgb-support).

### MiniMessage - `minimessage`

MiniMessage is the new standard for formatting strings. It's a really advanced formatter that has a lot of features.
The formatting is based on tags like `<red>` and `<bold>`. You don't need to close them like `</red>`,
but that sometimes make it clear what exactly you are formatting.

Everything about this format can be read [here](https://docs.advntr.dev/minimessage/format.html).

### Legacy & MiniMessage - `legacyminimessage`

This formatter is a combination of the legacy and MiniMessage formatter. It allows you to use both formats.
In that way, you can use the format that fits the best for every string.
You can also use both formats in one string, but you need to be careful with that, as it can lead to unexpected results.

### MineDown - `minedown`

This formatter is a perfect alternative to MiniMessage.
Mainly, it still supports the old legacy format, but also the new RGB format, as well as some more simple formatting.
You don't need to write these tags like in MiniMessage, instead you write more advanced formats like this `[Text]
(format)`.

You can read everything about this format [here](https://github.com/Phoenix616/MineDown).
