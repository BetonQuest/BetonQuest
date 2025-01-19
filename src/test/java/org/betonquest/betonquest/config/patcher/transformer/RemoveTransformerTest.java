package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ListEntryAddTransformer}.
 */
class RemoveTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final RemoveTransformer TRANSFORMER = new RemoveTransformer();

    @Test
    void flawless() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "section.myList"), CONFIG);
        assertNull(CONFIG.get("section.myList"), "The list was not removed.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("key", "section.invalid"), CONFIG));
    }
}
