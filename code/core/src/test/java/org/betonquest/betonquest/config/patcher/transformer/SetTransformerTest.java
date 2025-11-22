package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
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
        TRANSFORMER.transform(new PatcherOptions(Map.of("key", "journalLock", "value", "true")), config);
        assertEquals("true", config.get("journalLock"), "The value was not set.");
    }
}
