package org.betonquest.betonquest.quest.event.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.database.PlayerDataFactory;

import java.util.List;
import java.util.Locale;

/**
 * Factory for {@link ObjectiveEvent}s.
 */
public class ObjectiveEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Factory to create new Player Data.
     */
    private final PlayerDataFactory playerDataFactory;

    /**
     * Creates a new factory for {@link ObjectiveEvent}s.
     *
     * @param betonQuest        the BetonQuest instance
     * @param loggerFactory     the logger factory to create a logger for the events
     * @param questTypeApi      the Quest Type API
     * @param playerDataFactory the factory to create player data
     */
    public ObjectiveEventFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory,
                                 final QuestTypeApi questTypeApi, final PlayerDataFactory playerDataFactory) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
        this.questTypeApi = questTypeApi;
        this.playerDataFactory = playerDataFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createObjectiveEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createObjectiveEvent(instruction);
    }

    private NullableEventAdapter createObjectiveEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.get(Argument.STRING).getValue(null).toLowerCase(Locale.ROOT);
        final Variable<List<ObjectiveID>> objectives = instruction.getList(ObjectiveID::new);
        return new NullableEventAdapter(new ObjectiveEvent(betonQuest, loggerFactory.create(ObjectiveEvent.class),
                questTypeApi, instruction.getPackage(), objectives, playerDataFactory, action));
    }
}
