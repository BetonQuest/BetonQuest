package org.betonquest.betonquest.quest.event.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;
import java.util.Locale;

/**
 * Factory for {@link ObjectiveEvent}s.
 */
public class ObjectiveEventFactory implements EventFactory, StaticEventFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new factory for {@link ObjectiveEvent}s.
     *
     * @param betonQuest    the BetonQuest instance
     * @param loggerFactory the logger factory
     * @param questTypeAPI  the Quest Type API
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public ObjectiveEventFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory,
                                 final QuestTypeAPI questTypeAPI, final PluginMessage pluginMessage) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
        this.questTypeAPI = questTypeAPI;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return createObjectiveEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return createObjectiveEvent(instruction);
    }

    private NullableEventAdapter createObjectiveEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.next().toLowerCase(Locale.ROOT);
        final List<ObjectiveID> objectives = instruction.getIDList(ObjectiveID::new);
        return new NullableEventAdapter(new ObjectiveEvent(betonQuest, loggerFactory.create(ObjectiveEvent.class),
                pluginMessage, questTypeAPI, instruction.getPackage(), objectives, action));
    }
}
