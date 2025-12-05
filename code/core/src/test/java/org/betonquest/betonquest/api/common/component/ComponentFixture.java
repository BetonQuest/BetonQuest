package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.key.Key;
import org.betonquest.betonquest.api.common.component.font.Font;
import org.betonquest.betonquest.api.common.component.font.FontIndexFileFormat;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ComponentFixture {

    public <T, R> void assertWrap(final T input, final List<R> expected, final Function<T, List<R>> wrapper) {
        final List<R> result = wrapper.apply(input);
        assertEquals(expected, result, "The arrays should equal each other");
    }

    public FontRegistry getFontRegistry() throws IOException {
        final Key defaultKey = Key.key("default");
        final FontRegistry fontRegistry = new FontRegistry(defaultKey);
        final Path path = Path.of("src/main/resources/fonts/default.font.bin");
        final Font defaultFont = FontIndexFileFormat.BINARY.read(Files.newInputStream(path));
        fontRegistry.registerFont(defaultKey, defaultFont);
        return fontRegistry;
    }
}
