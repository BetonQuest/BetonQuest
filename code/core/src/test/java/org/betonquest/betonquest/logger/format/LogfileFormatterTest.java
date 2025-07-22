package org.betonquest.betonquest.logger.format;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.logger.BetonQuestLogRecord;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link LogfileFormatter}.
 */
class LogfileFormatterTest {
    /**
     * The mocked plugin instance.
     */
    private final Plugin plugin;

    /**
     * Default constructor.
     */
    public LogfileFormatterTest() {
        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("BetonQuest");
    }

    @Test
    void testDebugFormatting() {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, "Message1", plugin);
        assertLogMessage(record, "INFO]: [BetonQuest] Message1\n");
    }

    @Test
    void testChatFormattingLogRecord() {
        final LogRecord record = new LogRecord(Level.INFO, "Message1");
        assertLogMessage(record, "INFO]: [?] Message1\n");
    }

    @Test
    void testDebugFormattingPlugin() {
        final Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("CustomPlugin");
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, "Message2", plugin);
        assertLogMessage(record, "INFO]: [CustomPlugin] Message2\n");
    }

    @Test
    void testDebugFormattingPackage() {
        final QuestPackage pack = mock(QuestPackage.class);
        when(pack.getQuestPath()).thenReturn("TestPackage");
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, "Message3", plugin, pack);
        assertLogMessage(record, "INFO]: [BetonQuest] <TestPackage> Message3\n");
    }

    @Test
    void testDebugFormattingException() {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, "Message4", plugin);
        record.setThrown(new NullPointerException("Exception Message"));
        final String message = getFormattedMessage(record);
        final String start = """
                INFO]: [BetonQuest] Message4
                java.lang.NullPointerException: Exception Message
                """;
        assertEquals(start, message.substring(0, start.length()), "The start of the log message is not correct formatted");
    }

    private void assertLogMessage(final LogRecord record, final String expected) {
        final String formatted = getFormattedMessage(record);
        assertEquals(expected, formatted, "Message is not correct formatted");
    }

    private String getFormattedMessage(final LogRecord record) {
        final LogfileFormatter formatter = new LogfileFormatter();
        return formatter.format(record).substring(19).replace("\r\n", "\n").replace("\r", "\n");
    }
}
