package pl.betoncraft.betonquest.events;

import org.bukkit.World;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
