package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link KeyRenameTransformer}.
 */
class KeyRenameTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final KeyRenameTransformer TRANSFORMER = new KeyRenameTransformer();

    @Test
    void flawless() throws PatchException {
        final Object value = config.get("section.test");
        TRANSFORMER.transform(new PatcherOptions(Map.of("oldKey", "section.test", "newKey", "section.testNew")), config);
        assertNull(config.get("section.test"), "The previous list was not removed.");
        assertEquals(value, config.get("section.testNew"), "The new list was not set.");
    }

    @Test
    void flawless_on_list() throws PatchException {
        final Object value = config.get("section.myList");
        TRANSFORMER.transform(new PatcherOptions(Map.of("oldKey", "section.myList", "newKey", "section.newList")), config);
        assertNull(config.get("section.myList"), "The previous list was not removed.");
        assertEquals(value, config.get("section.newList"), "The new list was not set.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(new PatcherOptions(Map.of("oldKey", "section.invalid", "newKey", "section.testNew")), config));
    }
}
