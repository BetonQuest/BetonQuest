package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.id.ConditionID;

/**
 * Get the "fulfillment" status of a quest condition.
 */
public class ConditionVariable implements PlayerVariable {
    /**
     * Condition to check.
     */
    private final ConditionID conditionId;

    /**
     * If variable should be in PAPI style.
     */
    private final boolean papiMode;

    /**
     * Create a new Condition variable.
     *
     * @param conditionId the condition to get the "fulfillment" status
     * @param papiMode    if the return value should be in PAPI mode as defined in the documentation
     */
    public ConditionVariable(final ConditionID conditionId, final boolean papiMode) {
        this.conditionId = conditionId;
        this.papiMode = papiMode;
    }

    @Override
    public String getValue(final Profile profile) {
        final String lang = BetonQuest.getInstance().getPlayerData(profile).getLanguage();

        if (BetonQuest.condition(profile, conditionId)) {
            return papiMode ? Config.getMessage(lang, "condition_variable_met") : "true";
        }
        return papiMode ? Config.getMessage(lang, "condition_variable_not_met") : "false";
    }
}
