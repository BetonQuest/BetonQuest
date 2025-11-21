package org.betonquest.betonquest.text.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.text.TextParser;
import org.bukkit.ChatColor;

/**
 * A parser that uses legacy formatting codes to parse text.
 */
public class LegacyParser implements TextParser {
    /**
     * The legacy component serializer to use for parsing text.
     */
    private final LegacyComponentSerializer serializer;

    /**
     * Constructs a new legacy parser for parsing text.
     * <p>
     * This implementation calls {@link ChatColor#translateAlternateColorCodes(char, String)} with the `&amp;` character.
     * So you need to pass a serializer that supports the
     * {@link LegacyComponentSerializer#SECTION_CHAR} character to support the `&amp;` and `§` characters.
     *
     * @param serializer the legacy component serializer to use for parsing texts
     */
    public LegacyParser(final LegacyComponentSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Component parse(final String text) {
        return serializer.deserialize(ChatColor.translateAlternateColorCodes('&', text.replaceAll("(?<!\\\\)\\\\n", "\n")));
    }
}
