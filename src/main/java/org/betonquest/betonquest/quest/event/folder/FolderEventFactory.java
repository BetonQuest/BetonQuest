package org.betonquest.betonquest.quest.event.folder;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.plugin.PluginManager;

/**
 * Factory to create {@link FolderEvent} instances.
 */
public class FolderEventFactory implements EventFactory, StaticEventFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The plugin manager to register the quit listener.
     */
    private final PluginManager pluginManager;

    /**
     * Create a new folder event factory.
     *
     * @param betonQuest    the BetonQuest instance
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginManager the plugin manager to register the quit listener
     */
    public FolderEventFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory,
                              final PluginManager pluginManager) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
        this.pluginManager = pluginManager;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return createFolderEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return createFolderEvent(instruction);
    }

    private NullableEventAdapter createFolderEvent(final Instruction instruction) throws QuestException {
        final EventID[] events = instruction.getIDArray(EventID::new);
        final VariableNumber delay = instruction.get(instruction.getOptional("delay"), VariableNumber::new);
        final VariableNumber period = instruction.get(instruction.getOptional("period"), VariableNumber::new);
        final VariableNumber random = instruction.get(instruction.getOptional("random"), VariableNumber::new);
        final TimeUnit timeUnit = getTimeUnit(instruction);
        final boolean cancelOnLogout = instruction.hasArgument("cancelOnLogout");
        final ConditionID[] cancelConditions = instruction.getIDArray(instruction.getOptional("cancelConditions"), ConditionID::new);
        return new NullableEventAdapter(new FolderEvent(betonQuest, loggerFactory.create(FolderEvent.class), pluginManager,
                events, delay, period, random, timeUnit, cancelOnLogout, cancelConditions));
    }

    private TimeUnit getTimeUnit(final Instruction instruction) {
        if (instruction.hasArgument("ticks")) {
            return TimeUnit.TICKS;
        } else if (instruction.hasArgument("minutes")) {
            return TimeUnit.MINUTES;
        } else {
            return TimeUnit.SECONDS;
        }
    }
}
