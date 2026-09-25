package org.betonquest.betonquest.quest.action.weather;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArguments;
import org.betonquest.betonquest.quest.action.ThrowExceptionPlayerlessAction;
import org.bukkit.World;

/**
 * Factory to create weather actions from {@link Instruction}s.
 */
public class WeatherActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Creates the weather action factory.
     */
    public WeatherActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final PlayerAction weatherPlayerAction = parseWeatherAction(instruction);
        final PlayerAction playerAction;
        if (requiresPlayer(instruction)) {
            playerAction = new OnlineActionAdapter(weatherPlayerAction::execute);
        } else {
            playerAction = weatherPlayerAction;
        }
        return playerAction;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        if (requiresPlayer(instruction)) {
            return new ThrowExceptionPlayerlessAction("Action requires a 'world' argument.");
        }
        return parseWeatherAction(instruction);
    }

    private boolean requiresPlayer(final Instruction instruction) throws QuestException {
        return instruction.copy().string().get("world").isEmpty();
    }

    private NullableActionAdapter parseWeatherAction(final Instruction instruction) throws QuestException {
        final Argument<Weather> weather = instruction.parse(Weather::parseWeather).get();
        final Argument<World> world = instruction.world().get("world").orElse(DefaultArguments.PLAYER_WORLD);
        final Argument<Number> duration = instruction.number().get("duration", 0);
        return new NullableActionAdapter(new WeatherAction(weather, world, duration));
    }
}
