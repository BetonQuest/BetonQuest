package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces the package separator from '.' to '>' in events and conditions.
 */
public class PackageSeparator implements QuestMigration {
    /**
     * Empty default constructor.
     */
    public PackageSeparator() {
    }

    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        replace(config, "events", value -> true, value -> replaceSeparator(value, Pattern.compile("(conditions:)(\\S+)")));
        replace(config, "objectives", value -> true, value -> replaceSeparator(value, Pattern.compile("(events:)(\\S+)")));
        replace(config, "objectives", value -> true, value -> replaceSeparator(value, Pattern.compile("(events:)(\\S+)")));

        replaceSeperatorInList(config, "conversations", "*", "NPC_options", "*", "conditions");
        replaceSeperatorInList(config, "conversations", "*", "NPC_options", "*", "events");
        replaceSeperatorInList(config, "conversations", "*", "player_options", "*", "conditions");
        replaceSeperatorInList(config, "conversations", "*", "player_options", "*", "events");
        replaceSeperatorInList(config, "holograms", "*", "conditions");
        replaceSeperatorInList(config, "npc_holograms", "*", "conditions");
        replaceSeperatorInList(config, "effectlib", "*", "conditions");
        replaceSeperatorInList(config, "hide_npcs", "*");
        replaceSeperatorInList(config, "player_hider", "*", "source_player");
        replaceSeperatorInList(config, "player_hider", "*", "target_player");
        replaceSeperatorInList(config, "menus", "*", "slots", "*");
        replaceSeperatorInList(config, "menu_items", "*", "click");
        replaceSeperatorInList(config, "menu_items", "*", "click", "left");
        replaceSeperatorInList(config, "menu_items", "*", "click", "shiftLeft");
        replaceSeperatorInList(config, "menu_items", "*", "click", "right");
        replaceSeperatorInList(config, "menu_items", "*", "click", "shiftRight");
        replaceSeperatorInList(config, "menu_items", "*", "click", "middleMouse");
    }

    private String replaceSeparator(final String input, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(input);
        final StringBuilder stringBuffer = new StringBuilder();
        while (matcher.find()) {
            final String original = matcher.group(2);
            final String replaced = original.replace('.', '>');
            matcher.appendReplacement(stringBuffer, matcher.group(1) + Matcher.quoteReplacement(replaced));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private void replaceSeperatorInList(final MultiConfiguration config, final String... selection) throws InvalidConfigurationException {
        for (final String key : config.getKeys(true)) {
            if (!keyMatchesSelection(key.split("\\."), selection)) {
                continue;
            }
            if (!config.isString(key)) {
                continue;
            }
            final String value = config.getString(key, "");
            config.set(key, value.replace(".", ">"));
        }
    }

    @SuppressWarnings("PMD.UseVarargs")
    private boolean keyMatchesSelection(final String[] split, final String[] selection) {
        if (split.length != selection.length) {
            return false;
        }
        for (int i = 0; i < selection.length; i++) {
            final String select = selection[i];
            if (!("*".equals(select) || split[i].equals(select))) {
                return false;
            }
        }
        return true;
    }
}
