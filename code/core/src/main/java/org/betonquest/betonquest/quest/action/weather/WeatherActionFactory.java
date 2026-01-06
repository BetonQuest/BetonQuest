package org.betonquest.betonquest.quest.action.weather;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.quest.action.DoNothingPlayerlessAction;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create weather actions from {@link Instruction}s.
 */
public class WeatherActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The server to access worlds.
     */
    private final Server server;

    /**
     * Creates the weather action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param server        the server to access worlds
     */
    public WeatherActionFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        this.loggerFactory = loggerFactory;
        this.server = server;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final PlayerAction weatherPlayerAction = parseWeatherAction(instruction);
        final PlayerAction playerAction;
        if (requiresPlayer(instruction)) {
            playerAction = new OnlineActionAdapter(
                    weatherPlayerAction::execute,
                    loggerFactory.create(WeatherAction.class),
                    instruction.getPackage()
            );
        } else {
            playerAction = weatherPlayerAction;
        }
        return playerAction;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        if (requiresPlayer(instruction)) {
            return new DoNothingPlayerlessAction();
        }
        return parseWeatherAction(instruction);
    }

    private boolean requiresPlayer(final Instruction instruction) throws QuestException {
        return instruction.copy().string().get("world").isEmpty();
    }

    private NullableActionAdapter parseWeatherAction(final Instruction instruction) throws QuestException {
        final Argument<Weather> weather = instruction.parse(Weather::parseWeather).get();
        final Argument<String> world = instruction.string().get("world").orElse(null);
        final Selector<World> worldSelector = parseWorld(world == null ? null : world.getValue(null));
        final Argument<Number> duration = instruction.number().get("duration", 0);
        return new NullableActionAdapter(new WeatherAction(weather, worldSelector, duration));
    }

    private Selector<World> parseWorld(@Nullable final String worldName) {
        if (worldName == null) {
            return Selectors.fromPlayer(Player::getWorld);
        }
        final World world = server.getWorld(worldName);
        return new ConstantSelector<>(world);
    }
}
