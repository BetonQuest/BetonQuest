package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for 'particle' action to 'el_particle' migration.
 */
class EffectLibParticleRenameTest extends QuestFixture {

    @Test
    void migrate() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                   play: particle beton loc:100;200;300;world
                   other: notify "test"
                   complex: particle other loc:0;0;0;world private
                """);
        final Quest quest = setupQuest("actions.yml");
        new EffectLibParticleRename().migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                actions:
                   play: el_particle beton loc:100;200;300;world
                   other: notify "test"
                   complex: el_particle other loc:0;0;0;world private
                """);
        checkAssertion(quest, "actions.yml");
    }
}
