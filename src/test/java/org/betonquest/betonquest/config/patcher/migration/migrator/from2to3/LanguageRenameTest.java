package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests language key rename in Quests.
 */
class LanguageRenameTest extends QuestFixture {

    @Test
    void test_notify_no_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final String path = "events.notifyEvent";
        final String notifyMessage = "notify Here is no language to change";
        originalConfig.set(path, notifyMessage);
        final Quest quest = setupQuest(originalConfig);
        new LanguageRename().migrate(quest);
        assertEquals(notifyMessage, quest.getQuestConfig().getString(path),
                "Message without language should not be changed in quest");
        assertEquals(notifyMessage, loadPackageFile().getString(path),
                "Message without language should not be changed in file");
    }

    @Test
    void test_notify_change_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final String path = "events.notifyEvent";
        originalConfig.set(path, "notify {en} This is english {de} Und das deutsch!");
        final Quest quest = setupQuest(originalConfig);
        new LanguageRename().migrate(quest);
        final String expected = "notify {en-US} This is english {de-DE} Und das deutsch!";
        assertEquals(expected, quest.getQuestConfig().getString(path),
                "Message with language keys should update keys in quest");
        assertEquals(expected, loadPackageFile().getString(path),
                "Message with language keys should update keys in file");
    }

    @Test
    void test_conversations_change_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final String path = "conversations.mayor.player_options.second.text.";
        final String option = "english player option";
        originalConfig.set(path + "en", option);
        final String conversationsPath = "conversations.yml";
        final Quest quest = setupQuest(conversationsPath, originalConfig);
        new LanguageRename().migrate(quest);
        assertEquals(option, quest.getQuestConfig().getString(path + "en-US"),
                "The language key should be changed in quest");
        assertEquals(option, loadFile(conversationsPath).getString(path + "en-US"),
                "The language key should be changed in file");
    }

    @Test
    void test_compass_change_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final String path = "compass.eins.name.";
        final String name = "Compass Name";
        originalConfig.set(path + "de", name);
        final Quest quest = setupQuest(originalConfig);
        new LanguageRename().migrate(quest);
        assertEquals(name, quest.getQuestConfig().getString(path + "de-DE"),
                "The language key should be changed in quest");
        assertEquals(name, loadPackageFile().getString(path + "de-DE"),
                "The language key should be changed in file");
    }
}
