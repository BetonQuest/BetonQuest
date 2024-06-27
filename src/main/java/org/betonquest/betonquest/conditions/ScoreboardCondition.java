package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Checks the amount of points on scoreboards.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ScoreboardCondition extends Condition {
    private final String objective;

    private final VariableNumber count;

    public ScoreboardCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        objective = instruction.next();
        count = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective obj = board.getObjective(objective);
        if (obj == null) {
            throw new QuestRuntimeException("Scoreboard objective " + objective + " does not exist!");
        }
        final Score score = obj.getScore(profile.getPlayer());
        return score.getScore() >= count.getInt(profile);
    }
}
