package org.betonquest.betonquest.config.migrator.from3to4;

import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Tests for {@link DeleteEffectAny}.
 */
class DeleteEffectAnyTest extends QuestFixture {

    @Test
    void add_any_to_deleffect_actions_without_an_argument() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                actions:
                  withoutArgument: 'deleffect'
                  withConditions: 'deleffect conditions:hasEffect'
                  explicitAny: 'deleffect any'
                  selectedEffects: 'deleffect ABSORPTION,BLINDNESS'
                  nested:
                    withoutArgument: 'deleffect'
                """);
        final Quest quest = setupQuest("deleffect-actions.yml");

        new DeleteEffectAny().migrate(quest);
        quest.saveAll();

        expected.loadFromString("""
                actions:
                  withoutArgument: 'deleffect any'
                  withConditions: 'deleffect any conditions:hasEffect'
                  explicitAny: 'deleffect any'
                  selectedEffects: 'deleffect ABSORPTION,BLINDNESS'
                  nested:
                    withoutArgument: 'deleffect any'
                """);
        checkAssertion(quest, "deleffect-actions.yml");
    }
}
