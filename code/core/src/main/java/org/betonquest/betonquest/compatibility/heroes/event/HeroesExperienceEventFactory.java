package org.betonquest.betonquest.compatibility.heroes.event;

import com.herocraftonline.heroes.characters.CharacterManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.compatibility.heroes.HeroesClassType;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link HeroesExperienceEvent}s from {@link Instruction}s.
 */
public class HeroesExperienceEventFactory implements PlayerEventFactory {
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
    public HeroesExperienceEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                                        final CharacterManager characterManager) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.characterManager = characterManager;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<HeroesClassType> classType = instruction.get(Argument.ENUM(HeroesClassType.class));
        final Variable<Number> amountVar = instruction.get(Argument.NUMBER);

        return new PrimaryServerThreadEvent(new OnlineEventAdapter(new HeroesExperienceEvent(characterManager, classType, amountVar),
                loggerFactory.create(HeroesExperienceEvent.class), instruction.getPackage()), data);
    }
}
