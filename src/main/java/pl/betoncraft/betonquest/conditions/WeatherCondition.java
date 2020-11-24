package pl.betoncraft.betonquest.conditions;

import org.bukkit.World;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the weather to be of specific type
 */
public class WeatherCondition extends Condition {

    private final String weather;

    public WeatherCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        weather = instruction.next().toLowerCase().trim();
        if (!weather.equals("sun") && !weather.equals("clear") && !weather.equals("rain") && !weather.equals("rainy")
                && !weather.equals("storm") && !weather.equals("thunder")) {
            throw new InstructionParseException("Weather type '" + weather + "' does not exist");
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        final World world = PlayerConverter.getPlayer(playerID).getWorld();
        switch (weather) {
            case "sun":
            case "clear":
                if (!world.isThundering() && !world.hasStorm()) {
                    return true;
                }
                break;
            case "rain":
            case "rainy":
                if (world.hasStorm()) {
                    return true;
                }
                break;
            case "storm":
            case "thunder":
                if (world.isThundering()) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

}
