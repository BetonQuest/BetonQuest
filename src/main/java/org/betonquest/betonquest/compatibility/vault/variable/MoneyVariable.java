package org.betonquest.betonquest.compatibility.vault.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves to amount of money.
 */
public class MoneyVariable implements PlayerVariable {
    /**
     * Function to get the displayed money amount from a profile.
     */
    private final MoneyVariableFactory.QREFunction<Profile, String> function;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Pack used for logging identification.
     */
    private final QuestPackage pack;

    /**
     * @param function the function to get the displayed money amount from a profile
     * @param log      the custom {@link BetonQuestLogger} instance for exception logging
     * @param pack     the pack used for logging identification
     */
    public MoneyVariable(final MoneyVariableFactory.QREFunction<Profile, String> function, final BetonQuestLogger log, final QuestPackage pack) {
        this.function = function;
        this.log = log;
        this.pack = pack;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        if (profile == null) {
            return "";
        }
        try {
            return function.apply(profile);
        } catch (final QuestRuntimeException e) {
            log.warn(pack, "Unable to get money variable value: " + e.getMessage(), e);
            return "";
        }
    }
}
