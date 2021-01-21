package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.World;

/**
 * Changes the weather on the server
 */
@SuppressWarnings("PMD.CommentRequired")
public class WeatherEvent extends QuestEvent {

    private final boolean storm;
    private final boolean thunder;

    public WeatherEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String part = instruction.next();
        switch (part) {
            case "sun":
            case "clear":
                storm = false;
                thunder = false;
                break;
            case "rain":
            case "rainy":
                storm = true;
                thunder = false;
                break;
            case "storm":
            case "thunder":
                storm = false;
                thunder = true;
                break;
            default:
                throw new InstructionParseException("Weather type '" + part + "' does not exist");
        }
    }

    @Override
    protected Void execute(final String playerID) {
        final World world = PlayerConverter.getPlayer(playerID).getWorld();
        world.setStorm(storm);
        world.setThundering(thunder);
        return null;
    }

}
