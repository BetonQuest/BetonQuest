package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.compatibility.heroes.HeroesClassType;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link HeroesClassCondition}s from {@link Instruction}s.
 */
public class HeroesClassConditionFactory implements PlayerConditionFactory {
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
     * The {@link HeroClassManager} of the Heroes plugin.
     */
    private final HeroClassManager classManager;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory    the logger factory.
     * @param data             the data used for primary server access.
     * @param characterManager the {@link CharacterManager} of the Heroes plugin.
     * @param classManager     the {@link HeroClassManager} of the Heroes plugin.
     */
    public HeroesClassConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                                       final CharacterManager characterManager, final HeroClassManager classManager) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.characterManager = characterManager;
        this.classManager = classManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<HeroesClassType> classType = instruction.getVariable(Argument.ENUM(HeroesClassType.class));
        final Variable<String> heroClass = instruction.getVariable(Argument.STRING);
        final Variable<Number> level = instruction.getVariable(instruction.getOptional("level"), Argument.NUMBER);

        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new HeroesClassCondition(characterManager, classManager, classType, heroClass, level),
                loggerFactory.create(HeroesClassCondition.class), instruction.getPackage()), data);
    }
}
