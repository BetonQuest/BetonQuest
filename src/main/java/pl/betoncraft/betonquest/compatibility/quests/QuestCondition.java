package pl.betoncraft.betonquest.compatibility.quests;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has done specified quest before.
 */
public class QuestCondition extends Condition {

    private final String questName;

    public QuestCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questName = instruction.next();
    }

    @Override
    protected Boolean execute(final String playerID) {
        for (final String q : QuestsIntegrator.getQuestsInstance().getQuester(PlayerConverter.getPlayer(playerID).getUniqueId()).getCompletedQuests()) {
            if (q.replace(' ', '_').equalsIgnoreCase(questName)) {
                return true;
            }
        }
        return false;
    }

}
