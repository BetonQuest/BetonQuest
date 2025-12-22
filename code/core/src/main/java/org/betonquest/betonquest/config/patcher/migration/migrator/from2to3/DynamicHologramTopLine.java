package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Migrates old fixed top line into new dynamic.
 */
public class DynamicHologramTopLine implements QuestMigration {

    /**
     * The regex for one color.
     */
    private static final String COLOR_REGEX = ";?([&§]?[0-9a-f])?";

    /**
     * Pattern to match the correct syntax for the top line content.
     */
    private static final Pattern TOP_LINE_VALIDATOR = Pattern.compile("^top:([\\w.]+);(\\w+);(\\d+)"
            + COLOR_REGEX + COLOR_REGEX + COLOR_REGEX + COLOR_REGEX + "$", Pattern.CASE_INSENSITIVE);

    /**
     * The empty default constructor.
     */
    public DynamicHologramTopLine() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final ConfigurationSection holograms = quest.getQuestConfig().getConfigurationSection("holograms");
        if (holograms != null) {
            migrateSection(holograms);
        }
        final ConfigurationSection npcHolograms = quest.getQuestConfig().getConfigurationSection("npc_holograms");
        if (npcHolograms != null) {
            migrateSection(npcHolograms);
        }
    }

    private void migrateSection(final ConfigurationSection holograms) {
        for (final String hologram : holograms.getKeys(false)) {
            final String key = hologram + ".lines";
            final List<String> stringList = holograms.getStringList(key);
            boolean listChanged = false;
            for (int i = 0; i < stringList.size(); i++) {
                final String line = stringList.get(i);
                final Matcher matcher = TOP_LINE_VALIDATOR.matcher(line);
                if (matcher.matches()) {
                    stringList.set(i, buildLine(matcher));
                    listChanged = true;
                }
            }
            if (listChanged) {
                holograms.set(key, stringList);
            }
        }
    }

    private String buildLine(final Matcher matcher) {
        final String prefix = "top:%s;%s;%s;".formatted(matcher.group(1), matcher.group(2), matcher.group(3));
        final String formatting = "§%s{place}. §%s{name}§%s - §%s{score}".formatted(
                getColorCodes(matcher.group(4)), getColorCodes(matcher.group(5)),
                getColorCodes(matcher.group(6)), getColorCodes(matcher.group(7)));
        return prefix + formatting;
    }

    private char getColorCodes(@Nullable final String color) {
        if (color == null) {
            return ChatColor.WHITE.getChar();
        }
        final int length = color.length();
        if (length == 1 || length == 2) {
            final char colorChar = color.charAt(length - 1);
            final ChatColor byChar = ChatColor.getByChar(colorChar);
            if (byChar != null) {
                return byChar.getChar();
            }
        }
        return ChatColor.WHITE.getChar();
    }
}
