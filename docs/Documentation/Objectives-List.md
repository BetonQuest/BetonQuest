# Objectives List

## Action: `action`

This objective completes when the player clicks on the given block type. The first argument is the type of the click,
it can be right, left or any. Next is a [Block Selector](./Reference.md#block-selectors) or `any` if you
want to count all clicks, even into the air. You can also specify the `loc:` argument, followed by the standard location
format and the `range:` followed by a number (or variable). The specified location is the center of a sphere, the range it's radius.
Therefore, these arguments define where the clicked block needs to be, as opposed to "where you must be" in location condition.
If you add the argument `cancel`, the click will be canceled (chest will not open, button will not be pressed etc.).
This objective works great with the location condition and the item in hand condition to further limit the counted clicks.
One could make a magic wand using this.

The objective contains one property, `location`. It's a string formatted like `X: 100, Y: 200, Z:300`. It does not
show the radius.

!!! example
    ```YAML
    action right DOOR conditions:holding_key loc:100;200;300;world range:5
    action any any conditions:holding_magicWand events:fireSpell #Custom click listener for a wand
    ```

## Arrow Shooting: `arrow`

To complete this objective the player needs to shoot the arrow into the target. There are two arguments, location of the
target and precision number (radius around location where the arrow must land, should be small). Note that the position
of an arrow after hit is on the wall of a _full_ block, which means that shooting not full blocks (like heads) won't
give accurate results. Experiment with this objective a bit to make sure you've set the numbers correctly.

!!! example
    ```YAML
    arrow 100.5;200.5;300.5;world 1.1 events:reward conditions:correct_player_position
    ```

## Block: `block`

To complete this objective the player must break or place the specified amount of blocks. The first argument is a
[Block Selector](./Reference.md#block-selectors). Next is amount. It can be more than 0 for placing and
less than 0 for destroying. You can also use the `notify` keyword to display messages to the player each time he updates
amount of blocks, optionally with the notification interval after colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of blocks already done,
`left` is the amount of blocks still needed to complete the objective and `total` is the amount of blocks initially
needed.
Note that it follows the same rules as the amount argument, meaning that blocks to break are a negative number!

!!! example
    ```YAML
    block LOG -16 events:reward notify:5
    ```

## Breed animals: `breed`

This objective is completed by breeding animals of specified type. The first argument is the
[animal type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) and the second argument is the
amount (positive integer). You can add the `notify` argument to display a message with the remaining amount each time
the animal is bred, optionally with the notification interval after a colon. While you can specify any entity, the
objective will be completable only for breedable ones.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of animals already breed,
`left` is the amount of animals still needed to breed and `total` is the amount of animals initially required.

!!! example
    ```YAML
    breed cow 10 notify:2 events:reward
    ```

## Put items in a chest: `chestput`

This objective requires the player to put specified items in a specified chest. First argument is a location of the
chest, second argument is a list of items (from _items.yml_ file), separated with a comma. You can also add amount of
items after a colon. The items will be removed upon completing the objective unless you add `items-stay` optional
argument.

!!! example
    ```YAML
    chestput 100;200;300;world emerald:5,sword events:tag,message
    ```

## Eat/drink: `consume`

This objective is completed by eating specified food or drinking specified potion. The only required argument is the ID
of an item from _items.yml_.

!!! example
    ```YAML
    consume tawny_owl events:faster_endurance_regen
    ```

## Crafting: `craft`

To complete this objective the player must craft specified item. First argument is ID of the item, as in _items.yml_.
Next is amount (integer). You can use the `notify` keyword to display a message each time the player advances the
objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already crafted,
`left` is the amount of items still needed to craft and `total` is the amount of items initially required.

!!! example
    ```YAML
    craft saddle 5 events:reward
    ```

## Enchant item: `enchant`

This objectie is completed when the player enchants specified item with specified enchantment. The first argument is an
item name, as defined it _items.yml_. Second one is the
[enchantment](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html) and a level, separated
with a colon. If you need to check for multiple enchantments you can add a list of them, separated by colons.

!!! example
    ```YAML
    enchant sword damage_all:1,knockback:1 events:reward
    ```

## Experience: `experience`

This objective can by completed by reaching specified amount of experience points. You can check for whole levels by
adding the `level` argument. The conditions are checked when the player levels up, so if they are not met the first
time, the player will have to meet them and levelup again. You can use the `notify` keyword to display a message each
time the player advances the objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the current amount of experience,
`left` is the amount of experience still needed and `total` is the amount of experience required.

!!! example
    ```YAML
    experience 25 level events:reward
    ```

## Delay: `delay`

This objective is just a long, persistent delay for firing events. It will run only after certain amount of time
(measured in minutes) and only when player is online and meets all conditions. If a player is offline at that time it
will just wait for them to log in. You should use it for example to delete tags so the player can complete quests
multiple times. First argument is time, by default in minutes. You can also use `ticks` or `seconds` argument to use
different units, but keep in mind that it's not very precise - it will complete roughly after the time ends. To control
that precision you can specify an optional `interval:` argument, which specifies how many ticks should pass between
checks. One second is 20 ticks. Less makes the objective more precise, at the expense of performance. The rest is
just like in other objectives.

Delay has two properties, `left` and `date`. The first one will show how much time needs to pass before the delay is
completed (i.e. `23 days, 5 hours and 45 minutes`), the second one will show a date of completing the objective
formatted using `date_format` setting in _config.yml_ (it will look like the one above every journal entry).

!!! example
    ```YAML
    delay 1000 ticks interval:5 events:event1,event2
    ```

## Death: `die`

Death objective completes when the player dies meeting all conditions. You can optionally cancel death with `cancel`
argument. It will heal player and optionally teleport him to respawn location. There can be two arguments: `cancel`,
which is optional, and `respawn:`, which is also optional and only used if there is the `cancel` argument set. You can
add them right after type of objective.

!!! example
    ```YAML
    die cancel respawn:100;200;300;world;90;0 events:teleport
    ```

## Fishing: `fish`

Requires the player to catch something with the fishing rod. It doesn't have to be a fish, it can also be a treasure or
junk. The first argument is a [Block Selector](./Reference.md#block-selectors) of the item to catch.
Second argument must be the amount of fish to catch. You can also add the `notify` argument if you want to display
progress, optionally with the notification interval after a colon.

The fish objective has three properties: `left` is the amount of fish still left to be caught, `amount` is the amount of
already caught fish and `total` is the initially required amount of fish needed to be caught.

!!! example
    ```YAML
    fish SALMON 5 notify events:tag_fish_caught
    ```

## Interact with entity: `interact`

The player must click on an entity to complete this objective. The first argument is the type of a click.
Available values are `right`, `left` and `any`.
Second required argument is the [mob type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).
Next is an amount of mobs required to click on. These must be unique, so the player can't simply click twenty times on
the same zombie to complete it. There is also an optional `name:` parameter which specifies what custom name the entity must have
(you need to write `_` instead of the space character). To check for the real name (e.g. if you renamed players to include
their rank) you can also use `realname:` instead.
Add `marked:` if the clicked entity needs to be marked by the `spawn` event (see its description for marking explanation). 
You can also add `notify` argument to make the objective notify players whenever they click a correct entity,
optionally with the notification interval after colon and `cancel` if the click shouldn't do what it usually does
(i.e. left click won't hurt the entity). This can be limited with an optional `loc` and `range` attribute to limit within a range of a location.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of entities already interacted
with, `left` is the amount of entities still needed to be interacted with and `total` is the amount of entities
initially required.

!!! example
    ```YAML
    interact right creeper 1 marked:sick condition:syringeInHand cancel
    ```

## Kill player: `kill`

To complete this objective the player needs to kill another player. The first argument is amount of players to kill.
You can also specify additional arguments: `name:` followed by the name will only accept killing players with this name,
`required:` followed by a list of conditions separated with commas will only accept killing players meeting these conditions
and `notify` will display notifications when a player is killed, optionally with the notification interval after a colon.

The kill objective has three properties: `left` is the amount of players still left to kill, `amount` is the amount of
already killed players and `total` is the initially required amount to kill.

!!! example
    ```YAML
    kill 5 required:team_B
    ```

## Location: `location`

This objective completes when player moves in specified range of specified location and meets all conditions. The first
argument after objective's name must be location, the second - radius around the location. It can be a variable.

Location objective contains one property, `location`. It's a string formatted like `X: 100, Y: 200, Z:300`.

!!! example
    ```YAML
    location 100;200;300;world 5 condition:test1,!test2 events:test1,test2
    ```

## Login: `login`

To complete this objective the player simply needs to login to the server.
If you use `global` this objective will be also completed directly when the player joins the first time.
If you use `persistent` it will be permanent.
Don't forget that if you use global and persistent you can still remove the objective explicitly.

!!! example
    ```YAML
    login events:welcome_message
    ```

## Logout: `logout`

To complete this objective the player simply needs to leave the server. Keep in mind that running a `folder` event here
will make it run in "persistent" mode, since the player is offline on the next tick.

!!! example
    ```YAML
    logout events:delete_objective
    ```

## Password: `password`

This objective requires the player to write a certain password in chat. All attempts of a player will be hidden from public chat.
The password consists of a prefix followed by the actual secret word:
```
Solution: The Cake is a lie!     
^prefix   ^secret word(s)
```

The objective's instruction string is defined as follows:

1. The first argument is the password, use underscore characters (`_`) instead of spaces.
   The password is a [regular expression](https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285).
   They are a little complicated but worth the effort if you want more control over what exactly matches. 
   Websites like [regex101.com](https://regex101.com/) help with that complexity though.
   The offical [documentation](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/regex/Pattern.html#sum) for regular expressions
   in Java might also help you.
   If you don't want to get into them just write down the password but keep in mind that the players answer needs to be an exact match! 

2. The prefix can be changed: The default (when no prefix is set) is the translated prefix from the *messages.yml* config in the user's language.             
   Note that every custom prefix is suffixed with `:⠀`, so `prefix:Library_password` will require the user to enter `Library password: myfancypassword`.     
   To disable the prefix use an empty `prefix:` declaration, e.g. `password myfancypassword prefix: events:success`.
   Be aware of these side effects that come with disabling the prefix:
    
    * Nothing will be hidden on failure, so tries will be visible in chat and commands will get executed!
    * If a command was used to enter the password, the command will not be canceled on success and thus still be executed!    
    * This ensures that even if your password is `quest` you can still execute the `/quest` command. 
   
3. You can also add the `ignoreCase` argument if you want a password's capitalization to be ignored. This is especially important for regex matching.

4. If you want to trigger one or more events when the player failed to guess the password you can use the argument `fail` with a list of events (comma separated).
   With disabled prefix every command or chat message will trigger these events!

!!! example
    ```YAML
    password beton ignoreCase prefix:secret fail:failEvent1,failEvent2 events:message,reward
    ```

## Pickup item: `pickup`

To complete this objective you need to pickup the specified amount of items. 
The first argument must be the internal name of an item defined in `items.yml`. This can also be a comma-separated list of multiple items.
You can optionally add the `amount:` argument to specify how many of these items the player needs to pickup. 
This amount is a total amount though, it does not count per each individual item. You can use the `notify` keyword to
display a message each time the player advances the objective, optionally with the notification interval after a colon.

You can also add the `notify` keyword to display how many items are left to pickup.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already picked up,
`left` is the amount of items still needed to pick up and `total` is the amount of items initially required.

!!! example
    ```YAML
    pickup emerald amount:3 events:reward notify
    pickup emerald,diamond amount:6 events:reward notify
    ```

## Mob Kill: `mobkill`

The player must kill specified amount of mobs You must specify mob type first and then amount. You can find possible mob
types here: [mob types](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html). Additionally you
can specify names for mobs with `name:Uber_Zombie`, so only killing properly named mobs counts. All `_` are replaced
with spaces, so in this example you would have to kill 5 zombies with "Uber Zombie" above their heads. You can also
specify `notify` keyword to display messages to the player each time he kills a mob, optionally with the notification
interval after colon. If you want to accept only mobs marked with `spawn` event, use `marked:` argument followed by the
keyword used in that event.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of mob already killed,
`left` is the amount of mobs still needed to kill and `total` is the amount of mobs initially required.

!!! example
    ```YAML
    mobkill ZOMBIE 5 name:Uber_Zombie conditions:night
    ```

## Potion brewing: `brew`

To complete this objective the player needs to brew specified amount of specified potions.
The first argument is a potion ID from _items.yml_. Second argument is amount of potions.
You can optionally add `notify` argument to make the objective display progress to players,
optionally with the notification interval after a colon.

Progress will be counted for the player who last added or changed an item before the brew process completed. Only newly
created potions are counted.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of potions already brewed,
`left` is the amount of potions still needed to brew and `total` is the amount of potions initially required.

!!! example
    ```YAML
    brew weird_concoction 4 event:add_tag
    ```

## Sheep shearing: `shear`

To complete this objective the player has to shear specified amount of sheep, optionally with specified color and/or
name. The first, required argument is amount (integer). Optionally, you can add a `name:` argument to only count specific sheep.
All underscores will be replaced by spaces - if you want to use underscores, put a `\` before them.
You can also check for the sheep's `color:` using these [color names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html).
You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of sheep already sheared,
`left` is the amount of sheep still needed to shear and `total` is the amount of sheep initially required.

!!! example
    ```YAML
    shear 1 name:Bob color:black
    shear 1 name:jeb\_
    "shear 1 name:jeb\\_" #Use two backslashes if quoted
    ```

## Smelting: `smelt`

To complete this objective the player must smelt a specified item. Note that you must define the output item, not the
ingredient. The first argument is a [Block Selector](./Reference.md#block-selectors) for the output
item. The second is the amount (integer). You can use the `notify` keyword to display a message each time the player
advances the objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already smelted,
`left` is the amount of items still needed to smelt and `total` is the amount of items initially required.

!!! example
    ```YAML
    smelt IRON_INGOT 5 events:reward
    ```

## Step on pressure plate: `step`

To complete this objective the player has to step on a pressure plate at a given location. The type of plate does not
matter. The first and only required argument is a location. If the pressure plate is not present at that location, the
objective will not be completable and will log errors in the console.

Step objective contains one property, `location`. It shows the exact location of the pressure plate in a string
formatted like `X: 100, Y: 200, Z:300`.

!!! example
    ```YAML
    step 100;200;300;world events:done
    ```

## Taming: `tame`

To complete this objective player must tame some amount of mobs. First argument is type, second is amount. The mob must
be tameable for the objective to be valid, e.g. on 1.16.5: `CAT`, `DONKEY`, `HORSE`, `LLAMA`, `PARROT` or `WOLF`. You
can use the `notify` keyword to display a message each time the player advances the objective, optionally with the
notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of animals already tamed,
`left` is the amount of animals still needed to tame and `total` is the amount of animals initially required.

!!! example
    ```YAML
    tame WOLF 2 events:wolfs_tamed
    ```
   

## Player must Jump: `jump`
**:fontawesome-solid-tasks:{.task} Objective  ·  :fontawesome-solid-paper-plane: Requires [Paper](https://papermc.io)**

To complete this objective the player must jump. The only argument is amount. You can use the `notify` keyword to display a
message each time the player advances the objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of jumps already done,
`left` is the amount of jumps still needed and `total` is the amount of jumps initially required.

!!! example
    ```YAML
    jump 15 events:legExerciseDone
    ```

## Ride an entity: `ride`

This objective can be completed by riding the specified
<a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html" target="_blank_">entity</a>.
`any` is also a valid input and matches any entity.

!!! example
    ```YAML
    ride horse
    ride any
    ```

## Run a Command: `command`

To complete this objective the player must execute a specified command. It can be both an existing or a new, custom
command. The first argument is the command text. Use `_` in place of spaces for the command. If you need an actual `_`
in your command, you must escape it using a backslash (`\`, see example below). The command argument is case-sensitive
and also supports using placeholders. The second required argument is a list of events to execute when the objective is
met.

!!! example
    ```YAML
    command /warp_%player%_farms events:event1,event2
    command //replace_oak\_wood events:event1,event2
    ```

With this configuration, the command objective requires the player to execute `/warp MyName farms` to be completed. The
command objective matches from the start of the command that was executed, therefore if the player executed
`/warp MyName farms other arguments` it would still be completed.

Optional arguments:

* `ignoreCase`: If provided, instructs the objective to ignore case for the command to match.
* `exact`: If provided, requires an exact command match, not just the command start.
* `cancel`: If provided, the objective will cancel the execution of the command on a match. This needs to be enabled to suppress the `Unknown Command` message when using custom commands.
* `failEvents`: If provided, specifies a list of events to execute if a non-matching command is run and conditions are met.

!!! complex example
    ```YAML
    command /warp_%player%_farms ignoreCase exact cancel failEvents:failEvent1,failEvent2 events:event1,event2
    ```

!!! warning
    Sometimes you want to use actual underscores in your command. These will however be replaced with spaces by default.
    You can "escape" them using backslashes:
    One backslash (`\`) is required when using no quoting at all (`...`) or single quotes
    (`'...'`). Two backslashes are required (`\\`) when using double quotes (`"..."`).

    Examples:<br>
    `eventName: command /enchant_@s_minecraft:aqua_affinity` :arrow_right: `eventName:command /enchant_@s_minecraft:aqua{++\++}_affinity`<br>
    `eventName: {=='==}command /enchant_@s_minecraft:aqua_affinity{=='==}` :arrow_right: `eventName: {=='==}command /enchant_@s_minecraft:aqua{++\++}_affinity{=='==}`<br>
    `eventName: {=="==}command /enchant_@s_minecraft:aqua_affinity{=="==}` :arrow_right: `eventName: {=="==}command /enchant_@s_minecraft:aqua{++\\++}_affinity{=="==}`<br>

## Equip Armor Item: `equip`
**:fontawesome-solid-tasks:{.task} Objective  ·  :fontawesome-solid-paper-plane: Requires [Paper](https://papermc.io)**

The player must equip the specified quest item in the specified slot.
The item must be any quest item as defined in _items.yml_.
Available slot types: `HEAD`, `CHEST`, `LEGS`, `FEET`.

```YAML
equip HEAD amazing_helmet events:event1,event2
equip CHEST amazing_armor events:event1,event2
```

## Variable: `variable`

This objective is different. You cannot complete it, it will also ignore defined events and conditions. You can start it and that's it.
While this objective is active though, everything the player types in chat (and matches special pattern) will become a variable.
The pattern is `key: value`. So if you type that, it will create a variable called `key`, which will resolve to `value` string.
These are not global variables, you can access them as objective properties. Let's say you defined this objective as `var` in your _objectives.yml_ file.
You can access the variable in any conversation, event or condition with `%objective.var.key%` - and in case of this example, it will resolve to `value`.
The player can type something else, and the variable will change its value. Variables are per-player, so my `key` variable 
will be different from your `key` variable, depending on what we were typing in chat. You can have as much variables here as you want.
To remove this objective use `objective delete` event - there is no other way.

You can also use `variable` event to change variables stored in this objective. There is one optional argument, `no-chat`. If you use it, the objective won't be modified by what players type in chat.

!!! example
    ```YAML
    variable
    ```
