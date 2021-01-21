package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;

import java.util.List;

/**
 * Requires the player to have specified amount of points (or more) in specified
 * category
 */
@SuppressWarnings("PMD.CommentRequired")
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
