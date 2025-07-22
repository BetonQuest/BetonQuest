package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ListEntryAddTransformer}.
 */
class ListEntryAddTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final ListEntryAddTransformer TRANSFORMER = new ListEntryAddTransformer();

    @Test
    void flawless_add_default() throws PatchException {
        final List<String> value = config.getStringList("section.myList");
        TRANSFORMER.transform(new PatcherOptions(Map.of("type", "LIST_ENTRY_ADD", "key", "section.myList", "entry", "newEntry")), config);
        value.add("newEntry");
        assertEquals(value, config.getStringList("section.myList"), "The list entry was not added.");
    }

    @Test
    void flawless_add_last() throws PatchException {
        final List<String> value = config.getStringList("section.myList");
        TRANSFORMER.transform(new PatcherOptions(Map.of("type", "LIST_ENTRY_ADD", "key", "section.myList", "entry", "newEntry", "position", "LAST")), config);
        value.add("newEntry");
        assertEquals(value, config.getStringList("section.myList"), "The list entry was not added.");
    }

    @Test
    void flawless_add_first() throws PatchException {
        final List<String> value = config.getStringList("section.myList");
        TRANSFORMER.transform(new PatcherOptions(Map.of("type", "LIST_ENTRY_ADD", "key", "section.myList", "entry", "newEntry", "position", "FIRST")), config);
        value.add(0, "newEntry");
        assertEquals(value, config.getStringList("section.myList"), "The list entry was not added.");
    }

    @Test
    void flawless_add_invalid_position() throws PatchException {
        final List<String> value = config.getStringList("section.myList");
        TRANSFORMER.transform(new PatcherOptions(Map.of("type", "LIST_ENTRY_ADD", "key", "section.myList", "entry", "newEntry", "position", "rubbish")), config);
        value.add("newEntry");
        assertEquals(value, config.getStringList("section.myList"), "The list entry was not added.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(new PatcherOptions(Map.of("type", "LIST_ENTRY_ADD", "key", "section.invalid", "entry", "newEntry")), config));
    }
}
