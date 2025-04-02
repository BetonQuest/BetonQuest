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
        originalConfig.set("events.notifyEvent", "notify Here is no langauge to change");
        final Quest quest = setupQuest(originalConfig);
        new LanguageRename().migrate(quest);
        assertEquals("notify Here is no langauge to change",
                loadPackageFile().getString("events.notifyEvent"),
                "Message without language should not be changed");
    }

    @Test
    void test_notify_change_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        originalConfig.set("events.notifyEvent", "notify {en} This is english {de} Und das deutsch!");
        final Quest quest = setupQuest(originalConfig);
        new LanguageRename().migrate(quest);
        assertEquals("notify {en-US} This is english {de-DE} Und das deutsch!",
                loadPackageFile().getString("events.notifyEvent"),
                "Message with language keys should update keys");
    }

    @Test
    void test_conversations_change_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final String option = "english player option";
        originalConfig.set("conversations.mayor.player_options.second.text.en", option);
        final String conversationsPath = "conversations.yml";
        final Quest quest = setupQuest(conversationsPath, originalConfig);
        new LanguageRename().migrate(quest);
        assertEquals(option, loadFile(conversationsPath).getString("conversations.mayor.player_options.second.text.en-US"),
                "The language key should be changed");
    }

    @Test
    void test_compass_change_language() throws IOException, InvalidConfigurationException {
        final YamlConfiguration originalConfig = new YamlConfiguration();
        final String name = "Compass Name";
        originalConfig.set("compass.eins.name.de", name);
        final Quest quest = setupQuest(originalConfig);
        new LanguageRename().migrate(quest);
        assertEquals(name, loadPackageFile().getString("compass.eins.name.de-DE"),
                "The language key should be changed");
    }
}
