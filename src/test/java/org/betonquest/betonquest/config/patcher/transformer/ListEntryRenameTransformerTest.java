package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
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
        final List<String> value = config.getStringList("section.myList");
        TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.myList", "oldEntryRegex", "currentEntry", "newEntry", "newEntry")), config);
        value.replaceAll(s -> s.replaceAll("currentEntry", "newEntry"));
        assertEquals(value, config.get("section.myList"), "The list entry was not renamed.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.invalid", "oldEntryRegex", "currentEntry", "newEntry", "newEntry")), config));
    }

    @Test
    void throws_exception_on_no_matching_regex() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(new PatcherOptions(Map.of("key", "section.myList", "oldEntryRegex", "invalidRegex", "newEntry", "newEntry")), config));
    }
}
