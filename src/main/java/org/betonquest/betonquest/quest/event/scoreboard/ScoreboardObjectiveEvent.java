package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Adds/removes/multiplies/divides scores on scoreboards.
 */
public class ScoreboardObjectiveEvent implements Event {
    /**
     * The name of the objective.
     */
    private final String objective;

    /**
     * The number to modify the score by.
     */
    private final VariableNumber count;

    /**
     * The modification to apply to the score.
     */
    private final ScoreModification scoreModification;

    /**
     * Creates a new ScoreboardEvent.
     *
     * @param objective         the name of the objective
     * @param count             the number to modify the score by
     * @param scoreModification the modification to apply to the score
     */
    public ScoreboardObjectiveEvent(final String objective, final VariableNumber count, final ScoreModification scoreModification) {
        this.objective = objective;
        this.count = count;
        this.scoreModification = scoreModification;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective obj = board.getObjective(objective);
        if (obj == null) {
            throw new QuestException("Scoreboard objective " + objective + " does not exist!");
        }
        final Score score = obj.getScore(profile.getPlayer());
        score.setScore(scoreModification.modify(score.getScore(), count.getValue(profile).doubleValue()));
    }
}
