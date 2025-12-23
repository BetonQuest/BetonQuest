package org.betonquest.betonquest.compatibility.heroes.event;

import com.herocraftonline.heroes.characters.CharacterManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.compatibility.heroes.HeroesClassType;

/**
 * Factory to create {@link HeroesExperienceEvent}s from {@link Instruction}s.
 */
public class HeroesExperienceEventFactory implements PlayerEventFactory {

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
    public HeroesExperienceEventFactory(final BetonQuestLoggerFactory loggerFactory, final CharacterManager characterManager) {
        this.loggerFactory = loggerFactory;
        this.characterManager = characterManager;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<HeroesClassType> classType = instruction.enumeration(HeroesClassType.class).get();
        final Variable<Number> amountVar = instruction.number().get();

        return new OnlineEventAdapter(new HeroesExperienceEvent(characterManager, classType, amountVar),
                loggerFactory.create(HeroesExperienceEvent.class), instruction.getPackage());
    }
}
