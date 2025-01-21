package org.betonquest.betonquest.quest.event.time;

import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.DoNothingStaticEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create time events from {@link Instruction}s.
 */
public class TimeEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Server to use for fetching worlds.
     */
    private final Server server;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The variable processor to use for creating the time variable.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Creates the time event factory.
     *
     * @param server            the server to use
     * @param data              the data for primary server thread access
     * @param variableProcessor variable processor to create variables with
     */
    public TimeEventFactory(final Server server, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.server = server;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createTimeEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        if (instruction.copy().getOptional("world") == null) {
            return new DoNothingStaticEvent();
        } else {
            return new PrimaryServerThreadStaticEvent(createTimeEvent(instruction), data);
        }
    }

    private NullableEventAdapter createTimeEvent(final Instruction instruction) throws QuestException {
        final String timeString = instruction.next();
        final Time time = parseTimeType(timeString);
        final VariableNumber rawTime = parseTime(instruction.getPackage(), timeString, time != Time.SET);
        final Selector<World> worldSelector = parseWorld(instruction.getOptional("world"));
        final boolean hourFormat = !instruction.hasArgument("ticks");
        return new NullableEventAdapter(new TimeEvent(time, rawTime, worldSelector, hourFormat));
    }

    private Time parseTimeType(final String timeString) throws QuestException {
        if (timeString.isEmpty()) {
            throw new QuestException("Time cannot be empty");
        }
        return switch (timeString.charAt(0)) {
            case '+' -> Time.ADD;
            case '-' -> Time.SUBTRACT;
            default -> Time.SET;
        };
    }

    private VariableNumber parseTime(final QuestPackage questPackage, final String timeString, final boolean cutFirst) throws QuestException {
        final String rawTime = cutFirst ? timeString.substring(1) : timeString;
        return new VariableNumber(variableProcessor, questPackage, rawTime);
    }

    private Selector<World> parseWorld(@Nullable final String worldName) {
        if (worldName == null) {
            return Selectors.fromPlayer(Player::getWorld);
        } else {
            final World world = server.getWorld(worldName);
            return new ConstantSelector<>(world);
        }
    }
}
