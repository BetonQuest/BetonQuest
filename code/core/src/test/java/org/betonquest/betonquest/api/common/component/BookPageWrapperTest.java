package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Tests for the {@link BookPageWrapper}.
 */
class BookPageWrapperTest extends ComponentFixture {
    private static Stream<Arguments> pagesToWrap() {
        return Stream.of(
                Arguments.of("<green>Active Quest: <green>Flint <dark_blue>wants you to visit the Farm located at 191, 23, -167!",
                        List.of(Component.empty()
                                .append(Component.text("Active Quest: Flint").color(NamedTextColor.GREEN)).append(Component.newline())
                                .append(Component.text("wants you to visit").color(NamedTextColor.DARK_BLUE)).append(Component.newline())
                                .append(Component.text("the Farm located at").color(NamedTextColor.DARK_BLUE)).append(Component.newline())
                                .append(Component.text("191, 23, -167!").color(NamedTextColor.DARK_BLUE))),
                        100, 14),
                Arguments.of("<green>Active Quest: <green>Flint <dark_blue>wants you to visit the Farm located at 191, 23, -167!",
                        List.of(Component.empty()
                                        .append(Component.text("Active Quest: Flint").color(NamedTextColor.GREEN)).append(Component.newline())
                                        .append(Component.text("wants you to visit the").color(NamedTextColor.DARK_BLUE)).append(Component.newline())
                                        .append(Component.text("Farm located at 191,").color(NamedTextColor.DARK_BLUE)),
                                Component.text("23, -167!").color(NamedTextColor.DARK_BLUE)),
                        114, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("pagesToWrap")
    void string(final String input, final List<Component> expected, final int maxLineWidth, final int maxLines) throws IOException {
        final Component deserialize = MiniMessage.miniMessage().deserialize(input);
        final FontRegistry fontRegistry = getFontRegistry();
        assertWrap(deserialize, expected, component -> new BookPageWrapper(fontRegistry, maxLineWidth, maxLines).splitPages(component));
    }
}
