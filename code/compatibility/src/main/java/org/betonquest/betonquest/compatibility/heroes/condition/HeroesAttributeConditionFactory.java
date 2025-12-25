package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory to create {@link HeroesAttributeCondition}s from {@link Instruction}s.
 */
public class HeroesAttributeConditionFactory implements PlayerConditionFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory    the logger factory.
     * @param characterManager the {@link CharacterManager} of the Heroes plugin.
     */
    public HeroesAttributeConditionFactory(final BetonQuestLoggerFactory loggerFactory, final CharacterManager characterManager) {
        this.loggerFactory = loggerFactory;
        this.characterManager = characterManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> attributeVar = instruction.string().get();
        final Argument<Number> levelVar = instruction.number().get();
        return new OnlineConditionAdapter(new HeroesAttributeCondition(characterManager, attributeVar, levelVar),
                loggerFactory.create(HeroesAttributeCondition.class), instruction.getPackage());
    }
}
