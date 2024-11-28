package org.betonquest.betonquest.modules.logger.format;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link ChatFormatter}.
 */
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class ChatFormatterTest {
    /**
     * The message to send.
     */
    public static final String MESSAGE = "Message";

    /**
     * The formatted sent message.
     */
    public static final String FORMATTED_MESSAGE = "§f" + MESSAGE + "\"}";

    /**
     * The mocked plugin instance.
     */
    private final Plugin plugin;

    /**
     * The mocked plugin instance from an extension.
     */
    private final Plugin pluginExtension;

    /**
     * Default constructor.
     */
    public ChatFormatterTest() {
        plugin = mock(Plugin.class);
        pluginExtension = mock(Plugin.class);
        when(plugin.getName()).thenReturn("BetonQuest");
        when(pluginExtension.getName()).thenReturn("Extension");
    }

    @Test
    void testChatFormatting() {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, MESSAGE, plugin);
        final String expected1 = "{\"text\":\"" + FORMATTED_MESSAGE;
        final String expected2 = "{\"text\":\"§7[§8BQ§7]§r " + FORMATTED_MESSAGE;
        final String expected3 = "{\"text\":\"§7[§8BetonQuest§7]§r " + FORMATTED_MESSAGE;
        assertLogMessage(ChatFormatter.PluginDisplayMethod.NONE, null, null, record, expected1);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.PLUGIN, plugin, "BQ", record, expected2);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.PLUGIN, plugin, null, record, expected3);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN, plugin, "BQ", record, expected2);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN, plugin, null, record, expected3);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, "BQ", record, expected2);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, null, record, expected3);
    }

    @Test
    void testChatFormattingLogRecord() {
        final LogRecord record = new LogRecord(Level.INFO, MESSAGE);
        final String expected1 = "{\"text\":\"" + FORMATTED_MESSAGE;
        final String expected2 = "{\"text\":\"§7[§8?§7]§r " + FORMATTED_MESSAGE;
        final String expected3 = "{\"text\":\"§7[§8BQ§7]§r " + FORMATTED_MESSAGE;
        final String expected4 = "{\"text\":\"§7[§8BetonQuest§7]§r " + FORMATTED_MESSAGE;
        final String expected5 = "{\"text\":\"§7[§8BQ | ?§7]§r " + FORMATTED_MESSAGE;
        final String expected6 = "{\"text\":\"§7[§8BetonQuest | ?§7]§r " + FORMATTED_MESSAGE;
        assertLogMessage(ChatFormatter.PluginDisplayMethod.NONE, null, null, record, expected1);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.PLUGIN, plugin, "BQ", record, expected2);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.PLUGIN, plugin, null, record, expected2);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN, plugin, "BQ", record, expected3);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN, plugin, null, record, expected4);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, "BQ", record, expected5);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, null, record, expected6);
    }

    @Test
    void testChatFormattingPlugin() {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, MESSAGE, pluginExtension);
        final String expected1 = "{\"text\":\"" + FORMATTED_MESSAGE;
        final String expected2 = "{\"text\":\"§7[§8Extension§7]§r " + FORMATTED_MESSAGE;
        final String expected3 = "{\"text\":\"§7[§8BQ§7]§r " + FORMATTED_MESSAGE;
        final String expected4 = "{\"text\":\"§7[§8BetonQuest§7]§r " + FORMATTED_MESSAGE;
        final String expected5 = "{\"text\":\"§7[§8BQ | Extension§7]§r " + FORMATTED_MESSAGE;
        final String expected6 = "{\"text\":\"§7[§8BetonQuest | Extension§7]§r " + FORMATTED_MESSAGE;
        assertLogMessage(ChatFormatter.PluginDisplayMethod.NONE, null, null, record, expected1);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.PLUGIN, plugin, "BQ", record, expected2);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.PLUGIN, plugin, null, record, expected2);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN, plugin, "BQ", record, expected3);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN, plugin, null, record, expected4);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, "BQ", record, expected5);
        assertLogMessage(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, null, record, expected6);
    }

    @Test
    void testChatFormattingPackage() {
        final QuestPackage pack = mock(QuestPackage.class);
        when(pack.getQuestPath()).thenReturn("TestPackage");
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, MESSAGE, plugin, pack);
        final String expected = "{\"text\":\"\\u003cTestPackage\\u003e " + FORMATTED_MESSAGE;
        assertLogMessage(ChatFormatter.PluginDisplayMethod.NONE, null, null, record, expected);
    }

    @Test
    void testChatFormattingException() {
        final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.INFO, MESSAGE, plugin);
        record.setThrown(new NullPointerException("Exception Message"));
        final String message = getFormattedMessage(ChatFormatter.PluginDisplayMethod.NONE, null, null, record);
        final String start = "{\"extra\":[{\"color\":\"red\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"\\n"
                + "java.lang.NullPointerException: Exception Message\\n\\";
        final String end = "}},\"text\":\" Hover for Stacktrace!\"}],\"text\":\"" + FORMATTED_MESSAGE;
        assertEquals(start, message.substring(0, start.length()), "The start of the log message is not correct formatted");
        assertEquals(end, message.substring(message.length() - end.length()), "The end of the log message is not correct formatted");
    }

    @Test
    void testConstructorThrowsException() {
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new ChatFormatter(ChatFormatter.PluginDisplayMethod.PLUGIN, null, "BQ"),
                "Expected IllegalArgumentException is thrown");
        assertEquals("Plugin must be non null if displayMethod is not NONE", exception.getMessage(), "Expected other exception message");
    }

    @Test
    void testLevelColors() {
        final ChatFormatter formatter = new ChatFormatter();
        assertEquals("{\"text\":\"§4\"}", formatter.format(new LogRecord(Level.OFF, "")), "Expected color 4");
        assertEquals("{\"text\":\"§4\"}", formatter.format(new LogRecord(Level.SEVERE, "")), "Expected color 4");
        assertEquals("{\"text\":\"§c\"}", formatter.format(new LogRecord(Level.WARNING, "")), "Expected color c");
        assertEquals("{\"text\":\"§f\"}", formatter.format(new LogRecord(Level.INFO, "")), "Expected color f");
        assertEquals("{\"text\":\"§7\"}", formatter.format(new LogRecord(Level.CONFIG, "")), "Expected color 7");
        assertEquals("{\"text\":\"§7\"}", formatter.format(new LogRecord(Level.FINE, "")), "Expected color 7");
        assertEquals("{\"text\":\"§7\"}", formatter.format(new LogRecord(Level.FINER, "")), "Expected color 7");
        assertEquals("{\"text\":\"§7\"}", formatter.format(new LogRecord(Level.FINEST, "")), "Expected color 7");
        assertEquals("{\"text\":\"§7\"}", formatter.format(new LogRecord(Level.ALL, "")), "Expected color 7");
    }

    private void assertLogMessage(final ChatFormatter.PluginDisplayMethod displayMethod, final Plugin plugin,
                                  final String shortName, final LogRecord record, final String expected) {
        final String formatted = getFormattedMessage(displayMethod, plugin, shortName, record);
        assertEquals(expected, formatted, "Message is not correct formatted");
    }

    private String getFormattedMessage(final ChatFormatter.PluginDisplayMethod displayMethod, final Plugin plugin,
                                       final String shortName, final LogRecord record) {
        final ChatFormatter formatter = new ChatFormatter(displayMethod, plugin, shortName);
        return formatter.format(record).replace("\\r\\n", "\\n").replace("\\r", "\\n");
    }
}
