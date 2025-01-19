package org.betonquest.betonquest.util;

import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class test some utility class methods.
 */
@ExtendWith(BetonQuestLoggerService.class)
class UtilsTest {

    private void prepareConfig(final MockedStatic<Config> config) {
        config.when(() -> Config.getConfigString("journal.lines_per_page")).thenReturn("13");
        config.when(() -> Config.getConfigString("journal.chars_per_line")).thenReturn("19");
    }

    @Test
    void testPagesFromString() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            prepareConfig(config);
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
}
