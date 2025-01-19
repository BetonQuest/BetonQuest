package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SetTransformer}.
 */
class SetTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final SetTransformer TRANSFORMER = new SetTransformer();

    @Test
    void flawless() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "journalLock", "value", "true"), CONFIG);
        assertEquals("true", CONFIG.get("journalLock"), "The value was not set.");
    }
}
