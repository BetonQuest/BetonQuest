package org.betonquest.betonquest.util;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class test some utility class methods.
 */
@ExtendWith(BetonQuestLoggerService.class)
class UtilsTest {

    private void prepareConfig() {
        final FileConfigAccessor config = mock(FileConfigAccessor.class);
        when(config.getInt("journal.format.lines_per_page")).thenReturn(13);
        when(config.getInt("journal.format.chars_per_line")).thenReturn(19);
        final BetonQuest betonQuest = BetonQuest.getInstance();
        when(betonQuest.getPluginConfig()).thenReturn(config);
    }

    @Test
    void testPagesFromString() {
        prepareConfig();
        final String journalText = "&aActive Quest: &aFlint &1wants you to visit the Farm located at 191, 23, -167!";

        final List<String> journalTextFormatted = new ArrayList<>();
        journalTextFormatted.add("""
                &aActive Quest: &aFlint
                &1wants you to visit
                the Farm located at
                191, 23, -167!
                """);

        final List<String> journal = Utils.pagesFromString(journalText);
        assertEquals(journalTextFormatted, journal, "Formatted text does not equal expected result!");
    }
}
