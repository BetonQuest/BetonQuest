package org.betonquest.betonquest.quest.objective.interact;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EntityInteractObjective} and its inner classes.
 */
@ExtendWith(BetonQuestLoggerExtension.class)
class EntityInteractObjectiveTest {

    /**
     * The serialized instruction for an objective of one interaction with no progress.
     */
    private static final String NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED = "1/1/1/0;";

    /**
     * The MockedStatic instance of {@link BetonQuestLogger} class.
     */
    private MockedStatic<BetonQuest> staticBetonQuest;

    @BeforeEach
    void setUp(final BetonQuestApi betonQuestApi) {
        final BetonQuest betonQuest = mock(BetonQuest.class);
        lenient().when(betonQuest.getBetonQuestApi()).thenReturn(betonQuestApi);
        staticBetonQuest = mockStatic(BetonQuest.class);
        staticBetonQuest.when(BetonQuest::getInstance).thenReturn(betonQuest);
    }

    @AfterEach
    void tearDown() {
        staticBetonQuest.close();
    }

    @Test
    void create_from_minimal_counting_data_without_interact_appendix() throws QuestException {
        final EntityInteractData data = new EntityInteractData("1", null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from default data.");
    }

    @Test
    void create_from_extended_counting_data_without_interact_appendix() throws QuestException {
        final EntityInteractData data = new EntityInteractData("1/1/1/0", null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from serialized data without UUID list.");
    }

    @Test
    void create_from_minimal_counting_data_with_interact_appendix() throws QuestException {
        final EntityInteractData data = new EntityInteractData("1;", null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from minimal data with empty UUID list.");
    }

    @Test
    void create_from_extended_counting_data_with_interact_appendix() throws QuestException {
        final EntityInteractData data = new EntityInteractData(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, null, null);
        assertEquals(NO_PROGRESS_ON_SIMPLE_INTERACT_SERIALIZED, data.toString(), "Data should serialize from serialized data.");
    }

    @Test
    void create_data_with_collected() throws QuestException {
        final EntityInteractData data = new EntityInteractData("2/1/1/1;00000000-0000-0000-0000-000000000000", null, null);
        assertEquals("2/1/1/1;00000000-0000-0000-0000-000000000000", data.toString(), "Data should serialize with UUID.");
    }
}
