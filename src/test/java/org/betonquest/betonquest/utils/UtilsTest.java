package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
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

    /**
     * Default constructor.
     */
    public UtilsTest() {
    }

    private MockedStatic<Config> prepareConfig() {
        final MockedStatic<Config> config = Mockito.mockStatic(Config.class);
        config.when(() -> Config.getString("config.journal.lines_per_page")).thenReturn("13");
        config.when(() -> Config.getString("config.journal.chars_per_line")).thenReturn("19");
        return config;
    }

    @Test
    void testPagesFromString() {
        try (MockedStatic<Config> config = prepareConfig()) {
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
