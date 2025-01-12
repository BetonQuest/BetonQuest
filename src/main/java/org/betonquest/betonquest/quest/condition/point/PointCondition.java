package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.List;

/**
 * A condition that checks if a player has a certain amount of points.
 */
public class PointCondition implements PlayerCondition {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * The category of the points.
     */
    private final String category;

    /**
     * The amount of points.
     */
    private final VariableNumber count;

    /**
     * Whether the points should be equal to the specified amount.
     */
    private final boolean equal;

    /**
     * Constructor for the point condition.
     *
     * @param betonQuest the BetonQuest instance
     * @param category   the category of the points
     * @param count      the amount of points
     * @param equal      whether the points should be equal to the specified amount
     */
    public PointCondition(final BetonQuest betonQuest, final String category, final VariableNumber count, final boolean equal) {
        this.betonQuest = betonQuest;
        this.category = category;
        this.count = count;
        this.equal = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final List<Point> points = betonQuest.getPlayerData(profile).getPoints();
        for (final Point point : points) {
            if (point.getCategory().equals(category)) {
                return checkPoints(point.getCount(), profile);
            }
        }
        return false;
    }

    private boolean checkPoints(final int points, final Profile profile) throws QuestException {
        final int pCount = this.count.getValue(profile).intValue();
        return equal ? points == pCount : points >= pCount;
    }
}
