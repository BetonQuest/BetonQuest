package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
        if (number.startsWith("*")) {
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
