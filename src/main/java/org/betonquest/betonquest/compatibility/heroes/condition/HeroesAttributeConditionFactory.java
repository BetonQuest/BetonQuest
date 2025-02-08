package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link HeroesAttributeCondition}s from {@link Instruction}s.
 */
public class HeroesAttributeConditionFactory implements PlayerConditionFactory {
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
    public HeroesAttributeConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                                           final CharacterManager characterManager) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.characterManager = characterManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableString attributeVar = instruction.get(VariableString::new);
        final VariableNumber levelVar = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(new HeroesAttributeCondition(characterManager, attributeVar, levelVar),
                loggerFactory.create(HeroesAttributeCondition.class), instruction.getPackage()), data);
    }
}
