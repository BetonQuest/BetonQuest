package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
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
        TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.myList")), config);
        assertNull(config.get("section.myList"), "The list was not removed.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.invalid")), config));
    }
}
