package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Adds/removes/multiplies/divides scores on scoreboards.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ScoreboardEvent extends QuestEvent {

    private final VariableNumber count;
    private final boolean multi;
    private final String objective;

    public ScoreboardEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        persistent = true;
        objective = instruction.next();
        String number = instruction.next();
        if (!number.isEmpty() && number.charAt(0) == '*') {
            multi = true;
            number = number.replace("*", "");
        } else {
            multi = false;
        }
        try {
            count = new VariableNumber(instruction.getPackage().getName(), number);
        } catch (InstructionParseException e) {
            throw new InstructionParseException("Could not parse score count", e);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective obj = board.getObjective(objective);
        if (obj == null) {
            throw new QuestRuntimeException("Scoreboard objective " + objective + " does not exist!");
        }
        final Score score = obj.getScore(PlayerConverter.getName(playerID));
        if (multi) {
            score.setScore((int) Math.floor(score.getScore() * count.getDouble(playerID)));
        } else {
            score.setScore((int) Math.floor(score.getScore() + count.getDouble(playerID)));
        }
        return null;
    }

}
