package org.betonquest.betonquest.compatibility.quests;

import me.blackvein.quests.Quest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Starts a quests in Quests plugin.
 */
@SuppressWarnings("PMD.CommentRequired")
public class QuestsEvent extends QuestEvent {

    private final String questName;
    private final boolean override;

    public QuestsEvent(final Instruction instruction) throws InstructionParseException {
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
