package org.betonquest.betonquest.conditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
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
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective obj = board.getObjective(objective);
        if (obj == null) {
            throw new QuestRuntimeException("Scoreboard objective " + objective + " does not exist!");
        }
        final Score score = obj.getScore(PlayerConverter.getName(playerID));
        return score.getScore() >= count.getInt(playerID);
    }

}
