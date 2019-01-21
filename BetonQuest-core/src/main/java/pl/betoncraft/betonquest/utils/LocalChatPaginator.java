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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provide a slightly more intelligent wordwrap that will return the last last space if required
 */
public class LocalChatPaginator extends ChatPaginator {

    /**
     * Takes a string and returns the last colors that can be copied to a new line
     */
    public static String getLastColors(String input) {
        ChatColor lastColor = null;
        List<ChatColor> lastFormats = new ArrayList<>();

        int length = input.length();

        for (int index = length - 1; index > -1; --index) {
            char section = input.charAt(index);
            if (section == 167 && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(c);

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

        String result = String.join("", lastFormats.stream()
                .map(ChatColor::toString)
                .collect(Collectors.toList()));

        if (lastColor != null) {
            result = lastColor.toString() + result;
        }
        return result;
    }

    public static String[] wordWrap(String rawString, int lineLength) {
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
    public static String[] wordWrap(String rawString, int lineLength, String wrapPrefix) {
        // A null string is a single line
        if (rawString == null) {
            return new String[]{""};
        }

        // A string shorter than the lineWidth is a single line
        if (rawString.length() <= lineLength && !rawString.contains("\n")) {
            return new String[]{rawString};
        }

        // Work out wrapPrefix color chars
        int wrapPrefixColorChars = hiddenCount(wrapPrefix);
        int wraplineLength = lineLength - (wrapPrefix.length() - wrapPrefixColorChars);

        char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        int lineColorChars = 0;

        for (int i = 0; i < rawChars.length; i++) {
            char c = rawChars[i];

            // skip chat color modifiers
            if (c == ChatColor.COLOR_CHAR) {
                word.append(ChatColor.getByChar(String.valueOf(rawChars[i + 1]).toLowerCase()));
                lineColorChars += 2;
                i++; // Eat the next character as we have already processed it
                continue;
            }

            if (c == ' ' || c == '\n') {
                if (line.length() == 0 && word.length() > (lines.size() == 0 ? lineLength : wraplineLength)) { // special case: extremely long word begins a line
                    lines.addAll(Arrays.asList(word.toString().split("(?<=\\G.{" + (lines.size() == 0 ? lineLength : wraplineLength) + "})")));

                } else if (line.length() + 1 + word.length() - lineColorChars == (lines.size() == 0 ? lineLength : wraplineLength)) { // Line exactly the correct length...newline
                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                    lines.add(line.toString());
                    line = new StringBuilder();
                    lineColorChars = 0;
                } else if (line.length() + 1 + word.length() - lineColorChars > (lines.size() == 0 ? lineLength : wraplineLength)) { // Line too long...break the line at last space
                    lines.add(line.toString());
                    line = new StringBuilder();

                    for (String partialWord : word.toString().split("(?<=\\G.{" + (lines.size() == 0 ? lineLength : wraplineLength) + "})")) {
                        if (line.length() > 0) {
                            lines.add(line.toString());
                        }
                        line = new StringBuilder(partialWord);
                    }
                    lineColorChars = 0;
                } else {

                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                }
                word = new StringBuilder();

                if (c == '\n') { // Newline forces the line to flush
                    lines.add(line.toString());
                    line = new StringBuilder();
                    lineColorChars = 0;
                }
            } else {
                word.append(c);
            }
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
     * Return the length of the line minus hidden characters
     */
    public static int lineLength(String input) {
        int ret = 0;
        char[] rawChars = input.toCharArray();
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
    public static int hiddenCount(String input) {
        char[] rawChars = input.toCharArray();
        int count = 0;
        for (int i = 0; i < rawChars.length; i++) {
            char c = rawChars[i];

            if (c == ChatColor.COLOR_CHAR) {
                count += 2;
                i++;
            }

        }
        return count;
    }

}
