package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.quests.Quest;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Starts a quests in Quests plugin.
 */
@SuppressWarnings("PMD.CommentRequired")
public class QuestsEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final String questName;

    private final boolean override;

    public QuestsEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        questName = instruction.next();
        override = instruction.hasArgument("check-requirements");
    }

    @Override
    protected Void execute(final Profile profile) {
        Quest quest = null;
        for (final Quest q : QuestsIntegrator.getQuestsInstance().getLoadedQuests()) {
            if (q.getName().replace(' ', '_').equalsIgnoreCase(questName)) {
                quest = q;
                break;
            }
        }
        if (quest == null) {
            log.warn(instruction.getPackage(), "Quest '" + questName + "' is not defined");
            return null;
        }
        QuestsIntegrator.getQuestsInstance().getQuester(profile.getProfileUUID()).takeQuest(quest, override);
        return null;
    }
}
