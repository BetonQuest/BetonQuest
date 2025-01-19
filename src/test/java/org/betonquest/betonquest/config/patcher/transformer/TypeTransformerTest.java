package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link TypeTransformer}.
 */
class TypeTransformerTest extends TransformersFixture {
    /**
     * The transformer that is tested.
     */
    public static final TypeTransformer TRANSFORMER = new TypeTransformer();

    @Test
    void flawless_String_to_Boolean() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "section.booleanString", "newType", "boolean"), CONFIG);
        assertEquals(CONFIG.get("section.booleanString"), true, "The String was not converted to a boolean.");
    }

    @Test
    void flawless_String_to_Int() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "section.int", "newType", "integer"), CONFIG);
        assertEquals(CONFIG.get("section.int"), 2, "The String was not converted to an integer.");
    }

    @Test
    void flawless_String_to_Float() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "section.float", "newType", "float"), CONFIG);
        assertEquals(CONFIG.get("section.float"), 2.5F, "The String was not converted to a float.");
    }

    @Test
    void flawless_String_to_Double() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "section.double", "newType", "double"), CONFIG);
        assertEquals(CONFIG.get("section.double"), 2.123_456_789_123_456_7D, "The String was not converted to a double.");
    }

    @Test
    void flawless_Boolean_to_String() throws PatchException {
        TRANSFORMER.transform(Map.of("key", "section.boolean", "newType", "string"), CONFIG);
        assertEquals(CONFIG.get("section.boolean"), "true", "The boolean was not converted to a string.");
    }

    @Test
    void throws_exception_on_invalid() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("key", "section.invalid", "newType", "boolean"), CONFIG));
    }

    @Test
    void throws_exception_on_unsupported_type() {
        assertThrows(PatchException.class, () -> TRANSFORMER.transform(Map.of("key", "section.boolean", "newType", "invalid"), CONFIG));
    }
}
