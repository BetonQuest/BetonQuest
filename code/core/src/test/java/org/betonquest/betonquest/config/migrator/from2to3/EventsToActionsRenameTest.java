package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class EventsToActionsRenameTest extends QuestFixture {

    @Test
    void migrate() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                # Comment 1
                events:
                  # Comment 2
                  foo: bar
                """);
        expected.loadFromString("""
                # Comment 1
                actions:
                  # Comment 2
                  foo: bar
                """);

        final Quest quest = setupQuest("conv.yml");
        new EventsToActionsRename().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "conv.yml");
    }

}
