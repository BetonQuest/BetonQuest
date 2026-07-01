---
icon: material/sword-cross
---

# How to create complex quest items

Complex quest items are useful when an item should look unique, carry quest information, and stay protected from normal
player interaction.
The examples below only define items.
You can give them with `/bq give <package>><item>` or use them in actions, menus, conditions, conversations, or backpacks.

The basic idea is:

- Use YAML multiline syntax for long item definitions.
- Use `name` and `lore` to control colors and remove unwanted italics.
- Use `quest-item` when the item should be protected and stored in the backpack.
- Use flags to hide technical item data from players.
- Use custom model data or item models when a resource pack should change the appearance.

=== "Custom Weapon"

    This item is a persistent quest weapon with custom text, enchantments, hidden attributes, and custom model data.

    ```yaml
    items:
      cryptBlade: >-
        simple DIAMOND_SWORD
        "name:<!i><dark_purple>Crypt Blade"
        "lore:<!i><gray>A silent blade from the sealed crypt."
        "lore:<!i><red>Only useful while the crypt seal is broken."
        enchants:damage_all:3
        unbreakable
        custom-model-data:31001
        flags:HIDE_ENCHANTS,HIDE_ATTRIBUTES,HIDE_UNBREAKABLE
        quest-item
    ```

    Important parts:

    - Folded multiline syntax keeps long item definitions readable.
    - `enchants:damage_all:3` adds Sharpness 3 to the sword.
    - `unbreakable` makes the item unbreakable.
    - `custom-model-data:31001` allows a resource pack to replace the item's model.
    - `flags:HIDE_ENCHANTS,HIDE_ATTRIBUTES,HIDE_UNBREAKABLE` hides technical tooltip lines.
    - `quest-item` marks the item as protected and allows BetonQuest to store it in the backpack.

=== "Written Book"

    This item is a written quest book with a title, author, multiple pages, and protected quest-item behavior.

    ```yaml
    items:
      fieldReport: >-
        simple WRITTEN_BOOK
        "name:<!i><gold>Field Report"
        "title:Field Report"
        "author:The Archivist"
        "text:<dark_green>Field Report<newline><newline><black>The eastern gate is damaged. Speak with the guard captain before entering the ruins.|<black>Known risks:<newline>- unstable stone<newline>- hostile skeletons<newline>- missing scout team"
        quest-item
    ```

    Important parts:

    - Books can use the same display name options as other items.
    - `title` and `author` set the written book metadata.
    - The `|` character starts a new page.
    - `<newline>` creates a new line on the same page.
    - `quest-item` makes the book protected instead of a normal book players can freely lose.

=== "Custom Potion"

    This item is a custom potion with a display name, lore, a vanilla potion type, and additional potion effects.

    ```yaml
    items:
      scoutPotion: >-
        simple POTION
        "name:<!i><aqua>Scout's Focus"
        "lore:<!i><gray>Issued to scouts before entering hostile terrain."
        type:strength
        upgraded:false
        extended:false
        effects:speed:1:45,night_vision:1:60
        flags:HIDE_POTION_EFFECTS
        quest-item
    ```

    Important parts:

    - Potions can combine normal item arguments with potion-specific arguments.
    - `type:strength` sets the vanilla potion type.
    - `effects:speed:1:45,night_vision:1:60` adds custom effects using `effect:power:duration`.
    - Effect power must be a positive integer.
    - `flags:HIDE_POTION_EFFECTS` hides the technical potion effect list.
    - `quest-item` protects the potion as a quest item.

=== "Armor and Head"

    These items show a colored leather armor piece and a player head.
    This is useful for disguises, faction uniforms, trophies, or menu icons.

    ```yaml
    items:
      rangerChestplate: >-
        simple LEATHER_CHESTPLATE
        "name:<!i><green>Ranger Tunic"
        "lore:<!i><gray>Standard issue for forest patrols."
        color:#2f8f4e
        unbreakable
        flags:HIDE_DYE,HIDE_ATTRIBUTES,HIDE_UNBREAKABLE
        quest-item

      archivistHead: >-
        simple PLAYER_HEAD
        "name:<!i><yellow>Archivist Token"
        "lore:<!i><gray>A personal quest token."
        owner:Alex
        quest-item
    ```

    Important parts:

    - Leather armor supports the normal item arguments plus a color.
    - `color:#2f8f4e` uses a hexadecimal RGB color for the leather armor.
    - Player heads can be used as quest items or menu icons.
    - `owner:Alex` sets the player skin used by the head without requiring a player-dependent placeholder.
