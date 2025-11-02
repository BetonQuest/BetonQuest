package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests language key rename in Quests.
 */
class LanguageRenameTest extends QuestFixture {

    @Test
    void notify_no_language() throws IOException, InvalidConfigurationException {
        final String path = "events.notifyEvent";
        final String notifyMessage = "notify Here is no language to change";
        original.set(path, notifyMessage);
        checkStringEquality(notifyMessage, path, "Message without language should not be changed");
    }

    @Test
    void notify_change_language() throws IOException, InvalidConfigurationException {
        final String path = "events.notifyEvent";
        original.set(path, "notify {en} This is english {de} Und das deutsch!");
        final String expected = "notify {en-US} This is english {de-DE} Und das deutsch!";
        checkStringEquality(expected, path, "Message with language keys should update keys");
    }

    @Test
    void conversations_keep_custom_language() throws IOException, InvalidConfigurationException {
        final String path = "conversations.mayor.player_options.second.text.";
        final String option = "pirate player option *arr*";
        original.set(path + "pirate", option);
        checkStringEquality(option, path + "pirate", "The custom language key should not be changed");
    }

    @Test
    void compass_change_language() throws IOException, InvalidConfigurationException {
        final String path = "compass.eins.name.";
        final String name = "Compass Name";
        original.set(path + "de", name);
        checkStringEquality(name, path + "de-DE", "The language key should be changed");
    }

    private void checkStringEquality(final String expected, final String path, final String message) throws IOException, InvalidConfigurationException {
        final Quest quest = setupQuest();
        new LanguageRename().migrate(quest);
        quest.saveAll();
        assertEquals(expected, quest.getQuestConfig().getString(path), message + " in quest");
        assertEquals(expected, loadPackageFile().getString(path), message + " in file");
    }
}
