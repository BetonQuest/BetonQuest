# Frequently Asked Questions

If you have any questions please read it first. It's very likely that it has been already asked and answered. If not, feel free to ask us in the <a href="https://discordapp.com/invite/rK6mfHq" target="_blank">discord :fontawesome-brands-discord:</a> !.

***

**Q**: _Can you make conversation options clickable?_

**A**: Open _config.yml_ file and set `default_conversation_IO` option to "tellraw". You can also set it to "chest" if you want conversations to be displayed in an inventory GUI.

***

**Q**: _Can you add particles over NPCs' heads like in `Quests` plugin?_

**A**: Install [EffectLib](https://dev.bukkit.org/bukkit-plugins/effectlib/).

***

**Q**: _The players don't know they have to end a conversation, can you add "auto-ending" when they walk away?_

**A**: Set `stop` option to "false" in conversation file.

***

**Q**: _I have an error which says "Cannot load plugins/BetonQuest/{someFile}.yml", what is wrong?_

**A**: You have incorrect YAML syntax in your conversation file. Check it with [YAML Lint](http://yamllint.com) to see what's wrong. Usually it's because you started a line with `!` or `&`, forgot colons or made some weird things with apostrophes.

***

**Q**: _Where is a command for creating quests?_

**A**: There is no such command. BetonQuest is too complex to edit it with chat, commands and inventory windows. If you don't like editing files directly you can get [the editor](https://github.com/BetonQuest/BetonQuest-Editor).

***

**Q**: _Conversations are not working! I created NPC "Innkeeper" and he won't talk to me._

**A**: Conversations are not linked to an NPC through names, as you can have multiple Innkeepers. You need to connect them with their ID. Read [this](../User-Documentation/Reference.md#npcs).

***

**Q**: _Could you add some feature?_

**A**: Check if it wasn't already added in [DEV-builds](https://betonquest.org/old). You can see all changes in [the changelog](https://github.com/BetonQuest/BetonQuest/blob/master/CHANGELOG.md).
