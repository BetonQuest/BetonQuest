package org.betonquest.betonquest.quest.event.time;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.quest.event.DoNothingPlayerlessAction;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Factory to create time events from {@link Instruction}s.
 */
public class TimeActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Server to use for fetching worlds.
     */
    private final Server server;

    /**
     * Creates the time event factory.
     *
     * @param server the server to use
     */
    public TimeActionFactory(final Server server) {
        this.server = server;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createTimeEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        if (instruction.copy().string().get("world").isEmpty()) {
            return new DoNothingPlayerlessAction();
        }
        return createTimeEvent(instruction);
    }

    private NullableActionAdapter createTimeEvent(final Instruction instruction) throws QuestException {
        final Argument<TimeChange> time = instruction.parse(TimeParser.TIME).get();
        final Optional<Argument<String>> world = instruction.string().get("world");
        final Selector<World> worldSelector = parseWorld(world.isEmpty() ? null : world.get().getValue(null));
        final FlagArgument<Boolean> tickFormat = instruction.bool().getFlag("ticks", true);
        return new NullableActionAdapter(new TimeAction(time, worldSelector, tickFormat));
    }

    private Selector<World> parseWorld(@Nullable final String worldName) {
        if (worldName == null) {
            return Selectors.fromPlayer(Player::getWorld);
        }
        final World world = server.getWorld(worldName);
        return new ConstantSelector<>(world);
    }
}
