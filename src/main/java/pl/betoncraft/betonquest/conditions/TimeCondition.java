package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the time is right
 */
public class TimeCondition extends Condition {

    private final double timeMin;
    private final double timeMax;

    public TimeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String[] theTime = instruction.next().split("-");
        if (theTime.length != 2) {
            throw new InstructionParseException("Wrong time format");
        }
        try {
            timeMin = Double.parseDouble(theTime[0]);
            timeMax = Double.parseDouble(theTime[1]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse time", e);
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        double time = PlayerConverter.getPlayer(playerID).getWorld().getTime();
        if (time >= 18_000) {
            // 18000 minecraft-time is midnight, so there is new
            // normal-time cycle after that; subtracting 18 hours
            // from it makes sure that hour is correct in normal-time
            time = time / 1000 - 18;
        } else {
            // if it's less than 18000, then normal-time is in current
            // minecraft-time cycle, but 6 hours behind, so add 6 hours
            time = time / 1000 + 6;
        }
        return time >= timeMin && time <= timeMax;
    }

}
