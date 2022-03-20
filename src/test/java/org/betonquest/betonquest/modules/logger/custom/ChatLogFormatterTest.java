package org.betonquest.betonquest.modules.logger.custom;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link ChatLogFormatter}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class ChatLogFormatterTest {
    /**
     * The mocked plugin instance.
     */
    private final Plugin plugin;

    /**
     * Default constructor.
     */
    public ChatLogFormatterTest() {
        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("BetonQuest");
    }

    @Test
    void testChatFormatting() {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, null, Level.INFO, "Message1");
        assertLogMessage(record, "{\"text\":\"§7[§8BQ§7]§r §fMessage1\"}");
    }

    @Test
    void testChatFormattingPlugin() {
        final Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("CustomPlugin");
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, null, Level.INFO, "Message2");
        assertLogMessage(record, "{\"text\":\"§7[§8BQ | CustomPlugin§7]§r §fMessage2\"}");
    }

    @Test
    void testChatFormattingPackage() {
        final QuestPackage pack = mock(QuestPackage.class);
        when(pack.getPackagePath()).thenReturn("TestPackage");
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, pack, Level.INFO, "Message3");
        assertLogMessage(record, "{\"text\":\"§7[§8BQ§7]§r \\u003cTestPackage\\u003e §fMessage3\"}");
    }

    @Test
    void testChatFormattingException() {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(plugin, null, Level.INFO, "Message4");
        record.setThrown(new NullPointerException("Exception Message"));
        final String message = getFormattedMessage(record);
        final String start = "{\"extra\":[{\"color\":\"red\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"\\r\\n"
                + "java.lang.NullPointerException: Exception Message\\r\\n\\";
        final String end = "}},\"text\":\" Hover for Stacktrace!\"}],\"text\":\"§7[§8BQ§7]§r §fMessage4\"}";
        assertEquals(start, message.substring(0, start.length()), "The start of the log message is not correct formatted");
        assertEquals(end, message.substring(message.length() - end.length()), "The end of the log message is not correct formatted");
    }

    private void assertLogMessage(final BetonQuestLogRecord record, final String expected) {
        final String formatted = getFormattedMessage(record);
        assertEquals(expected, formatted, "Message is not correct formatted");
    }

    private String getFormattedMessage(final BetonQuestLogRecord record) {
        final ChatLogFormatter formatter = new ChatLogFormatter();
        return formatter.format(record);
    }
}
