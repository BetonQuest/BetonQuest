package pl.betoncraft.betonquest.compatibility.quests;

import me.blackvein.quests.Quest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Checks if the player has done specified quest before.
 */
@SuppressWarnings("PMD.CommentRequired")
public class QuestsCondition extends Condition {

    private final String questName;

    public QuestsCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questName = instruction.next();
    }

    @Override
    protected Boolean execute(final String playerID) {
        final ConcurrentSkipListSet<Quest> completedQuests = QuestsIntegrator.getQuestsInstance().getQuester(PlayerConverter.getPlayer(playerID).getUniqueId()).getCompletedQuests();
        for (final Quest q : completedQuests) {
            if (q.getName().replace(' ', '_').equalsIgnoreCase(questName)) {
                return true;
            }
        }
        return false;
    }

}
