package org.betonquest.betonquest.quest.action.weather;

import org.betonquest.betonquest.api.QuestException;
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
import org.bukkit.World;

/**
 * Factory to create weather actions from {@link Instruction}s.
 */
public class WeatherActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates the weather action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public WeatherActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
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
        final String worldPart = instruction.string().get("world", "%location.world%").getValue(null);
        final Argument<World> world = instruction.chainForArgument(worldPart).world().get();
        final Argument<Number> duration = instruction.number().get("duration", 0);
        return new NullableActionAdapter(new WeatherAction(weather, world, duration));
    }
}
