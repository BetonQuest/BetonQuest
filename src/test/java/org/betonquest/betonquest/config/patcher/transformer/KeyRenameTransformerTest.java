package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
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
        final Object value = CONFIG.get("section.test");
        TRANSFORMER.transform(Map.of("oldKey", "section.test", "newKey", "section.testNew"), CONFIG);
        assertNull(CONFIG.get("section.test"), "The previous list was not removed.");
        assertEquals(value, CONFIG.get("section.testNew"), "The new list was not set.");
    }

    @Test
    void flawless_on_list() throws PatchException {
        final Object value = CONFIG.get("section.myList");
        TRANSFORMER.transform(Map.of("oldKey", "section.myList", "newKey", "section.newList"), CONFIG);
        assertNull(CONFIG.get("section.myList"), "The previous list was not removed.");
        assertEquals(value, CONFIG.get("section.newList"), "The new list was not set.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("oldKey", "section.invalid", "newKey", "section.testNew"), CONFIG));
    }
}
