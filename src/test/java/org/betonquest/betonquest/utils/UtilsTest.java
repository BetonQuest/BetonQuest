package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.config.Config;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("PMD.CommentRequired")
public class UtilsTest {

    public UtilsTest() {
    }

    public MockedStatic<Config> prepareConfig() {
        final MockedStatic<Config> config = Mockito.mockStatic(Config.class);
        config.when(() -> Config.getString("config.journal.lines_per_page")).thenReturn("13");
        config.when(() -> Config.getString("config.journal.chars_per_line")).thenReturn("19");
        return config;
    }

    @Test
    public void testPagesFromString() {
        try (MockedStatic<Config> config = prepareConfig()) {
            final String journalText = "&aActive Quest: &aFlint &1wants you to visit the Farm located at 191, 23, -167!";

            final List<String> journalTextFormatted = new ArrayList<>();
            journalTextFormatted.add("&aActive Quest: &aFlint\n" + "&1wants you to visit\n" + "the Farm located at\n" + "191, 23, -167!\n");

            final List<String> journal = Utils.pagesFromString(journalText);
            assertEquals(journalTextFormatted, journal, "Formatted text does not equal expected result!");
        }
    }
}
