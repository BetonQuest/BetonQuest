package org.betonquest.betonquest.quest.event.time;

import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.DoNothingPlayerlessEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create time events from {@link Instruction}s.
 */
public class TimeEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createTimeEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        if (instruction.copy().getOptional("world") == null) {
            return new DoNothingPlayerlessEvent();
        } else {
            return new PrimaryServerThreadPlayerlessEvent(createTimeEvent(instruction), data);
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
