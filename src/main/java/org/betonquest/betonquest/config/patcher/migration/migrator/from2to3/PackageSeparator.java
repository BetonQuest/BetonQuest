package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces the package separator from '.' to '>' in certain places.
 */
public class PackageSeparator implements QuestMigration {
    /**
     * Empty default constructor.
     */
    public PackageSeparator() {
    }

    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.NcssCount"})
    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceSeparatorSimple(config, "events", "( conditions:)(\\S+)");
        replaceSeparatorSimple(config, "objectives", "( events:)(\\S+)");
        replaceSeparatorSimple(config, "objectives", "( conditions:)(\\S+)");

        replaceSeparatorSimple(config, "conditions", "(and )(\\S+)");
        replaceSeparatorSimple(config, "cónditions", "(armor )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(chestitem \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(conversation )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(globalpoint )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(globaltag )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(hand )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(inconversation conversation:)(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(item )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(journal )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(npcdistance )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(npclocation )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(objective )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(or )(\\S+)");
        replace(config, "conditions", value -> value.startsWith("party "),
                value -> replaceSeparatorMultiple(value, "(party \\S+ )(\\S+)", "( every:)(\\S+)", "( any:)(\\S+)"));
        replaceSeparatorSimple(config, "conditions", "(point )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(stage )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(tag )(\\S+)");

