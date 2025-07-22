package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.key.Key;
import org.betonquest.betonquest.api.common.component.font.DefaultFont;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ComponentFixture {
    public <T, R> void assertWrap(final T input, final List<R> expected, final Function<T, List<R>> wrapper) {
        final List<R> result = wrapper.apply(input);
        assertEquals(expected, result, "The arrays should equal each other");
    }

    public FontRegistry getFontRegistry() {
        final Key defaultKey = Key.key("default");
        final FontRegistry fontRegistry = new FontRegistry(defaultKey);
        final DefaultFont defaultFont = new DefaultFont();
        fontRegistry.registerFont(defaultKey, defaultFont);
        return fontRegistry;
    }
}
