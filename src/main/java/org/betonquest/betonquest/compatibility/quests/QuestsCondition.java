package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import me.pikamug.quests.quests.Quest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.VariableString;

import java.util.Set;

/**
 * Checks if the player has done specified quest before.
 */
public class QuestsCondition implements PlayerCondition {

    /**
     * Quests instance.
     */
    private final Quests quests;

    /**
     * Quest name to check.
     */
    private final VariableString questName;

    /**
     * Create a new quest completed condition.
     *
     * @param quests    active quests instance
     * @param questName name of quest which has to be completed
     */
    public QuestsCondition(final Quests quests, final VariableString questName) {
        this.quests = quests;
        this.questName = questName;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String questName = this.questName.getValue(profile);
        final Set<Quest> completedQuests = quests.getQuester(profile.getProfileUUID()).getCompletedQuests();
        for (final Quest q : completedQuests) {
            if (q.getName().replace(' ', '_').equalsIgnoreCase(questName)) {
                return true;
            }
        }
        return false;
    }
}
