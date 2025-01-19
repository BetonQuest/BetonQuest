package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ListEntryRenameTransformer}.
 */
class ListEntryRenameTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final ListEntryRenameTransformer TRANSFORMER = new ListEntryRenameTransformer();

    @Test
    void flawless() throws PatchException {
        final List<String> value = CONFIG.getStringList("section.myList");
        TRANSFORMER.transform(Map.of("key", "section.myList", "oldEntryRegex", "currentEntry", "newEntry", "newEntry"), CONFIG);
        value.replaceAll(s -> s.replaceAll("currentEntry", "newEntry"));
        assertEquals(value, CONFIG.get("section.myList"), "The list entry was not renamed.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("key", "section.invalid", "oldEntryRegex", "currentEntry", "newEntry", "newEntry"), CONFIG));
    }

    @Test
    void throws_exception_on_no_matching_regex() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("key", "section.myList", "oldEntryRegex", "invalidRegex", "newEntry", "newEntry"), CONFIG));
    }
}
