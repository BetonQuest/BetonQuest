package org.betonquest.betonquest.api.common.component.font;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The FontRegistry class is responsible for managing a collection of fonts.
 * It allows for the registration and retrieval of fonts by their names.
 */
public class FontRegistry {
    /**
     * The map of registered fonts, where the key is the font name and the value is the Font object.
     */
    private final Map<Key, Font> fonts;

    /**
     * The default font to use if no other font is found.
     */
    private final Key defaultFont;

    /**
     * Create a new FontRegistry.
     *
     * @param defaultFont the default font to use if no other font is found, this font still need to be registered,
     *                    with the same name
     */
    public FontRegistry(final Key defaultFont) {
        this.defaultFont = defaultFont;
        this.fonts = new HashMap<>();
    }

    /**
     * Register a font with the given name.
     *
     * @param name the name of the font
     * @param font the font to register
     */
    public void registerFont(final Key name, final Font font) {
        fonts.put(name, font);
    }

    /**
     * Get a font by its name.
     *
     * @param name the name of the font
     * @return the font, or default font if not found
     */
    public Font getFont(@Nullable final Key name) {
        final Font font = fonts.get(name);
        if (font != null) {
            return font;
        }
        final Font fallbackFont = fonts.get(defaultFont);
        if (fallbackFont != null) {
            return fallbackFont;
        }
        throw new IllegalArgumentException("Font not found: " + name);
    }
}
