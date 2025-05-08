package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.common.component.font.DefaultFont;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ComponentLineWrapperTest {
    @Nested
    class component_width_wrapping {
        private static Stream<Arguments> componentsToWrap() {
            return Stream.of(
                    Arguments.of("",
                            List.of(Component.empty()),
                            150),
                    Arguments.of("This is only a hopefully long enough text so it gets wrapped,<br> some edge cases are tested later like links translation keys and key bindings.",
                            List.of(Component.text("This is only a hopefully long"),
                                    Component.text("enough text so it gets"),
                                    Component.text("wrapped,"),
                                    Component.text(" some edge cases are tested"),
                                    Component.text("later like links translation"),
                                    Component.text("keys and key bindings.")
                            ),
                            150),
                    Arguments.of("<red>This is only a hopefully long <yellow>enough text so it gets wrapped,<br> some edge cases are tested </yellow>later like links translation keys and key bindings.",
                            List.of(Component.text("This is only a hopefully long ").color(NamedTextColor.RED),
                                    Component.text("enough text so it gets").color(NamedTextColor.YELLOW),
                                    Component.text("wrapped,").color(NamedTextColor.YELLOW),
                                    Component.text(" some edge cases are tested").color(NamedTextColor.YELLOW),
                                    Component.text("later like links translation").color(NamedTextColor.RED),
                                    Component.text("keys and key bindings.").color(NamedTextColor.RED)
                            ),
                            150),
                    Arguments.of("<red>This is only a hopefully long <yellow>enough text so it gets wrapped,<br> some edge cases are tested </yellow>later like links translation keys and key bindings.",
                            List.of(Component.text("This is only a hopefully long ").color(NamedTextColor.RED).append(Component.text("enough text so it gets wrapped,").color(NamedTextColor.YELLOW)),
                                    Component.empty().color(NamedTextColor.RED)
                                            .append(Component.text(" some edge cases are tested ").color(NamedTextColor.YELLOW))
                                            .append(Component.text("later like links translation keys and key bindings."))
                            ),
                            500));
        }

        @ParameterizedTest
        @MethodSource("componentsToWrap")
        void line_wrap(final String input, final List<Component> expected, final int lineLength) {
            final Key defaultKey = Key.key("default");
            final FontRegistry fontRegistry = new FontRegistry(defaultKey);
            final DefaultFont defaultFont = new DefaultFont();
            fontRegistry.registerFont(defaultKey, defaultFont);
            final ComponentLineWrapper wrapper = new ComponentLineWrapper(fontRegistry, lineLength);
            final Component deserialize = MiniMessage.miniMessage().deserialize(input);
            final List<Component> result = wrapper.splitWidth(deserialize);
            assertEquals(expected, result, "The arrays should equal each other");
        }
    }

    @Nested
    class text_width_wrapping {
        private static Stream<Arguments> stringsToWrap() {
            return Stream.of(
                    Arguments.of("",
                            List.of(""),
                            150),
                    Arguments.of(" ",
                            List.of(" "),
                            150),
                    Arguments.of("Bist du bereit?",
                            List.of("Bist du bereit?"),
                            150),
                    Arguments.of("____",
                            List.of("____"),
                            24),
                    Arguments.of("________",
                            List.of("____", "____"),
                            24),
                    Arguments.of("____ ____",
                            List.of("____", "____"),
                            24),
                    Arguments.of(". ___ ____",
                            List.of(". ___", "____"),
                            24),
                    Arguments.of("Bist du bereit?              ",
                            List.of("Bist du bereit?              "),
                            150),
                    Arguments.of("Bist du bereit?",
                            List.of("Bist du bereit?"),
                            150),
                    Arguments.of("   [Ich denke ich lese mir die Regeln noch mal durch.]",
                            List.of("   [Ich denke ich lese mir die", "Regeln noch mal durch.]"),
                            150),
                    Arguments.of("This should really break.",
                            List.of("This s", "hould", "really", "break", "."),
                            30),
                    Arguments.of("This should so break.",
                            List.of("This s", "hould", "so br", "eak."),
                            30),
                    Arguments.of(String.valueOf(ChatColor.COLOR_CHAR),
                            List.of(String.valueOf(ChatColor.COLOR_CHAR)),
                            150),
                    Arguments.of("§ no valid color code",
                            List.of("§ no valid color code"),
                            150),
                    Arguments.of("Broken code no space§",
                            List.of("Broken code no space§"),
                            150),
                    Arguments.of("Broken code much space §",
                            List.of("Broken code much space §"),
                            150),
                    Arguments.of("Broken code no space§",
                            List.of("Broken code", "no space§"),
                            66),
                    Arguments.of("Broken code much space §",
                            List.of("Broken code", "much space §"),
                            72),
                    Arguments.of("Very colorful!",
                            List.of("Very colorful!"),
                            72),
                    Arguments.of("fun with spaces",
                            List.of("fun with", "spaces"),
                            42),
                    Arguments.of("fun              with            spaces",
                            List.of("fun   ", "       ", "  with ", "       ", "  spa", "ces"),
                            30)
            );
        }

        @ParameterizedTest
        @MethodSource("stringsToWrap")
        void line_wrap(final String input, final List<String> expected, final int lineLength) {
            final Key defaultKey = Key.key("default");
            final FontRegistry fontRegistry = new FontRegistry(defaultKey);
            final DefaultFont defaultFont = new DefaultFont();
            fontRegistry.registerFont(defaultKey, defaultFont);
            final ComponentLineWrapper wrapper = new ComponentLineWrapper(fontRegistry, lineLength);
            final List<String> result = wrapper.wrapText(defaultFont, input, new ComponentLineWrapper.Offset());
            assertEquals(expected, result, "The arrays should equal each other");
        }
    }

    @Nested
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    class new_line_wrapping {
        @Test
        void empty() {
            final TextComponent empty = Component.empty();
            final List<Component> lines = ComponentLineWrapper.splitNewLine(empty);
            assertEquals(1, lines.size(), "Expected one line for empty component");
            assertEquals(empty, lines.get(0), "Expected empty component to be the only line");
        }

        @Test
        void single_line() {
            final TextComponent singleLine = Component.text("Hello, World!");
            final List<Component> lines = ComponentLineWrapper.splitNewLine(singleLine);
            assertEquals(1, lines.size(), "Expected one line for single line component");
            assertEquals(singleLine, lines.get(0), "Expected single line component to be the only line");
        }

        @Test
        void multiple_lines() {
            final TextComponent multiLine = Component.text("Hello,\nWorld!");
            final List<Component> lines = ComponentLineWrapper.splitNewLine(multiLine);
            assertEquals(2, lines.size(), "Expected two lines for multi line component");
            assertEquals(Component.text("Hello,"), lines.get(0), "Expected first line to be 'Hello,'");
            assertEquals(Component.text("World!"), lines.get(1), "Expected second line to be 'World!'");
        }

        @Test
        void one_new_line() {
            final TextComponent onlyNewLine = Component.text("\n");
            final List<Component> lines = ComponentLineWrapper.splitNewLine(onlyNewLine);
            assertEquals(2, lines.size(), "Expected two lines for single new line component");
            assertEquals(Component.empty(), lines.get(0), "Expected first line to be empty");
            assertEquals(Component.empty(), lines.get(1), "Expected second line to be empty");
        }

        @Test
        void two_new_lines() {
            final TextComponent twoNewLines = Component.text("\n\n");
            final List<Component> lines = ComponentLineWrapper.splitNewLine(twoNewLines);
            assertEquals(3, lines.size(), "Expected three lines for two new lines component");
            assertEquals(Component.empty(), lines.get(0), "Expected first line to be empty");
            assertEquals(Component.empty(), lines.get(1), "Expected second line to be empty");
            assertEquals(Component.empty(), lines.get(2), "Expected third line to be empty");
        }

        @Test
        void new_line_at_the_start() {
            final TextComponent newLineAtStart = Component.text("\nHello");
            final List<Component> lines = ComponentLineWrapper.splitNewLine(newLineAtStart);
            assertEquals(2, lines.size(), "Expected two lines for new line at start component");
            assertEquals(Component.empty(), lines.get(0), "Expected first line to be empty");
            assertEquals(Component.text("Hello"), lines.get(1), "Expected second line to be 'Hello'");
        }

        @Test
        void new_line_at_the_end() {
            final TextComponent newLineAtEnd = Component.text("Hello\n");
            final List<Component> lines = ComponentLineWrapper.splitNewLine(newLineAtEnd);
            assertEquals(2, lines.size(), "Expected two lines for new line at end component");
            assertEquals(Component.text("Hello"), lines.get(0), "Expected first line to be 'Hello'");
            assertEquals(Component.empty(), lines.get(1), "Expected second line to be empty");
        }

        @Test
        void multiple_lines_with_children() {
            final TextComponent child1 = Component.text("\nChild 1");
            final TextComponent child2 = Component.text("\nChild 2");
            final TextComponent multiLineWithChildren = Component.text("Hello,\nWorld!").append(child1).append(child2);
            final List<Component> lines = ComponentLineWrapper.splitNewLine(multiLineWithChildren);
            assertEquals(4, lines.size(), "Expected four lines for multi line with children component");
            assertEquals(Component.text("Hello,"), lines.get(0), "Expected first line to be 'Hello,'");
            assertEquals(Component.text("World!"), lines.get(1), "Expected second line to be 'World!'");
            assertEquals(Component.text("Child 1"), lines.get(2), "Expected third line to be 'Child 1'");
            assertEquals(Component.text("Child 2"), lines.get(3), "Expected fourth line to be 'Child 2'");
        }

        @Test
        void new_line_with_colour_from_minimessage() {
            final String inputString = "<red>This is only a long <yellow>enough text<br> so it gets</yellow> wrapped";
            final Component input = MiniMessage.miniMessage().deserialize(inputString);
            final List<Component> lines = ComponentLineWrapper.splitNewLine(input);
            assertEquals(2, lines.size(), "Expected two lines for minimessage component");
            assertEquals(Component.text("This is only a long ").color(NamedTextColor.RED)
                            .append(Component.text("enough text").color(NamedTextColor.YELLOW)), lines.get(0),
                    "Expected first line to be 'This is only a long enough text'");
            assertEquals(Component.empty().color(NamedTextColor.RED).append(Component.text(" so it gets")
                            .color(NamedTextColor.YELLOW)).append(Component.text(" wrapped")), lines.get(1),
                    "Expected second line to be 'so it gets wrapped'");
        }

        @Test
        void new_line_with_colour_after_br_from_minimessage() {
            final String inputString = "<red>This is only a hopefully long <yellow>enough text so it gets wrapped,<br> some edge cases are tested </yellow>later like links translation keys and key bindings.";
            final Component input = MiniMessage.miniMessage().deserialize(inputString);
            final List<Component> lines = ComponentLineWrapper.splitNewLine(input);
            assertEquals(2, lines.size(), "Expected two lines for minimessage component");
            assertEquals(Component.text("This is only a hopefully long ").color(NamedTextColor.RED)
                            .append(Component.text("enough text so it gets wrapped,").color(NamedTextColor.YELLOW)), lines.get(0),
                    "Expected first line to be 'This is only a hopefully long enough text so it gets wrapped,'");
            assertEquals(Component.empty().color(NamedTextColor.RED).append(Component.text(" some edge cases are tested ")
                            .color(NamedTextColor.YELLOW)).append(Component.text("later like links translation keys and key bindings.")), lines.get(1),
                    "Expected second line to be 'some edge cases are tested later like links translation keys and key bindings.'");
        }
    }
}
