package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Changes the weather on the server
 */
@SuppressWarnings("PMD.CommentRequired")
public class WeatherEvent extends QuestEvent {

    private final boolean storm;
    private final boolean thunder;
    private final String world;
    private final VariableNumber duration;

    @SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault")
    public WeatherEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        final String part = instruction.next();
        switch (part) {
            case "sun", "clear" -> {
                storm = false;
                thunder = false;
            }
            case "rain", "rainy" -> {
                storm = true;
                thunder = false;
            }
            case "storm", "thunder" -> {
                storm = true;
                thunder = true;
            }
            default -> throw new InstructionParseException("Weather type '" + part + "' does not exist");
        }
        world = instruction.getOptional("world");
        duration = instruction.getVarNum(instruction.getOptional("duration"));
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final World world = getWorld(playerID);
        world.setStorm(storm);
        world.setThundering(thunder);
        if (duration != null) {
            world.setWeatherDuration(duration.getInt(playerID) * 20);
        }
        return null;
    }

    private @NotNull World getWorld(final String playerID) throws QuestRuntimeException {
        if (world == null) {
            return PlayerConverter.getPlayer(playerID).getWorld();
        }
        final World resolvedWorld = Bukkit.getWorld(world);
        if (resolvedWorld == null) {
            throw new QuestRuntimeException("The world '" + world + "' does not exist");
        }
        return resolvedWorld;
    }
}
