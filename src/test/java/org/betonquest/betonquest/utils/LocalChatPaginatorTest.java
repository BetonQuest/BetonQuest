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
                Arguments.of("____",
                        new String[]{"____"},
                        4, ""),
                Arguments.of("________",
                        new String[]{"____", "____"},
                        4, ""),
                Arguments.of("____ ____",
                        new String[]{"____", "____"},
                        4, ""),
                Arguments.of(". ___ ____",
                        new String[]{". ___", "____"},
                        4, ""),
                Arguments.of("Bist du bereit?              ",
                        new String[]{"Bist du bereit?"},
                        25, ""),
                Arguments.of("\nBist du bereit?",
                        new String[]{"", "Bist du bereit?"},
                        25, ""),
                Arguments.of("Bist du bereit?\n",
                        new String[]{"Bist du bereit?"},
                        25, ""),
                Arguments.of("Bist du bereit?\n\n",
                        new String[]{"Bist du bereit?", ""},
                        25, ""),
                Arguments.of("Bist du bereit?\nDenn ich bin es!                ",
                        new String[]{"Bist du bereit?", "Denn ich bin es!"},
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
                                "verylongprefix!",
                                "verylongprefix!r",
                                "verylongprefix!e",
                                "verylongprefix!a",
                                "verylongprefix!l",
                                "verylongprefix!l",
                                "verylongprefix!y",
                                "verylongprefix!",
                                "verylongprefix!b",
                                "verylongprefix!r",
                                "verylongprefix!e",
                                "verylongprefix!a",
                                "verylongprefix!k",
                                "verylongprefix!."},
                        5, "verylongprefix!"),
                Arguments.of(String.valueOf(ChatColor.COLOR_CHAR),
                        new String[]{String.valueOf(ChatColor.COLOR_CHAR)},
                        25, ""),
                Arguments.of("§ no valid color code",
                        new String[]{"§ no valid color code"},
                        25, ""),
                Arguments.of("Broken code no space§",
                        new String[]{"Broken code no space§"},
                        25, ""),
                Arguments.of("Broken code much space §",
                        new String[]{"Broken code much space §"},
                        25, ""),
                Arguments.of("Broken code no space§",
                        new String[]{"Broken code", "no space§"},
                        11, ""),
                Arguments.of("Broken code much space §",
                        new String[]{"Broken code", "much space §"},
                        12, ""),
                Arguments.of("This is a broken color code: §\nand a following newline.",
                        new String[]{"This is a broken color code: §",
                                "and a following newline."},
                        30, ""),
                Arguments.of("This is a broken color code:§\nand a following newline.",
                        new String[]{"This is a broken color code:§",
                                "and a following newline."},
                        25, ""),
                Arguments.of("Very colorful!",
                        new String[]{"Very colorful!"},
                        12, ""),
                Arguments.of("§aV§be§cr§dy §ec§fo§1l§2o§3r§4f§5u§6l§7!",
                        new String[]{"§aV§be§cr§dy §ec§fo§1l§2o§3r§4f§5u§6l§7!"},
                        12, ""),
                Arguments.of("fun with spaces",
                        new String[]{"fun with", "spaces"},
                        7, ""),
                Arguments.of("fun              with            spaces",
                        new String[]{"fun", "", "  with", "", "", "space", "s"},
                        5, ""),
                Arguments.of("fun\nwith\nspaces",
                        new String[]{"fun", "with", "spaces"},
                        25, ""),
                Arguments.of("fun          \n    with   \n                                      spaces",
                        new String[]{"fun", "    with", "", "spaces"},
                        25, ""),
                Arguments.of("fun          \n    with   \n                                       spaces",
                        new String[]{"fun", "    with", "", " spaces"},
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
