package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import me.pikamug.quests.quests.Quest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Starts a quests in Quests plugin.
 */
public class QuestsAction implements PlayerAction {

    /**
     * Quests instance.
     */
    private final Quests quests;

    /**
     * Name of quest to start.
     */
    private final Argument<String> questName;

    /**
     * If the quest start should be forced.
     */
    private final FlagArgument<Boolean> override;

    /**
     * Create a new start quest action.
     *
     * @param quests    active quests instance
     * @param questName name of quest to start
     * @param override  whether to force quest start
     */
    public QuestsAction(final Quests quests, final Argument<String> questName, final FlagArgument<Boolean> override) {
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
        quests.getQuester(profile.getProfileUUID()).takeQuest(quest, override.getValue(profile).orElse(false));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
