package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import me.pikamug.quests.quests.Quest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Starts a quests in Quests plugin.
 */
public class QuestsEvent implements PlayerEvent {

    /**
     * Quests instance.
     */
    private final Quests quests;

    /**
     * Name of quest to start.
     */
    private final Variable<String> questName;

    /**
     * If the quest start should be forced.
     */
    private final boolean override;

    /**
     * Create a new start quest event.
     *
     * @param quests    active quests instance
     * @param questName name of quest to start
     * @param override  whether to force quest start
     */
    public QuestsEvent(final Quests quests, final Variable<String> questName, final boolean override) {
        this.quests = quests;
        this.questName = questName;
        this.override = override;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final String questName = this.questName.getValue(profile);
        Quest quest = null;
        for (final Quest q : quests.getLoadedQuests()) {
            if (q.getName().replace(' ', '_').equalsIgnoreCase(questName)) {
                quest = q;
                break;
            }
        }
        if (quest == null) {
            throw new QuestException("Quest '" + questName + "' is not defined");
        }
        quests.getQuester(profile.getProfileUUID()).takeQuest(quest, override);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
