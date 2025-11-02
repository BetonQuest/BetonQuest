package org.betonquest.betonquest.api.common.component.font;

import java.util.Map;

/**
 * Default font implementation for Minecraft.
 */
public class DefaultFont implements Font {
    /**
     * Pixel-length of characters in the default resource pack Minecraft font.
     * Only the most common characters are defined.
     */
    private final Map<Character, Integer> fontSizes;

    /**
     * Creates a new DefaultFont instance.
     */
    public DefaultFont() {
        this.fontSizes = Map.ofEntries(
                Map.entry(' ', 4),
                Map.entry('!', 2),
                Map.entry('¡', 2),
                Map.entry('"', 4),
                Map.entry('\'', 2),
                Map.entry('(', 4),
                Map.entry(')', 4),
                Map.entry('*', 4),
                Map.entry(',', 2),
                Map.entry('.', 2),
                Map.entry(':', 2),
                Map.entry(';', 2),
                Map.entry('<', 5),
                Map.entry('>', 5),
                Map.entry('@', 7),
                Map.entry('I', 4),
                Map.entry('[', 4),
                Map.entry(']', 4),
                Map.entry('`', 3),
                Map.entry('f', 5),
                Map.entry('i', 2),
                Map.entry('í', 3),
                Map.entry('ì', 3),
                Map.entry('ȋ', 4),
                Map.entry('î', 4),
                Map.entry('ǐ', 4),
                Map.entry('ï', 4),
                Map.entry('k', 5),
                Map.entry('l', 3),
                Map.entry('t', 4),
                Map.entry('{', 4),
                Map.entry('|', 2),
                Map.entry('}', 4),
                Map.entry('~', 7)
        );
    }

    @Override
    public int getWidth(final char character) {
        return fontSizes.getOrDefault(character, 6);
    }
}
