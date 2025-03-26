package org.betonquest.betonquest.message.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.message.MessageParser;
import org.bukkit.ChatColor;

/**
 * A parser that uses legacy formatting codes to parse messages.
 */
public class LegacyParser implements MessageParser {
    /**
     * The legacy component serializer to use for parsing messages.
     */
    private final LegacyComponentSerializer serializer;

    /**
     * Constructs a new legacy parser for parsing messages.
     * <p>
     * This implementation calls {@link ChatColor#translateAlternateColorCodes(char, String)} with the `&amp;` character.
     * So you need to pass a serializer that supports the
     * {@link LegacyComponentSerializer#SECTION_CHAR} character to support the `&amp;` and `§` characters.
     *
     * @param serializer the legacy component serializer to use for parsing messages
     */
    public LegacyParser(final LegacyComponentSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Component parse(final String message) {
        return serializer.deserialize(ChatColor.translateAlternateColorCodes('&', message.replaceAll("(?<!\\\\)\\\\n", "\n")));
    }
}
