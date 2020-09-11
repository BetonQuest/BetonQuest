package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.List;

/**
 * Requires the player to have specified amount of points (or more) in specified
 * category
 */
public class PointCondition extends Condition {

    protected final String category;
    protected final VariableNumber count;
    protected final boolean equal;

    public PointCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        category = Utils.addPackage(instruction.getPackage(), instruction.next());
        count = instruction.getVarNum();
        equal = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return check(playerID, BetonQuest.getInstance().getPlayerData(playerID).getPoints());
    }

    protected boolean check(final String playerID, final List<Point> points) throws QuestRuntimeException {
        final int pCount = count.getInt(playerID);
        for (final Point point : points) {
            if (point.getCategory().equalsIgnoreCase(category)) {
                if (equal) {
                    return point.getCount() == pCount;
                } else {
                    return point.getCount() >= pCount;
                }
            }
        }
        return false;
    }

}
