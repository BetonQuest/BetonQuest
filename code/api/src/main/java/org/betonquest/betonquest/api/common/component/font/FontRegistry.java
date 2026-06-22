package org.betonquest.betonquest.api.common.component.font;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for a registry for fonts.
 *
 * @since 3.0.0
 */
public interface FontRegistry {

    /**
     * Register a font with the given name.
     *
     * @param name the name of the font
     * @param font the font to register
     * @since 3.0.0
     */
    void registerFont(Key name, Font font);

    /**
     * Get a font by its name.
     *
     * @param name the name of the font
     * @return the font, or default font if not found
     * @since 3.0.0
     */
    Font getFont(@Nullable Key name);
}
