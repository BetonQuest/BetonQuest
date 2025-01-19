package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SetTransformer}.
 */
class ValueRenameTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final ValueRenameTransformer TRANSFORMER = new ValueRenameTransformer();

    @Test
    void flawless() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "section.testKey", "oldValueRegex", "test", "newValue", "newTest"), CONFIG);
        assertEquals("newTest", CONFIG.get("section.testKey"), "The value was not renamed.");
    }

    @Test
    void throws_exception_on_non_existing() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("key", "section.invalidKey", "oldValueRegex", "test", "newValue", "newTest"), CONFIG));
    }

    @Test
    void throws_exception_on_no_matching_regex() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("key", "section.testKey", "oldValueRegex", "noMatchRegex", "newValue", "newTest"), CONFIG));
    }
}
