package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ComponentLineWrapperTest extends ComponentFixture {

    @Nested
    class pattern {

        @Nested
        class new_line {

            private static Stream<Arguments> stringsToWrap() {
                return Stream.of(
                        Arguments.of("",
                                List.of(Component.empty())
                        ),
                        Arguments.of("\n",
                                List.of(Component.empty(),
                                        Component.empty())
                        ),
                        Arguments.of("\n\n",
                                List.of(Component.empty(),
                                        Component.empty(),
                                        Component.empty())
                        ),
                        Arguments.of("Hello, World!",
                                List.of(Component.text("Hello, World!"))
                        ),
                        Arguments.of("Hello,\nWorld!",
                                List.of(Component.text("Hello,"),
                                        Component.text("World!"))
                        ),
                        Arguments.of("\nHello",
                                List.of(Component.empty(),
                                        Component.text("Hello"))
                        ),
                        Arguments.of("Hello\n",
                                List.of(Component.text("Hello"),
                                        Component.empty())
                        ),
                        Arguments.of("<red>This is only a long <yellow>enough text<br> so it gets</yellow> wrapped",
                                List.of(Component.text("This is only a long ").color(NamedTextColor.RED)
                                                .append(Component.text("enough text").color(NamedTextColor.YELLOW)),
                                        Component.empty().color(NamedTextColor.RED).append(Component.text(" so it gets")
                                                .color(NamedTextColor.YELLOW)).append(Component.text(" wrapped")))
                        ),
                        Arguments.of("<red>This is only a hopefully long <yellow>enough text so it gets wrapped,<br> some edge cases are tested </yellow>later like links translation keys and key bindings.",
                                List.of(Component.text("This is only a hopefully long ").color(NamedTextColor.RED)
                                                .append(Component.text("enough text so it gets wrapped,").color(NamedTextColor.YELLOW)),
                                        Component.empty().color(NamedTextColor.RED).append(Component.text(" some edge cases are tested ")
                                                .color(NamedTextColor.YELLOW)).append(Component.text("later like links translation keys and key bindings.")))
                        )
                );
            }

            private static Stream<Arguments> componentsToWrap() {
                return Stream.of(
                        Arguments.of(Component.text("Hello,\nWorld!").append(Component.text("\nChild 1")).append(Component.text("\nChild 2")),
                                List.of(Component.text("Hello,"),
                                        Component.text("World!"),
                                        Component.text("Child 1"),
                                        Component.text("Child 2"))
                        )
                );
            }

            @ParameterizedTest
            @MethodSource("stringsToWrap")
            void wrap_string(final String input, final List<Component> expected) {
                final Component deserialize = MiniMessage.miniMessage().deserialize(input);
                assertWrap(deserialize, expected, ComponentLineWrapper::splitNewLine);
            }

            @ParameterizedTest
            @MethodSource("componentsToWrap")
            void wrap_component(final Component input, final List<Component> expected) {
                assertWrap(input, expected, ComponentLineWrapper::splitNewLine);
            }
        }
    }

    @Nested
    class width {

        @Nested
        class string {

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
            void wrap_line(final String input, final List<String> expected, final int lineLength) throws IOException {

                final Component deserialize = MiniMessage.miniMessage().deserialize(input);
                final FixedComponentLineWrapper wrapper = new FixedComponentLineWrapper(getFontRegistry(), lineLength);
                assertWrap(deserialize, expected, (component) -> {
                    final List<String> results = new ArrayList<>();
                    for (final Component result : wrapper.splitWidth(component)) {
                        if (result instanceof final TextComponent textComponent) {
                            results.add(textComponent.content());
                        } else {
                            fail("Expected TextComponent but got: " + component.getClass().getSimpleName());
                        }
                    }
                    return results;
                });
            }
        }

        @Nested
        class component {

            private static Stream<Arguments> stringsToWrap() {
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
                                List.of(Component.text("This is only a hopefully long").color(NamedTextColor.RED),
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
                                                .append(Component.text(" some edge cases are tested").color(NamedTextColor.YELLOW))
                                                .append(Component.text(" later like links translation keys and key bindings."))
                                ),
                                500),
                        Arguments.of("How are you? And if I continue writing, it should now break bold text actually correct at the end of the line!",
                                List.of(Component.text("How are you? And if I continue writing, it should now break bold"),
                                        Component.text("text actually correct at the end of the line!")
                                ),
                                320),
                        Arguments.of("<bold>How are you? And if I continue writing, it should now break bold text actually correct at the end of the line</bold>!",
                                List.of(Component.text("How are you? And if I continue writing, it should now").decoration(TextDecoration.BOLD, true),
                                        Component.text("break bold text actually correct at the end of the").decoration(TextDecoration.BOLD, true),
                                        Component.empty().append(Component.text("line").decoration(TextDecoration.BOLD, true)).append(Component.text("!"))
                                ),
                                320),
                        Arguments.of("Hello <rainbow>test</rainbow>!",
                                List.of(Component.text("Hello"),
                                        Component.empty()
                                                .append(Component.text("t").color(TextColor.fromHexString("#f3801f")))
                                                .append(Component.text("e").color(TextColor.fromHexString("#4bff2c")))
                                                .append(Component.text("s").color(TextColor.fromHexString("#0c80e0")))
                                                .append(Component.text("t").color(TextColor.fromHexString("#b401d3")))
                                                .append(Component.text("!"))
                                ),
                                40));
            }

            @ParameterizedTest
            @MethodSource("stringsToWrap")
            void wrap_line(final String input, final List<Component> expected, final int lineLength) throws IOException {
                final Component deserialize = MiniMessage.miniMessage().deserialize(input);
                final FixedComponentLineWrapper wrapper = new FixedComponentLineWrapper(getFontRegistry(), lineLength);
                assertWrap(deserialize, expected, wrapper::splitWidth);
            }
        }
    }
}
