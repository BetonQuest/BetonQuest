---
icon: material/notebook
---

The journal is a book which can be used to display any quest related information in an immersive way.

## Basic Information

The journal can be obtained with the **/journal** command or by selecting it from the quest item backpack (**/backpack**).
It's a quest item, so you cannot put it into any chests, item frames and so on.
If you ever feel the need to get rid of your journal: Just drop it! It will safely return to your backpack.

The journal is updated with the `journal` action, based on the text entries written inside a _journal_ section.
The entries can use color codes, but the color will be lost between pages. 
If you update these texts and reload the plugin, all players' journals will reflect changes. 

If you want to translate the entry do the same thing as with conversation options - go to new line,
add language ID and the journal text for every language you want to include.

## Main Page
You can also add a main page to the journal. It's a list of texts, which will show only if specified conditions are met.
You can define them in the `journal_main_page` section:

```YAML
journal_main_page:
  title:
    priority: 1
    text:
      en-US: '&eThe Journal'
      pl-PL: '&eDziennik'
    conditions: 'quest_started,!quest_completed'
```

Each string can have text in different languages, list of conditions separated by commas (these must be met for
the text to show in the journal) and `priority`, which controls the order of texts.
You can use conversation placeholders in the texts, but they will only be updated when the player gets his journal with
the **/journal** command.
Color codes are supported.

If you want your main page take a separate page (so entries will be displayed on next free page), set `full_main_page` in "_config.yml_" to "true".
If you want to manually wrap the page, use the pipe `|` character. Use \n to create a new line.

## Configuration
You can control behavior of the journal in "_config.yml_" file, in the `journal` section.
`chars_per_line` and `lines_per_page` specifies how many characters will be placed on a single page.
If you set it too high, the text will overflow outside the page, too low, there will be too many pages.
`one_entry_per_page` allows you to place every entry on a single page.
`reversed_order` allows you to reverse order of entries and `hide_date` lets you remove the date from journal entries.

The journal by default appears in the last slot of the hotbar.
If you want to change that use `default_journal_slot` option in "_config.yml_", experiment with different settings until you're ok with it.

You can control colors in the journal in `journal_colors` section in "_config.yml_": `date` is a color of date of every
entry, `line` is a color of lines separating entries and `text` is just a color of a text. You need to use standard
color codes without `&` (eg. `'4'` for dark red).
