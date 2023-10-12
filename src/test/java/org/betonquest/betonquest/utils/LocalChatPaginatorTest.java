package org.betonquest.betonquest.utils;

import org.bukkit.ChatColor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link LocalChatPaginator}
 */
@SuppressWarnings("PMD.UseVarargs")
class LocalChatPaginatorTest {
    private static Stream<Arguments> stringsToWrap() {
        final String spaces = "    ";
        return Stream.of(
                Arguments.of("Bist du bereit?",
                        new String[]{"Bist du bereit?"},
                        25, ""),
                Arguments.of("\nBist du bereit?",
                        new String[]{"", "Bist du bereit?"},
                        25, ""),
                Arguments.of("Ich stelle dir nun 15 Fragen, von denen du mindestens 13 richtig beantworten musst. \nBist du bereit?",
                        new String[]{"Ich stelle dir nun 15 Fragen,",
                                "von denen du mindestens 13",
                                "richtig beantworten musst.",
                                "Bist du bereit?"},
                        25, ""),

                Arguments.of("Bist du bereit?",
                        new String[]{"Bist du bereit?"},
                        25, spaces),
                Arguments.of("\nBist du bereit?",
                        new String[]{"", spaces + "Bist du bereit?"},
                        25, spaces),
                Arguments.of("Ich stelle dir nun 15 Fragen, von denen du mindestens 13 richtig beantworten musst.\n Bist du bereit?",
                        new String[]{"Ich stelle dir nun 15 Fragen,",
                                spaces + "von denen du mindestens",
                                spaces + "13 richtig beantworten",
                                spaces + "musst.",
                                spaces + " Bist du bereit?"},
                        25, spaces),
                Arguments.of("   §8[§3Ich denke ich lese mir die Regeln noch mal durch.§r§3§8]",
                        new String[]{"   §8[§3Ich denke ich lese mir die",
                                spaces + "§3Regeln noch mal durch.§r§3§8]"},
                        25, spaces),
                Arguments.of("This should really break.",
                        new String[]{"This",
                                "verylongprefix!s",
                                "verylongprefix!h",
                                "verylongprefix!o",
                                "verylongprefix!u",
                                "verylongprefix!l",
                                "verylongprefix!d",
                                "verylongprefix!r",
                                "verylongprefix!e",
                                "verylongprefix!a",
                                "verylongprefix!l",
                                "verylongprefix!l",
                                "verylongprefix!y",
                                "verylongprefix!b",
                                "verylongprefix!r",
                                "verylongprefix!e",
                                "verylongprefix!a",
                                "verylongprefix!k",
                                "verylongprefix!."},
                        5, "verylongprefix!"),
                Arguments.of(String.valueOf(ChatColor.COLOR_CHAR),
                        new String[]{"§"},
                        25, ""),
                Arguments.of("fun\nwith\nspaces",
                        new String[]{"fun", "with", "spaces"},
                        25, "")
        );
    }

    @ParameterizedTest
    @MethodSource("stringsToWrap")
    void line_wrap(final String input, final String[] expected, final int lineLength, final String wrapPrefix) {
        final String[] result = LocalChatPaginator.wordWrap(input, lineLength, wrapPrefix);
        assertArrayEquals(expected, result, "The arrays should equal each other");
    }
}