        replaceSeparatorSimple(config, "events", "(cancel )(\\S+)");
        replaceSeparatorSimple(config, "events", "(chestgive \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "events", "(chesttake \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "events", "(compass \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "events", "(conversation )(\\S+)");
        replaceSeparatorSimple(config, "events", "(deleteglobalpoint )(\\S+)");
        replaceSeparatorSimple(config, "events", "(deletepoint )(\\S+)");
        replace(config, "events", value -> value.startsWith("drop "),
                value -> replaceSeparator(value, "( items:)(\\S+)"));
        replace(config, "events", value -> value.startsWith("folder "),
                value -> replaceSeparatorMultiple(value, "(folder )(\\S+)", "( cancelconditions:)(\\S+)"));
        replaceSeparatorSimple(config, "events", "(first )(\\S+)");
        replaceSeparatorSimple(config, "events", "(give )(\\S+)");
        replaceSeparatorSimple(config, "events", "(globaltag \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "events", "(globalpoint )(\\S+)");
        replace(config, "events", value -> value.startsWith("if "),
                value -> replaceSeparatorMultiple(value, "(if \\S+ )(\\S+)", "(else )(\\S+)"));
        replaceSeparatorSimple(config, "events", "(journal \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "events", "(npcteleport )(\\S+)");
        replaceSeparatorSimple(config, "events", "(objective \\S+ )(\\S+)");
        replace(config, "events", value -> value.startsWith("party "),
                value -> replaceSeparatorMultiple(value, "(party \\S+ )(\\S+)", "(party \\S+ \\S+ )(\\S+)"));
        replace(config, "events", value -> value.startsWith("pickrandom "),
                value -> replaceSeparatorInList(value, part -> part.replaceFirst("~(.*)\\.", "~$1>")));
        replaceSeparatorSimple(config, "events", "(point )(\\S+)");
        replace(config, "events", value -> value.startsWith("runForAll "),
                value -> replaceSeparatorMultiple(value, "( where:)(\\S+)", "( events:)(\\S+)"));
        replace(config, "events", value -> value.startsWith("runIndependent "),
                value -> replaceSeparator(value, "( events:)(\\S+)"));
        replace(config, "events", value -> value.startsWith("spawn "),
                value -> replaceSeparatorMultiple(value, "( drops:)(\\S+)",
                        "( h:)(\\S+)", "( c:)(\\S+)", "( l:)(\\S+)", "( b:)(\\S+)", "( m:)(\\S+)", "( o:)(\\S+)"));
        replaceSeparatorSimple(config, "events", "(stage )(\\S+)");
        replaceSeparatorSimple(config, "events", "(tag \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "events", "(take )(\\S+)");
        replaceSeparatorSimple(config, "events", "(variable )(\\S+)");

        replaceSeparatorSimple(config, "objectives", "(chestput \\S+ )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(consume )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(craft )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(enchant )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(fish )(\\S+)");
        replace(config, "objectives", value -> value.startsWith("kill"),
                value -> replaceSeparator(value, "( required:)(\\S+)"));
        replaceSeparatorSimple(config, "objectives", "(npcinteract )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(npcrange )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(pickup )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(smelt )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(equip \\S+ )(\\S+)");

        replaceSeparatorSimple(config, "events", "(menu open )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(menu )(\\S+)");
        replaceSeparatorSimple(config, "conditions", "(menu )(\\S+)");

        replaceSeparatorSimple(config, "events", "(npcmove )(\\S+)");
        replaceSeparatorSimple(config, "events", "(npcstop )(\\S+)");
        replaceSeparatorSimple(config, "objectives", "(npckill )(\\S+)");

        replaceSeparatorInList(config, "conversations", "*", "NPC_options", "*", "conditions");
        replaceSeparatorInList(config, "conversations", "*", "NPC_options", "*", "events");
        replaceSeparatorInList(config, "conversations", "*", "player_options", "*", "conditions");
        replaceSeparatorInList(config, "conversations", "*", "player_options", "*", "events");
        replaceSeparatorInList(config, "conversations", "*", "first");
        replaceSeparatorInList(config, value -> {
            return StringUtils.countMatches(value, ".") == 2 ? replaceSeparatorInList(value) : value;
        }, "conversations", "*", "NPC_options", "*", "pointers");
        replaceSeparatorInList(config, value -> {
            return StringUtils.countMatches(value, ".") == 2 ? replaceSeparatorInList(value) : value;
        }, "conversations", "*", "player_options", "*", "pointers");

        replaceSeparatorInList(config, "holograms", "*", "conditions");
        replaceSeparatorInList(config, "npc_holograms", "*", "conditions");
        replaceSeparatorInList(config, "npc_holograms", "*", "npcs");

        replaceSeparatorInList(config, "effectlib", "*", "conditions");
        replaceSeparatorInList(config, "effectlib", "*", "npcs");

        replaceSeparatorInList(config, "hide_npcs", "*");

        replaceSeparatorInList(config, "player_hider", "*", "source_player");
        replaceSeparatorInList(config, "player_hider", "*", "target_player");

        replaceSeparatorInList(config, "menus", "*", "slots", "*");
        replaceSeparatorInList(config, "menu_items", "*", "click");
        replaceSeparatorInList(config, "menu_items", "*", "click", "left");
        replaceSeparatorInList(config, "menu_items", "*", "click", "shiftLeft");
        replaceSeparatorInList(config, "menu_items", "*", "click", "right");
        replaceSeparatorInList(config, "menu_items", "*", "click", "shiftRight");
        replaceSeparatorInList(config, "menu_items", "*", "click", "middleMouse");

        replaceSeparatorInList(config, "cancel", "*", "conditions");
        replaceSeparatorInList(config, "cancel", "*", "objectives");
        replaceSeparatorInList(config, "cancel", "*", "tags");
        replaceSeparatorInList(config, "cancel", "*", "points");
        replaceSeparatorInList(config, "cancel", "*", "journal");
        replaceSeparatorInList(config, "cancel", "*", "events");
        replaceSeparatorInList(config, "cancel", "*", "item");

        replaceSeparatorInList(config, "schedules", "*", "events");

        replaceSeparatorInList(config, "compass", "*", "item");

        replaceSeparatorInList(config, "journal_main_page", "*", "conditions");
    }

    private void replaceSeparatorSimple(final MultiConfiguration config, final String sectionName, final String pattern) {
        replace(config, sectionName, value -> true, value -> replaceSeparator(value, pattern));
    }

    private String replaceSeparatorMultiple(final String input, final String... patterns) {
        String result = input;
        for (final String pattern : patterns) {
            result = replaceSeparator(result, pattern);
        }
        return result;
    }

    private String replaceSeparator(final String input, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern).matcher(input);
        final StringBuilder stringBuffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, matcher.group(1) + replaceSeparatorInList(matcher.group(2)));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private void replaceSeparatorInList(final MultiConfiguration config, final String... selection) {
        replaceSeparatorInList(config, this::replaceSeparatorInList, selection);
    }

    private void replaceSeparatorInList(final MultiConfiguration config, final Function<String, String> operation, final String... selection) {
        for (final String key : config.getKeys(true)) {
            if (!keyMatchesSelection(key.split("\\."), selection)) {
                continue;
            }
            if (!config.isString(key)) {
                continue;
            }
            config.set(key, operation.apply(config.getString(key, "")));
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

    private String replaceSeparatorInList(final String listInput) {
        return replaceSeparatorInList(listInput, value -> value.replaceFirst("\\.", ">"));
    }

    private String replaceSeparatorInList(final String listInput, final Function<String, String> operation) {
        final String[] valueParts = listInput.split(",", -1);
        for (int i = 0; i < valueParts.length; i++) {
            final String valuePart = valueParts[i];
            if (!valuePart.contains("%")) {
                valueParts[i] = operation.apply(valuePart);
            }
        }
        return String.join(",", valueParts);
    }
}
