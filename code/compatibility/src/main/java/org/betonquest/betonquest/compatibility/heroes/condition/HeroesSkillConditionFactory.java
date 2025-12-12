package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link HeroesSkillCondition}s from {@link Instruction}s.
 */
public class HeroesSkillConditionFactory implements PlayerConditionFactory {
    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory    the logger factory.
     * @param data             the data used for primary server access.
     * @param characterManager the {@link CharacterManager} of the Heroes plugin.
     */
    public HeroesSkillConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                                       final CharacterManager characterManager) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.characterManager = characterManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> skillNameVar = instruction.get(Argument.STRING);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(new HeroesSkillCondition(characterManager, skillNameVar),
                loggerFactory.create(HeroesSkillCondition.class), instruction.getPackage()), data);
    }
}
