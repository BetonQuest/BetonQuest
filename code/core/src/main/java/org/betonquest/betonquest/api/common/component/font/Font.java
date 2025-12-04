package org.betonquest.betonquest.api.common.component.font;

/**
 * The Font interface represents a font that can be used to measure the width of characters.
 * It provides a method to get the width of a character (codepoint) in pixels.
 */
@FunctionalInterface
@SuppressWarnings("PMD.ShortClassName")
public interface Font {

    /**
     * Get the width of a character in pixels.
     *
     * @param characterCodePoint the codepoint of the character to get the width of
     * @return the width of the character in pixels
     */
    int getWidth(int characterCodePoint);

    /**
     * Get the width of a character in pixels.
     *
     * @param character the character to get the width of
     * @return the width of the character in pixels
     */
    default int getWidth(final char character) {
        return getWidth(String.valueOf(character).codePointAt(0));
    }
}
