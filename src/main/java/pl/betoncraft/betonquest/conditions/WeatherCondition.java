package pl.betoncraft.betonquest.conditions;

import org.bukkit.World;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the weather to be of specific type
 */
@SuppressWarnings("PMD.CommentRequired")
public class WeatherCondition extends Condition {

    private final String weather;

    public WeatherCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        weather = instruction.next().toLowerCase().trim();
        if (!"sun".equals(weather) && !"clear".equals(weather) && !"rain".equals(weather) && !"rainy".equals(weather)
                && !"storm".equals(weather) && !"thunder".equals(weather)) {
            throw new InstructionParseException("Weather type '" + weather + "' does not exist");
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
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
                return false;
        }
        return false;
    }

}
