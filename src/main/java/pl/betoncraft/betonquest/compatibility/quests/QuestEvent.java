package pl.betoncraft.betonquest.compatibility.quests;

import me.blackvein.quests.Quest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Starts a quests in Quests plugin.
 */
@SuppressWarnings("PMD.CommentRequired")
public class QuestEvent extends pl.betoncraft.betonquest.api.QuestEvent {

    private final String questName;
    private final boolean override;

    public QuestEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questName = instruction.next();
        override = instruction.hasArgument("check-requirements");
    }

    @Override
    protected Void execute(final String playerID) {
        Quest quest = null;
        for (final Quest q : QuestsIntegrator.getQuestsInstance().getQuests()) {
            if (q.getName().replace(' ', '_').equalsIgnoreCase(questName)) {
                quest = q;
                break;
            }
        }
        if (quest == null) {
            LogUtils.getLogger().log(Level.WARNING, "Quest '" + questName + "' is not defined");
            return null;
        }
        QuestsIntegrator.getQuestsInstance().getQuester(PlayerConverter.getPlayer(playerID).getUniqueId()).takeQuest(quest, override);
        return null;
    }

}
