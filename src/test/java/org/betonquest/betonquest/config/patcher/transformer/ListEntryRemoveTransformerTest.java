package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ListEntryRemoveTransformer}.
 */
class ListEntryRemoveTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final ListEntryRemoveTransformer TRANSFORMER = new ListEntryRemoveTransformer();

    @Test
    void flawless() throws PatchException {
        final List<String> value = config.getStringList("section.myList");
        TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.myList", "entry", "removedEntry")), config);
        value.remove("removedEntry");
        assertEquals(value, config.get("section.myList"), "The list entry was not removed.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.invalid", "entry", "removedEntry")), config));
    }

    @Test
    void throws_exception_on_no_matching_entry() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.myList", "entry", "invalidEntry")), config));
    }
}
