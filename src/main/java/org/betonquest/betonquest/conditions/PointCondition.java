package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
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
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        return check(profile, BetonQuest.getInstance().getPlayerData(profile).getPoints());
    }

    protected boolean check(final Profile profile, final List<Point> points) throws QuestRuntimeException {
        final int pCount = count.getInt(profile);
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
