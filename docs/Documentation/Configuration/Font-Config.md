---
icon: fontawesome/solid/font
---

Mainly in conversation and the journal of BetonQuest you frequently encounter automatically wrapped text.  
To customize the way this text is wrapped, you can configure a font index file to determine the width of each character.  
For custom fonts contained in resource packs this also enables you to ensure correct line wrapping.  
All configurations for fonts are located in the `BetonQuest/fonts` folder.

!!! warning
    This feature does **not** add fonts to the game.  

## Font Index File (FIF)

A font index file is a single file representing the character widths of a single font.
By configuring a FIF, you can precisely adjust the way characters are wrapped in the chat.
There currently exist two formats for FIFs: `json` and `binary`.

### Formats
!!! example ""

    === "JSON"
    
        !!! tip ""
            Use this format if you are frequently editing the font index file.  
        The JSON format uses key-value pairs where the key is the character and the value is the width.  
        It contains a single JSON object at the top level and represents each pair as a property.  
        
        ??? example "myfont.json"
             ``` json
             {
               "(": 3,
               ")": 3,
               "*": 3,
               "+": 5,
               ",": 1,
               "-": 5,
               ".": 1,
               "/": 5
             }
             ```
        !!! warning
            Make sure your json is saved as UTF-8 if you are using the inbuilt reader to avoid character encoding issues.
    
    === "Binary"
    
        !!! tip ""
            Use this format for large font index files that you are **not** frequently editing.
        The binary format saves a sequence of *characters* and their *widths* in a fixed-sized block of four bytes which 
        can be represented as a 32-bit integer in java.  
        The character is represented as the Unicode code point, which is a number in the range of `0x0 - 0x10FFFF` 
        occupying the 21 least significant bits of the aforementioned integer.  
        The width is a number in the range of `0x0 - 0x7FF = 0 - 2047` occupying the 11 most significant bits as unsigned integer.
        A block might look like this: `[11 bits width][21 bits codepoint]`      
    
        ??? example "Examples"
            - Character `t` with exemplary width `4`: 
                * Blocks in hex: `[0x4][0x74]`
                * Blocks in binary: `[00000000100][000000000000001110100]`
                * Bytes in hex: `0x00 0x80 0x00 0x74`
            - Character `生` with exemplary width `15`: 
                * Blocks in hex: `[0xF][0xEA3F]`
                * Blocks in binary: `[00000001111][000000111010100011111]`
                * Bytes in hex: `0x01 0xE0 0x75 0x1F`
        
        !!! warning
            The binary format is not able to store characters with a width greater than `2047`!

### Default Reader

The default reader for FIFs considers the file extension of the FIF to determine the format.

!!! info inline "Extension"
    All characters after the last dot in the file name are considered the file extension.

| Extension   | Format   |
| :---------: | :------: |
| `.json`     | JSON     |
| `.bin`      | Binary   |

The default reader for FIFs considers the first part of the filename to determine the font's key to be used in the game.

!!! info inline ""

    !!! info "Filename"
        All characters before the first dot in the file name are considered the filename.
    
    !!! info "Namespaces"
        Since `:` is not allowed in filenames for some filesystems, the default reader considers `+` as the namespace delimiter.  
        It defaults to `minecraft` if no namespace is specified. 
    
| Example     | Key | Format |
| :--------- | :--: | :--: |
| `myfont.json` | `minecraft:myfont` | JSON |
| `font.bin` | `minecraft:font` | Binary |
| `my.font.json` | `minecraft:my` | JSON |
| `default.font.bin` | `minecraft:default` | Binary |
| `namespaced+font.bin` | `namespaced:font` | Binary |
| `fancy+custom.font.json` | `fancy:custom` | JSON |

### Default Minecraft Font

The default font index file is located in `BetonQuest/fonts/default.font.bin` and uses the `binary` format.
!!! warning "Current State"
    Except for the default whitespace character (codepoint `32`, UTF-8 `U+20`), all other Unicode whitespace 
    characters are treated as 0-width characters.

## Changing The Font

!!! example ""

    === "MiniMessage"
       
        To change the font in the MiniMessage format, use the `<font:[key]></font>` tag, where `[key]` is the key of the 
        font index file. See [PaperMC](https://docs.papermc.io/adventure/minimessage/format/#font){:target="_blank"} for more information.
        ??? example
            The namespace defaults to `minecraft`.  
            `<font:myfont>Written in my font.</font>`  
            `<font:custom:fancy>Written in fancy font from a custom namespace</font>`
    
    === "MineDown"
        
        To change the font used in MineDown messages, use the `font` key in the subsection.  
        The value of `font` is supposed to use the same key as the font index file.  
        For more information on the MineDown format, see [MineDown](https://wiki.phoenix616.dev/library/minedown/syntax){:target="_blank"}. 
        ??? example 
            `[My text with custom font](font=custom_font)`
