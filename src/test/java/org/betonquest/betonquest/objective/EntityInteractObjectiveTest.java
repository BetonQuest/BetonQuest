package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link EntityInteractObjective} and its inner classes.
 */
@ExtendWith(BetonQuestLoggerService.class)
class EntityInteractObjectiveTest {

    /**
     * The serialized instruction for an objective of one interaction with no progress.
     */
    private static final String NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED = "1/1/1/0;";

    @Test
    void testCreateFromMinimalCountingDataWithoutInteractAppendix() {
        final EntityInteractObjective.EntityInteractData data = new EntityInteractObjective.EntityInteractData("1", null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from default data.");
    }

    @Test
    void testCreateFromExtendedCountingDataWithoutInteractAppendix() {
        final EntityInteractObjective.EntityInteractData data = new EntityInteractObjective.EntityInteractData("1/1/1/0", null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from serialized data without UUID list.");
    }

    @Test
    void testCreateFromMinimalCountingDataWithInteractAppendix() {
        final EntityInteractObjective.EntityInteractData data = new EntityInteractObjective.EntityInteractData("1;", null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from minimal data with empty UUID list.");
    }

    @Test
    void testCreateFromExtendedCountingDataWithInteractAppendix() {
        final EntityInteractObjective.EntityInteractData data = new EntityInteractObjective.EntityInteractData(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from serialized data.");
    }

    @Test
    void testCreateDataWithCollected() {
        final EntityInteractObjective.EntityInteractData data = new EntityInteractObjective.EntityInteractData("2/1/1/1;00000000-0000-0000-0000-000000000000", null, null);
        assertEquals("2/1/1/1;00000000-0000-0000-0000-000000000000", data.toString(), "Data should serialize with UUID.");
    }
}
