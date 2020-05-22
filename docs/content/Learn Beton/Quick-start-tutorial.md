This section will teach you the very basics of BetonQuest.

## Checking out the "default" example quest. 

Let's start by checking out the build in example quest. You can find it in the BetonQuest directory that has been generated in your plugins folder.
The folder you are looking for is named "_default_".

Open it up and find a file called _main.yml_. It contains a lot of options but you only need to look at this sections:
```YAML
npcs:
  'Innkeeper': innkeeper
  '0': innkeeper
```
You will need to change the `'0'` to the ID of the Citizens NPC you want to learn Beton with. 
You obtain a NPC's ID by selecting it with **/npc sel** while looking at it and then running **/npc id**.   
Execute **/q reload** and right-click the NPC.

The conversation should start. If it did not, check if you correctly assigned the ID. Ask the Innkeeper for some quests.
He will tell you to cut some trees. If you want, type **/journal** to get the journal and see a new entry. Now, don't try to place any wood blocks.
BetonQuest will detect that and increase the number of blocks to destroy. Just go and find some trees. Cut them down and if you're in creative mode,
give yourself 16 blocks of wood. Now you can return to Innkeeper and give him the wood. You will receive the reward.

## Using events and conditions

Now that you know how a (very) simple quest looks like time to start learning how to write something similar. 
Let's start with events. We won't do conversations just now, since they heavily use events and conditions, 
so you need to know them first. You can read complete reference to events in the [Reference chapter](../User%20Documentation/Reference.md#events). Do that now or just continue with this tutorial.

### Events

Let's just open _events.yml_ file inside the _default_ package. At the end add a new line:

```YAML
mega: message Hello world!
```

This is an event instruction. BetonQuest will use it to determine what type of event it is and what exactly should it do. 
`mega` is the name, `message` is the events type and `Hello world!` tells the message event what it needs to display. In this case,
if you run `sayHello` event, it will display to you `Hello world!` message. Now save the file, issue **/q reload** command
and run the event with **/q e {name} mega** command (`q` is shortcut for `quest`, `e` is shortcut for `event`, `{name}`
is your Minecraft name without the brackets and `mega` is the name of the event we've just created). It should show you white `Hello world!` message in the chat.

Let's create another event, more complicated one. `teleport` seems complicated enough. As you can read in the [Events list](../User%20Documentation/Events-List.md),
it needs a single argument, the location. Press F3 and check out your current location (it's shown on the left, three numbers, `x`, `y` and `z`).
Now add in _events.yml_ another line:

```YAML
tp: teleport 100;200;300;world
```

and replace `100` with your `x` coordinate, `200` with `y` and `300` with `z`. `world` needs to be replaced with your 
current world's name. Save the file, reload the plugin (**/q reload**) and run this event with a command described
 before.
It should teleport you to the location you have specified.

Congratulations, you have just created your first events. Go ahead and do some experiments with other event types. 
You can find them in [Events list](../User%20Documentation/Events-List.md) chapter. Once you're done let's start learning conditions.

### Conditions

Open the _conditions.yml_ file and add there a new line:

```YAML
mega: location 100;200;300;world;5
```

Can you see how we named the `mega` condition in the same way as the `mega` event? They are not connected in any way.
Condition names and event names are separated, so you can give them the same name without any problems. Now let's
look at the instruction string. As you can suspect, `location` is a type of the condition. This one means that we'll be 
checking if the player is near that location (you should change the location to the place where you're standing right now,
so you don't have to run around the world). Note that at the end of location argument there is an additional number, `5`.
This is the maximum distance you can be away from the location to meet the condition. Alright, save the file and reload the plugin.

Now walk to the location you have defined in the condition. Try to stand on the exact block corresponding to that location. 
Issue **/q c {name} mega** command (`c` is shortcut for `condition`). It should show you "checking condition blah blah
blah: **true**". We're focusing on that last word, **true**. This means that you're meeting the condition: you're standing withing
5 block radius of the location. Now move 2 blocks away and issue that command again. You should still be meeting the condition. 
Walk 4 more blocks away and try now. It should show **false**. You are now outside of that 5 block radius. Get it? Great.

Now I'll show you the simplest use of those conditions. Open the _events.yml_ file again, and at the end of `mega` 
instruction add `conditions:mega` argument. By the way, rename your events to something that actually fits the type
of the event, otherwise it will get confusing really fast. Example:

!!! example "Example"
    === "events.yml"
        ```YAML
        sayHello: "message Hello world! conditions:isAtSpawn"
        ```
    === "conditions.yml"s
        ```YAML
        isAtSpawn: "location 100;200;300;world;5"
        ```
  


```YAML
baz: message Hello world! conditions:foo
```

Now `baz` event will run only if it meets `foo` condition. Reload the plugin, walk outside of the 5 block radius and try to run `baz` event. Puff, nothing happens. It's because you're not meeting `foo` condition. Walk into the radius again and try to run that event now. It should happily display the `Hello world!` message.

It's very nice that we can add such conditions, but the problem is: what if you wanted to display the message only if the player is _outside_ the radius? Don't worry, you don't have to specify `inverted_location` condition or anything like that. You can simply negate the condition. Negation makes the condition behave in the exact opposite way, in this case it `foo` will be met only if the player is outside of the 5 block radius, and it won't be met if he's inside. Open the _events.yml_ and add an exclamation mark before the `foo` condition, so it looks like

```YAML
baz: message Hello world! conditions:!foo
```

This means "display message `Hello world!` if the `foo` condition is _not met_". Save the file, reload the plugin and run the event inside and outside of the radius to see how it works.

## Basic tags

Now that you know how to use events and conditions I'll show you what tags are. Create new events:

```YAML
add_beton_tag: tag add beton
del_beton_tag: tag del beton
```

It's a good practice to give your events names that describe what they are doing. Imagine you have 100 events, `foo24`, `bar65`, `baz12` etc. You would get lost pretty quickly. So, `add_beton_tag` event here simply adds `beton` tag to the player, `del_beton_tag` removes it. Save the file, reload the plugin and run this event. Nothing happens... or does it? Issue **/q t {name}** command (`t` is shortcut for `tags`). It should show you a list with few entries. Right now focus on `default.beton`, the rest are used by the default quest for Innkeeper. Alright, `default` is the name of the package in which the tag is, and `beton` is the name of the tag, as defined in `add_beton_tag` event. Now run `del_beton_tag` event. Guess what, `default.beton` disappeared from the list! And that's it, you know how to add and remove tags. Pretty useless.

Nothing could be more wrong. Tags are one the most powerful things in BetonQuest. They just need to be used with `tag` condition. Open _conditions.yml_ and add

```YAML
has_beton_tag: tag beton
```

line. As you can imagine, `tag` is the type of a condition (the same as `tag` event, but these are not the same things - one is an event, the other one is a condition) and `beton` is the name of the tag. You don't have to specify `default.beton`, but you can if you want. Now save, reload and check it with a command. It should show **false**, since you have removed the tag with `del_beton_tag` event. Add it again with `add_beton_tag` event and check the `has_beton_tag` condition again. Now it will show **true**.

Now you probably understand how powerful this system is. You could for example set a tag on the first time the player talks with an NPC, and if the NPC sees that tag next time they talk, he will tell something different, like "welcome back".

## Creating objectives

Time to write some objectives! Open the _objectives.yml_ file and add a new line:

```YAML
kill_creepers: mobkill creeper 3 events:bar conditions:has_beton_tag
```

Now let's analyze it. `kill_creepers` is a name of the objective. `mobkill` is a type. In this case, to complete the objective the player will have to kill some mobs. `creeper` is a type of the mob, so we know that these mobs will have to be Creepers. `3` is the amount. It means that the player has to kill 3 Creepers. `events:bar` means than once the player kills those Creepers, the `bar` event will be run (it's the teleportation event). `conditions:has_beton_tag` tells us that the player will have to have `beton` tag while killing Creepers to complete the objective. Save it, reload the plugin and issue **/q o {name} add kill_creepers** command (`o` is for `objective`, `add` tells the plugin to add an objective).

Now you can check if you actually have this objective with **/q o {name}** command, it will show you all your active objectives. It should show `default.kill_creepers`. Alright, remove (yes, remove!) the `beton` tag from you and find some Creepers to kill. Once you killes 3 of them you will notice that nothing happened. It's because `has_beton_tag` condtion is not met, so the objective does not count your progress. Now add the tag again and kill another Creepers. When the third is dead you should be teleported to the location defined in `bar` event.

Congratulations, now you know how to use objectives. You should experiment with other types now, since objectives will be used very often in your quests. Once you're done check out the _Writing your first conversation_ chapter to use your knowledge to write your fisrt conversation.

## Writing your first conversation

Now that you have seen BetonQuest in action and understood events, conditions and objectives, it's time for writing your first conversation. There's a _conversations_ directory inside the default package. It contains a single file, _innkeeper.yml_. This is the conversation with Innkeeper, the one who asks you to cut some trees. Open it, we'll use that for reference. Now create a new file, let's say _miner.yml_. Now type (don't copy-paste it, you'll learn better while typing) that into the file:

```YAML
quester: Miner
first: greeting
NPC_options:
  greeting:
    text: Hi there, traveler!
```

It's the most basic conversation possible. The NPC named `Miner` upon starting the conversation will use `greeting` option, which means he will say `Hi there, traveler!`. Then the conversation will end, because there are no player options defined.

Now you need to link the conversation with an NPC. You do that in the _main.yml_ file. Open it now. As you can see, _inkeeper.yml_ conversation is linked to `Innkeeper` word. It's the one you have put on the sign, remember? Now, add another line under the Innkeeper: `Miner: miner.yml`, save the file and reload the server. This will link our new conversation with the NPC named "Miner". Construct a new NPC on the server, give him a sign with "Miner" name and click on the head.

Guess what, the conversation finished right after it started. The Miner just said `Hi there, traveler!`, as expected. Now go to the conversation file and edit it (again, manually, no copy-paste!) so the options look like this:

```YAML
NPC_options:
  greeting:
    text: Hi there, traveler!
    pointers: hello, bye
player_options:
  hello:
    text: Hello!
  bye:
    text: I need to go, sorry.
```

When you save the file, reload the plugin and start the conversation again you will notice that there are two options for you to choose: `Hello!` and `I need to go, sorry.` Choosing any of them will end the conversation, because these options did not specify any pointers.

Now add a new NPC option, for example `weather` with text `Nice weather.` and make `hello` player option point to it. When you save&reload, the Miner should say `Nice weather.` when you tell him `Hello!`. I think you get how it works.

```YAML
NPC_options:
  greeting:
    text: Hi there, traveler!
    pointers: hello, bye
  weather:
    text: Nice weather.
player_options:
  hello:
    text: Hello!
    pointer: weather
  bye:
    text: I need to go, sorry.
```

Now, every time you talk to the Miner, he will say the same thing. It would be nice if the second time you talk to him he knew your name. We can do that with tags. Define a `meet_miner` event and `has_met_miner` condition. When you talk to the Miner for the first time, he will check if you have met him. If not, he will meet you (with that event) and next time you talk, the condition will be passed and he will use your name.

Now, rename `greeting` NPC option to `first_greeting`. Add `meet_miner` event and negated `has_met_miner` condition (negated because this option should only show if the player has not met the Miner yet). You will need to surround the condition with `''`, because strings cannot start with exclamation marks in YAML. It should look like this:

```YAML
first: first_greeting
NPC_options:
  first_greeting:
    text: Hi there, traveler!
    condition: '!has_met_miner'
    event: meet_miner
    pointers: hello, bye 
```

This means: `first_greeting` should be used if the player **does not** pass `has_met_miner` condition (meaning he doesn't have a tag because he haven't talked to the NPC yet). When this option is used, it will fire `meet_miner` event and display `hello` and `bye` options. Alright, but what happens if the player met the Miner and now negated `has_met_miner` condition doesn't work? NPC will try to use next option defined in `first` setting. There is none yet, so let's add it.

```YAML
first: first_greeting, regular_greeting
NPC_options:
  regular_greeting:
    text: Hi %player%!
    pointers: hello, bye
```

This option does not have any conditions, so if the `first_greeting` fails, the NPC will always choose this one. Now take a look at the `%player%` thing. It's a variable. In this place it will show your name. There are more than this one, they are described in _Reference_ chapter. Alright, save&reload and start the conversation. If you did everything correctly, the Miner should greet you as a "traveler", and the second time you talk to him, he should greet you with your Minecraft name.

Here's the whole conversation you created, so you can check if you understood everything correctly:

```YAML
    first: first_greeting, regular_greeting
    NPC_options:
      first_greeting:
        text: Hi there, traveler!
        condition: '!has_met_miner'
        event: meet_miner
        pointers: hello, bye 
      regular_greeting:
        text: Hi %player%!
        pointers: hello, bye
      weather:
        text: Nice weather.
    player_options:
      hello:
        text: Hello!
        pointer: weather
      bye:
        text: I need to go, sorry.
```

Now you should experiment some more with this conversation, you can help yourself by looking at the [innkeeper.yml](https://github.com/Co0sh/BetonQuest/blob/master/BetonQuest-core/src/main/resources/default/defaultConversation.yml) file. Try to understand how that conversation works step by step. As the excercise you should complete the Miner NPC, so he asks you to mine some iron ore, then smelt it in the furnace, next craft an armor with it and return to him wearing this armor.

You might want to check out the [Reference](../User%20Documentation/Reference.md) chapter to see how to handle items in your quests and how to add entries to the journal. If you want to use Citizens NPCs instead of the ones made with clay you will find information you need in that chapter too. To find out more about events, conditions, objectives and variables, take a look at the appropriate lists (after the _Reference_ chapter).
