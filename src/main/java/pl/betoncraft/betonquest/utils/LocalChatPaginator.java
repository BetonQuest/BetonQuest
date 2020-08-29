/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.betoncraft.betonquest.utils;

import org.bukkit.ChatColor;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provide a slightly more intelligent wordwrap that will return the last last space if required
 * <p>
 * We also try to handle variable width characters.
 */
public class LocalChatPaginator extends ChatPaginator {
    public static Map<Character, Integer> fontSizes;
    public static int defaultCharWidth = 5;

    static {
        fontSizes = Stream.of(new Object[][]{
                {' ', 4}, {'!', 2}, {'"', 5}, {'#', 6}, {'$', 6}, {'%', 6}, {'&', 6}, {'\'', 3},
                {'(', 6}, {')', 6}, {'*', 5}, {'+', 6}, {',', 2}, {'-', 6}, {'.', 2}, {'/', 6},
                {'0', 6}, {'1', 6}, {'2', 6}, {'3', 6}, {'4', 6}, {'5', 6}, {'6', 6}, {'7', 6},
                {'8', 6}, {'9', 6}, {':', 2}, {';', 2}, {'<', 5}, {'=', 6}, {'>', 5}, {'?', 6},
                {'@', 7}, {'A', 6}, {'B', 6}, {'C', 6}, {'D', 6}, {'E', 6}, {'F', 6}, {'G', 6},
                {'H', 6}, {'I', 4}, {'J', 6}, {'K', 6}, {'L', 6}, {'M', 6}, {'N', 6}, {'O', 6},
                {'P', 6}, {'Q', 6}, {'R', 6}, {'S', 6}, {'T', 6}, {'U', 6}, {'V', 6}, {'W', 6},
                {'X', 6}, {'Y', 6}, {'Z', 6}, {'[', 4}, {'\\', 6}, {']', 4}, {'^', 6}, {'_', 6},
                {'`', 3}, {'a', 6}, {'b', 6}, {'c', 6}, {'d', 6}, {'e', 6}, {'f', 5}, {'g', 6},
                {'h', 6}, {'i', 2}, {'j', 6}, {'k', 5}, {'l', 3}, {'m', 6}, {'n', 6}, {'o', 6},
                {'p', 6}, {'q', 6}, {'r', 6}, {'s', 6}, {'t', 4}, {'u', 6}, {'v', 6}, {'w', 6},
                {'x', 6}, {'y', 6}, {'z', 6}, {'{', 5}, {'|', 2}, {'}', 5}, {'~', 7}
        }).collect(Collectors.toMap(data -> (Character) data[0], data -> (Integer) data[1]));
    }

    /**
     * Takes a string and returns the last colors that can be copied to a new line
     */
    public static String getLastColors(final String input) {
        ChatColor lastColor = null;
        final List<ChatColor> lastFormats = new ArrayList<>();

        final int length = input.length();

        for (int index = length - 1; index > -1; --index) {
            final char section = input.charAt(index);
            if (section == 167 && index < length - 1) {
                final char colorChar = input.charAt(index + 1);
                final ChatColor color = ChatColor.getByChar(colorChar);

                if (color != null) {
                    if (color.equals(ChatColor.RESET)) {
                        break;
                    }

                    if (color.isColor() && lastColor == null) {
                        lastColor = color;
                        continue;
                    }

                    if (color.isFormat() && !lastFormats.contains(color)) {
                        lastFormats.add(color);
                    }
                }
            }
        }

        String result = lastFormats.stream()
                .map(ChatColor::toString)
                .collect(Collectors.joining(""));

        if (lastColor != null) {
            result = lastColor.toString() + result;
        }
        return result;
    }

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
    public static String[] wordWrap(final String rawString, final int lineLength, final String wrapPrefix) {

        // A null string is a single line
        if (rawString == null) {
            return new String[]{""};
        }

        final int maxWidth = lineLength * defaultCharWidth;

        // A string shorter than the lineWidth is a single line
        if (getWidth(rawString) <= maxWidth && !rawString.contains("\n")) {
            return new String[]{rawString};
        }

        // Work out wrapPrefix color chars
        final int maxWrapWidth = maxWidth - getWidth(wrapPrefix);

        final char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder();
        final List<String> lines = new LinkedList<>();
        int wordWidth = 0;
        int lineWidth = 0;

        for (int i = 0; i < rawChars.length; i++) {
            final char singleChar = rawChars[i];

            // skip chat color modifiers
            if (singleChar == ChatColor.COLOR_CHAR) {
                word.append(ChatColor.getByChar(String.valueOf(rawChars[i + 1]).toLowerCase()));
                i++; // Eat the next character as we have already processed it
                continue;
            }

            final int width = getWidth(singleChar);

            if (singleChar != ' ' && singleChar != '\n') {
                // Extremely long word begins a line, break the word up
                if (line.length() == 0 && wordWidth + width >= (lines.size() == 0 ? maxWidth : maxWrapWidth)) {
                    lines.add(word.toString());
                    word = new StringBuilder();
                    wordWidth = 0;
                }

                // Word too long with rest of line, force line to wrap
                if (line.length() > 0 && lineWidth + wordWidth + width >= (lines.size() == 0 ? maxWidth : maxWrapWidth)) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                    lineWidth = 0;
                }

                word.append(singleChar);
                wordWidth += width;
                continue;
            }

            if (singleChar == '\n') {
                // NewLine forces a new line
                line.append(' ');
                line.append(word);
                lines.add(line.toString());
                line = new StringBuilder();
                word = new StringBuilder();
                lineWidth = 0;
                continue;
            }

            if (line.length() > 0) {
                line.append(' ');
                lineWidth += getWidth(' ');
            }
            line.append(word);
            lineWidth += wordWidth;
            word = new StringBuilder();
            wordWidth = 0;
        }

        if (line.length() > 0) { // Only add the last line if there is anything to add
            lines.add(line.toString());
        }

        // Iterate over the wrapped lines, applying the last color from one line to the beginning of the next
        if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != ChatColor.COLOR_CHAR) {
            lines.set(0, ChatColor.WHITE + lines.get(0));
        }
        for (int i = 1; i < lines.size(); i++) {
            final String pLine = lines.get(i - 1);
            final String subLine = lines.get(i);

            //char color = pLine.charAt(pLine.lastIndexOf(ChatColor.COLOR_CHAR) + 1);
            lines.set(i, wrapPrefix + getLastColors(pLine) + subLine);
        }

        return lines.toArray(new String[0]);
    }

    /**
     * Return the width of text taking into account variable font size and ignoring hidden characters
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

    public static int getWidth(final Character character) {
        return fontSizes.containsKey(character) ? fontSizes.get(character) : defaultCharWidth;
    }

    /**
     * Return the length of the line minus hidden characters
     */
    public static int lineLength(final String input) {
        int ret = 0;
        final char[] rawChars = input.toCharArray();
        for (int i = 0; i < rawChars.length; i++) {
            if (rawChars[i] == ChatColor.COLOR_CHAR) {
                i += 1;
                continue;
            }
            ret++;
        }
        return ret;
    }

    /**
     * Return the number of hidden characters in input
     */
    public static int hiddenCount(final String input) {
        final char[] rawChars = input.toCharArray();
        int count = 0;
        for (int i = 0; i < rawChars.length; i++) {
            final char colorChar = rawChars[i];

            if (colorChar == ChatColor.COLOR_CHAR) {
                count += 2;
                i++;
            }

        }
        return count;
    }

}
