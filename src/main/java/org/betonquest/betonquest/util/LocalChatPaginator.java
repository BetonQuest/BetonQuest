package org.betonquest.betonquest.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provide a slightly more intelligent wordwrap that will return the last last space if required
 * <p>
 * We also try to handle variable width characters.
 */
public final class LocalChatPaginator {
    /**
     * Pixel-length of characters in the default resource pack Minecraft font.
     * Only the most common characters are defined.
     */
    public static final Map<Character, Integer> FONT_SIZES;

    /**
     * Default assumption for pixel-length of characters that are not covered by {@link #FONT_SIZES}.
     */
    public static final int DEFAULT_CHAR_WIDTH = 6;

    /**
     * Pixel-length of a space character.
     */
    public static final int SPACE_WIDTH = 4;

    static {
        FONT_SIZES = Map.ofEntries(
                Map.entry(' ', SPACE_WIDTH),
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

    private LocalChatPaginator() {
    }

    /**
     * Breaks a raw string up into a series of lines that have similar
     * length when displayed with the default Minecraft font.
     * Wrapping happens on space characters if possible,
     * but very long words will be broken if necessary.
     *
     * @param rawString  input string to wrap
     * @param lineLength expected line length in characters to aim for
     * @return array containing lines
     */

    public static String[] wordWrap(final String rawString, final int lineLength) {
        return wordWrap(rawString, lineLength, "");
    }

    /**
     * Breaks a raw string up into a series of lines. Words are wrapped using
     * spaces as decimeters and the newline character is respected.
     *
     * @param rawString  The raw string to break.
     * @param lineLength The length of a line of text.
     * @param wrapPrefix The string to prefix the wrapped line with
     * @return An array of word-wrapped lines.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity", "PMD.NcssCount",
            "PMD.SwitchDensity"})
    public static String[] wordWrap(final String rawString, final int lineLength, final String wrapPrefix) {
        final int maxWidth = lineLength * DEFAULT_CHAR_WIDTH;
        if (!rawString.contains("\n")) {
            final String strippedRawString = StringUtils.stripEnd(rawString, null);
            if (getWidth(strippedRawString) <= maxWidth) {
                return new String[]{strippedRawString};
            }
        }

        final int maxWrapWidth = maxWidth - getWidth(wrapPrefix);
        final char[] rawChars = rawString.toCharArray();

        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder(lineLength);
        final List<String> lines = new LinkedList<>();
        int wordWidth = 0;
        int lineWidth = 0;

        for (int i = 0; i < rawChars.length; i++) {
            final char singleChar = rawChars[i];
            switch (singleChar) {
                case '\n' -> {
                    line.append(word);
                    lines.add(line.toString());
                    line = new StringBuilder(lineLength);
                    word = new StringBuilder();
                    wordWidth = 0;
                    lineWidth = 0;
                }
                case ChatColor.COLOR_CHAR -> {
                    word.append(ChatColor.COLOR_CHAR);
                    if (rawChars.length <= i + 1) {
                        continue;
                    }
                    final char colorCode = rawChars[i + 1];
                    if (colorCode == 'x' || ChatColor.getByChar(colorCode) != null) {
                        word.append(colorCode);
                        i++;
                    }
                }
                case ' ' -> {
                    if (!line.isEmpty() && lineWidth + wordWidth > (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                        lines.add(line.toString());
                        line = new StringBuilder(lineLength);
                        lineWidth = 0;
                    }
                    word.append(' ');
                    wordWidth += SPACE_WIDTH;
                    line.append(word);
                    lineWidth += wordWidth;
                    word = new StringBuilder();
                    wordWidth = 0;
                }
                default -> {
                    final int singleCharWidth = getWidth(singleChar);
                    if (!line.isEmpty() && lineWidth + wordWidth + singleCharWidth > (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                        lines.add(line.toString());
                        line = new StringBuilder(lineLength);
                        lineWidth = 0;
                    }
                    if (line.isEmpty() && wordWidth + singleCharWidth > (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                        lines.add(word.toString());
                        word = new StringBuilder();
                        wordWidth = 0;
                    }
                    word.append(singleChar);
                    wordWidth += singleCharWidth;
                }
            }
        }

        if (!word.isEmpty()) {
            line.append(word);
        }

        if (!line.isEmpty()) {
            lines.add(line.toString());
        }

        lines.replaceAll(str -> StringUtils.stripEnd(str, null));

        for (int i = 1; i < lines.size(); i++) {
            final String previousLine = lines.get(i - 1);
            final String currentLine = lines.get(i);

            lines.set(i, wrapPrefix + ChatColor.getLastColors(previousLine) + currentLine);
        }

        return lines.toArray(new String[0]);
    }

    /**
     * Return the width of text taking into account variable font size and ignoring hidden characters
     *
     * @param input the input string.
     * @return width of text
     */
    public static int getWidth(final String input) {
        int ret = 0;
        final char[] rawChars = input.toCharArray();

        for (int i = 0; i < rawChars.length; i++) {
            if (rawChars[i] == ChatColor.COLOR_CHAR) {
                i += 1;
                continue;
            }
            ret += getWidth(rawChars[i]);
        }
        return ret;
    }

    /**
     * Get the width of a character in pixels. Returned values are for the default Minecraft font.
     *
     * @param character character to look up
     * @return width of the character
     */
    public static int getWidth(final Character character) {
        final Integer charWidth = FONT_SIZES.get(character);
        if (charWidth != null) {
            return charWidth;
        }

        final String withoutAccent = StringUtils.stripAccents(character.toString());
        final char withoutAccentChar = withoutAccent.charAt(0);
        if (!character.equals(withoutAccentChar)) {
            return FONT_SIZES.getOrDefault(withoutAccentChar, DEFAULT_CHAR_WIDTH);
        }
        return DEFAULT_CHAR_WIDTH;
    }
}
