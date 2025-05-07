package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComponentLineWrapperTest {
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
            final String inputString = "<red>This is only a long <yellow>enougth text<br> so it gets</yellow> wrapped";
            final Component input = MiniMessage.miniMessage().deserialize(inputString);
            final List<Component> lines = ComponentLineWrapper.splitNewLine(input);
            assertEquals(2, lines.size(), "Expected two lines for minimessage component");
            assertEquals(Component.text("This is only a long ").color(NamedTextColor.RED)
                            .append(Component.text("enougth text").color(NamedTextColor.YELLOW)), lines.get(0),
                    "Expected first line to be 'This is only a long enougth text'");
            assertEquals(Component.empty().color(NamedTextColor.RED).append(Component.text(" so it gets")
                            .color(NamedTextColor.YELLOW)).append(Component.text(" wrapped")), lines.get(1),
                    "Expected second line to be 'so it gets wrapped'");
        }
    }
}
