package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.HeroesClassType;

/**
 * Factory to create {@link HeroesClassCondition}s from {@link Instruction}s.
 */
public class HeroesClassConditionFactory implements PlayerConditionFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * The {@link HeroClassManager} of the Heroes plugin.
     */
    private final HeroClassManager classManager;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory    the logger factory.
     * @param characterManager the {@link CharacterManager} of the Heroes plugin.
     * @param classManager     the {@link HeroClassManager} of the Heroes plugin.
     */
    public HeroesClassConditionFactory(final BetonQuestLoggerFactory loggerFactory, final CharacterManager characterManager,
                                       final HeroClassManager classManager) {
        this.loggerFactory = loggerFactory;
        this.characterManager = characterManager;
        this.classManager = classManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<HeroesClassType> classType = instruction.enumeration(HeroesClassType.class).get();
        final Argument<String> heroClass = instruction.string().get();
        final Argument<Number> level = instruction.number().get("level").orElse(null);
        return new OnlineConditionAdapter(new HeroesClassCondition(characterManager, classManager, classType, heroClass, level),
                loggerFactory.create(HeroesClassCondition.class), instruction.getPackage());
    }
}
