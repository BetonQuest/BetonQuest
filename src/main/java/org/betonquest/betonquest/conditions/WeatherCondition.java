package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.World;

import java.util.Locale;

/**
 * Requires the weather to be of specific type
 */
@SuppressWarnings("PMD.CommentRequired")
public class WeatherCondition extends Condition {

    private final String weather;

    public WeatherCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        weather = instruction.next().toLowerCase(Locale.ROOT).trim();
        if (!"sun".equals(weather) && !"clear".equals(weather) && !"rain".equals(weather) && !"rainy".equals(weather)
                && !"storm".equals(weather) && !"thunder".equals(weather)) {
            throw new InstructionParseException("Weather type '" + weather + "' does not exist");
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        if (profile.getPlayer().isEmpty()) {
            throw new QuestRuntimeException("Player is offline");
        }
        final World world = profile.getPlayer().get().getWorld();
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
