package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.quest.event.point.PointType;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Adds/removes/multiplies/divides scores on scoreboards.
 */
public class ScoreboardObjectiveEvent implements PlayerAction {

    /**
     * The name of the objective.
     */
    private final Argument<String> objective;

    /**
     * The number to modify the score by.
     */
    private final Argument<Number> count;

    /**
     * The modification to apply to the score.
     */
    private final PointType modification;

    /**
     * Creates a new ScoreboardEvent.
     *
     * @param objective    the name of the objective
     * @param count        the number to modify the score by
     * @param modification the modification to apply to the score
     */
    public ScoreboardObjectiveEvent(final Argument<String> objective, final Argument<Number> count, final PointType modification) {
        this.objective = objective;
        this.count = count;
        this.modification = modification;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final String objective = this.objective.getValue(profile);
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective obj = board.getObjective(objective);
        if (obj == null) {
            throw new QuestException("Scoreboard objective " + objective + " does not exist!");
        }
        final Score score = obj.getScore(profile.getPlayer());
        score.setScore(modification.modify(score.getScore(), count.getValue(profile).doubleValue()));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
