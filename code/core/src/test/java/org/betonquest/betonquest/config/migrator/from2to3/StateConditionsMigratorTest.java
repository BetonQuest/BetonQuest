package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class StateConditionsMigratorTest extends QuestFixture {

    @Test
    void migrate() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                conditions:
                  isFlying: fly
                  isSneaking: sneak
                  isBurning: burning
                  other: other_condition
                """);
        expected.loadFromString("""
                conditions:
                  isFlying: state GLIDING
                  isSneaking: state SNEAKING
                  isBurning: state FIRE
                  other: other_condition
                """);

        final Quest quest = setupQuest("conditions.yml");
        new StateConditionsMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "conditions.yml");
    }
}
