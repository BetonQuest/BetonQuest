package org.betonquest.betonquest.config.migrator.from3to4;

import org.betonquest.betonquest.config.migrator.from2to3.BurnDurationArgumentName;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for {@link BurnDurationArgumentName}.
 */
class BurnDurationArgumentNameTest extends QuestFixture {

    @Test
    void refactor_burn_action_duration_correctly() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                actions:
                  burnAction: 'burn duration:5'
                  another: 'burn duration:22 conditions:something,else'
                  somePlace: 'burn conditions:anything duration:%any.placeholder%'
                  deeper:
                    burnAction: 'burn conditions:anything,really duration:5'
                    another: 'burn duration:22'
                """);
        final Quest quest = setupQuest("burn-actions.yml");
        new BurnDurationArgumentName().migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                actions:
                  burnAction: 'burn 5'
                  another: 'burn 22 conditions:something,else'
                  somePlace: 'burn %any.placeholder% conditions:anything'
                  deeper:
                    burnAction: 'burn 5 conditions:anything,really'
                    another: 'burn 22'
                """);
        checkAssertion(quest, "burn-actions.yml");
    }
}
