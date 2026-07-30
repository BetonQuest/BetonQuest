---
icon: material/notebook
---

# Journal

The journal is a quest item for persistent quest-related text.
It can contain manually added entries and an automatically generated main page.
Typical use cases are quest logs, investigation notes, discovered clues, story summaries, and compact status overviews.

Players can get the journal with:

```text
/journal
```

They can also open the quest backpack with `/backpack` and select the journal there, depending on your backpack
configuration.
The journal is a quest item, so it is protected from normal storage behavior such as putting it into chests or item
frames.
If a player drops the journal, it safely returns to the backpack.

## Sections

Journal content is configured with two package sections:

- `journal` defines entries that can be added to or removed from a player's journal.
- `journal_main_page` defines conditional text for the first journal page.

Entries are stored per player when the `journal add` action is executed.
The entry content itself remains configuration-driven: if the configured text is changed and BetonQuest is reloaded,
players who already have that entry will see the updated text after the journal is refreshed.

```yaml
journal:
  mine_started:
    en-US: "&0The miner asked me to inspect the old mine."
    de-DE: "&0Der Bergarbeiter bat mich, die alte Mine zu untersuchen."
  mine_finished:
    en-US: "&0I inspected the mine and reported back to the miner."
    de-DE: "&0Ich habe die Mine untersucht und dem Bergarbeiter Bericht erstattet."

journal_main_page:
  mine_active:
    priority: 10
    text:
      en-US: "&6Active quest:&0 Inspect the old mine."
      de-DE: "&6Aktive Quest:&0 Untersuche die alte Mine."
    conditions: "mineStarted,!mineFinished"
```

`journal` entries remain assigned to the player until they are deleted.
`journal_main_page` entries are condition-based and are recalculated when the journal is refreshed.

## Actions

Journal entries are managed with the `journal` action:

```yaml
actions:
  addMineEntry: "journal add mine_started"
  deleteMineEntry: "journal delete mine_started"
  refreshJournal: "journal update"
```

Available operations:

- `journal add <entry>` adds an entry from the `journal` section.
- `journal delete <entry>` removes an entry from the player's journal.
- `journal update` refreshes the journal, especially the `journal_main_page`.

The `givejournal` action gives the player the journal item:

```yaml
actions:
  startMineQuest: "folder addMineStartedTag,addMineEntry,giveJournal,refreshJournal"
  addMineStartedTag: "tag add mine_started"
  addMineEntry: "journal add mine_started"
  giveJournal: "givejournal"
  refreshJournal: "journal update"
```

`givejournal` works like the `/journal` command.

## Entry lifecycle

Journal entries are not replaced automatically.
Quest packages should explicitly delete outdated entries and add the next relevant entry when quest state changes.

```yaml
actions:
  startMineQuest: "folder addStartedTag,addStartedEntry,giveJournal,refreshJournal"
  addStartedTag: "tag add mine_started"
  addStartedEntry: "journal add mine_started"

  finishMineQuest: "folder addFinishedTag,replaceStartedEntry,rewardPlayer,refreshJournal"
  addFinishedTag: "tag add mine_finished"
  replaceStartedEntry: "folder deleteStartedEntry,addFinishedEntry"
  deleteStartedEntry: "journal delete mine_started"
  addFinishedEntry: "journal add mine_finished"
  rewardPlayer: "give reward:3"
  refreshJournal: "journal update"

conditions:
  mineStarted: "tag mine_started"
  mineFinished: "tag mine_finished"

items:
  reward: "simple EMERALD"
```

This pattern keeps one relevant detail entry in the journal while the main page can show the current state.

## Main page

The main page is built from the `journal_main_page` section.
Each entry can have:

- `priority`: controls ordering; lower numbers are shown first.
- `text`: the text to display.
- `conditions`: optional conditions that must be true.

```yaml
journal_main_page:
  title:
    priority: 1
    text:
      en-US: "&0Quest Overview\n"
      de-DE: "&0Quest Uebersicht\n"

  mine_not_started:
    priority: 10
    text:
      en-US: "&4[ ] Old Mine - not started"
      de-DE: "&4[ ] Alte Mine - nicht begonnen"
    conditions: "!mineStarted,!mineFinished"

  mine_active:
    priority: 11
    text:
      en-US: "&6[>] Old Mine - inspect the entrance"
      de-DE: "&6[>] Alte Mine - untersuche den Eingang"
    conditions: "mineStarted,!mineFinished"

  mine_done:
    priority: 12
    text:
      en-US: "&2[x] Old Mine - completed"
      de-DE: "&2[x] Alte Mine - abgeschlossen"
    conditions: "mineFinished"
```

Call `journal update` after changing tags, points, or other data used by main page conditions when the visible status
should refresh immediately.

## Translations

Journal entries and main page text can use the same language-key structure as conversations.
Players choose their language with `/questlang`.
If a translation is missing, BetonQuest falls back to the default language configured in `config.yml`.

```yaml
journal:
  delivery_started:
    en-US: "&0Deliver the letter to Stone Village."
    de-DE: "&0Bring den Brief nach Steindorf."

journal_main_page:
  delivery_status:
    priority: 10
    text:
      en-US: "&6Active quest:&0 Deliver the letter."
      de-DE: "&6Aktive Quest:&0 Liefere den Brief."
    conditions: "deliveryStarted,!deliveryDone"
```

The default language should be defined for every translated entry.
This prevents missing text when a player uses a language that has no specific translation for that entry.

## Formatting text

Journal text supports color codes.
Use `\n` for a new line inside a string:

```yaml
journal:
  mine_started: "&6Old Mine\n&0Inspect the entrance and report back."
```

Use the pipe character `|` to manually move text to a new journal page:

```yaml
journal:
  investigation_notes: "&0First page of notes.|&0Second page of notes."
```

Long lines can overflow depending on the configured journal format.
Shorten the text or adjust the journal format settings in `config.yml` if page content does not fit.

## Placeholders

Journal text can use BetonQuest placeholders.
They are resolved when the journal is created or refreshed.

```yaml
journal_main_page:
  reputation:
    priority: 30
    text: "&6Reputation:&0 %point.reputation.amount%"
```

Use `journal update` when placeholder values or main page conditions change and the player should see the new value
without reopening or receiving the journal again.

## Admin commands

Administrators can inspect or modify journal entries with `/bq journal`:

```text
/bq journal Steve list
/bq journal Steve add my_package>mine_started
/bq journal Steve del my_package>mine_started
```

When adding an entry manually, a date can be appended with an underscore between date and time:

```text
/bq journal Steve add my_package>mine_started 23.04.2014_16:52
```

The entry ID is package-prefixed, so use `my_package>entry_name` when running commands.


## Usage guidelines

Use journal entries for text the player should be able to reread later.
Use `notify` for immediate feedback and short reminders.

Good uses for journal entries:

- quest summaries
- current task details
- investigation notes
- discovered clues
- completed quest records

Good uses for the main page:

- active quest overview
- quest status list
- reminders based on tags or points
- progress values using placeholders

Keep journal entries short.
Minecraft books have limited space, and long lines can be hard to read.
