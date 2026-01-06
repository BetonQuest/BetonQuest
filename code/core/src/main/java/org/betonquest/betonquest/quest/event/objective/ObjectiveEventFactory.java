package org.betonquest.betonquest.quest.event.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.database.PlayerDataFactory;

import java.util.List;
import java.util.Locale;

/**
 * Factory for {@link ObjectiveEvent}s.
 */
public class ObjectiveEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

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
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createObjectiveEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createObjectiveEvent(instruction);
    }

    private NullableActionAdapter createObjectiveEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.string().map(s -> s.toLowerCase(Locale.ROOT)).get().getValue(null);
        final Argument<List<ObjectiveID>> objectives = instruction.parse(ObjectiveID::new).list().get();
        return new NullableActionAdapter(new ObjectiveEvent(betonQuest, loggerFactory.create(ObjectiveEvent.class),
                questTypeApi, instruction.getPackage(), objectives, playerDataFactory, action));
    }
}
