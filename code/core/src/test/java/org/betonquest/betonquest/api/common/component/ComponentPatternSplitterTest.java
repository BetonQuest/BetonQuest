package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class ComponentPatternSplitterTest extends ComponentFixture {

    @Nested
    class split {

        private static Stream<Arguments> stringsToSplit() {
            return Stream.of(
                    Arguments.of("(?= )", "test", List.of("test")),
                    Arguments.of("(?= )", " test", List.of("", " test")),
                    Arguments.of("(?= )", "test ", List.of("test", " ")),
                    Arguments.of("(?= )", " test ", List.of("", " test", " ")),
                    Arguments.of("(?= )", " test test ", List.of("", " test", " test", " ")),
                    Arguments.of("(?= )", "  test  test  ", List.of("", " ", " test", " ", " test", " ", " ")),
                    Arguments.of("(?= )", "   ", List.of("", " ", " ", " ")),
                    Arguments.of("(?= )", "", List.of("")),
                    Arguments.of("\\n", "line1\nline2\nline3", List.of("line1", "line2", "line3"))
            );
        }

        @ParameterizedTest
        @MethodSource("stringsToSplit")
        void pattern(final String pattern, final String toSplit, final List<String> expected) {
            assertWrap(toSplit, expected, (input) -> ComponentPatternSplitter.split(Pattern.compile(pattern), input));
        }
    }

    @Nested
    class split_space {

        private static Stream<Arguments> stringsToWrap() {
            return Stream.of(
                    Arguments.of("<red>Multi<yellow> color</yellow>",
                            List.of(Component.text("Multi").color(NamedTextColor.RED),
                                    Component.empty().color(NamedTextColor.RED)
                                            .append(Component.text(" color").color(NamedTextColor.YELLOW))
                            )),
                    Arguments.of("<red>Multi <yellow>color</yellow>",
                            List.of(Component.text("Multi").color(NamedTextColor.RED),
                                    Component.text(" ").color(NamedTextColor.RED)
                                            .append(Component.text("color").color(NamedTextColor.YELLOW))
                            )),
                    Arguments.of("<red>Multi color <yellow>text to</yellow> assertWrap",
                            List.of(Component.text("Multi").color(NamedTextColor.RED),
                                    Component.text(" color").color(NamedTextColor.RED),
                                    Component.text(" ").color(NamedTextColor.RED)
                                            .append(Component.text("text").color(NamedTextColor.YELLOW)),
                                    Component.empty().color(NamedTextColor.RED)
                                            .append(Component.text(" to").color(NamedTextColor.YELLOW)),
                                    Component.empty().color(NamedTextColor.RED)
                                            .append(Component.text(" assertWrap"))
                            ))
            );
        }

        @ParameterizedTest
        @MethodSource("stringsToWrap")
        void string(final String input, final List<Component> expected) {
            final Component deserialize = MiniMessage.miniMessage().deserialize(input);
            assertWrap(deserialize, expected, component -> ComponentPatternSplitter.split(component, Pattern.compile("(?= )")));
        }
    }

    @Nested
    class split_new_line {

        private static Stream<Arguments> stringsToWrap() {
            return Stream.of(
                    Arguments.of("<red>Multi color <yellow>text<br> to</yellow> assertWrap",
                            List.of(Component.text("Multi color ").color(NamedTextColor.RED)
                                            .append(Component.text("text").color(NamedTextColor.YELLOW)),
                                    Component.empty().color(NamedTextColor.RED)
                                            .append(Component.text(" to").color(NamedTextColor.YELLOW))
                                            .append(Component.text(" assertWrap")))
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("stringsToWrap")
        void string(final String input, final List<Component> expected) {
            final Component deserialize = MiniMessage.miniMessage().deserialize(input);
            assertWrap(deserialize, expected, component -> ComponentPatternSplitter.split(component, Pattern.compile("\n")));
        }
    }
}
