package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestAPI;
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
     * BetonQuest API.
     */
    private final BetonQuestAPI questAPI;

    /**
     * Create a new Condition variable.
     *
     * @param conditionId the condition to get the "fulfillment" status
     * @param papiMode    if the return value should be in PAPI mode as defined in the documentation
     * @param questAPI    the BetonQuest API
     */
    public ConditionVariable(final ConditionID conditionId, final boolean papiMode, final BetonQuestAPI questAPI) {
        this.conditionId = conditionId;
        this.papiMode = papiMode;
        this.questAPI = questAPI;
    }

    @Override
    public String getValue(final Profile profile) {
        final String lang = BetonQuest.getInstance().getPlayerData(profile).getLanguage();

        if (questAPI.condition(profile, conditionId)) {
            return papiMode ? Config.getMessage(lang, "condition_variable_met") : "true";
        }
        return papiMode ? Config.getMessage(lang, "condition_variable_not_met") : "false";
    }
}
