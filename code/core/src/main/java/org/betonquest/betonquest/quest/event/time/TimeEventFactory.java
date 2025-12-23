package org.betonquest.betonquest.quest.event.time;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.quest.event.DoNothingPlayerlessEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Factory to create time events from {@link Instruction}s.
 */
public class TimeEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Server to use for fetching worlds.
     */
    private final Server server;

    /**
     * Creates the time event factory.
     *
     * @param server the server to use
     */
    public TimeEventFactory(final Server server) {
        this.server = server;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createTimeEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        if (instruction.copy().string().get("world").isEmpty()) {
            return new DoNothingPlayerlessEvent();
        } else {
            return createTimeEvent(instruction);
        }
    }

    private NullableEventAdapter createTimeEvent(final Instruction instruction) throws QuestException {
        final Variable<TimeChange> timeVariable = instruction.parse(TimeParser.TIME).get();
        final Optional<Variable<String>> worldVar = instruction.string().get("world");
        final Selector<World> worldSelector = parseWorld(worldVar.isEmpty() ? null : worldVar.get().getValue(null));
        final boolean hourFormat = !instruction.hasArgument("ticks");
        return new NullableEventAdapter(new TimeEvent(timeVariable, worldSelector, hourFormat));
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
