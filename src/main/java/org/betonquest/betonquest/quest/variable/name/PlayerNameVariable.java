package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;

/**
 * This variable resolves into the player's name. It can have optional "display"
 * argument, which will resolve it to the display name.
 */
public class PlayerNameVariable implements PlayerVariable {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * QuestPackage to use in logging.
     */
    private final QuestPackage questPackage;

    /**
     * The type of the variable.
     */
    private final PlayerNameType type;

    /**
     * Creates a new PlayerNameVariable from the given instruction.
     *
     * @param type         the type to extract the variable value from the profile
     * @param log          the logger to use when there was an error during variable resolving
     * @param questPackage the quest package used for the error logging
     */
    public PlayerNameVariable(final PlayerNameType type, final BetonQuestLogger log, final QuestPackage questPackage) {
        this.type = type;
        this.log = log;
        this.questPackage = questPackage;
    }

    @Override
    public String getValue(final Profile profile) {
        try {
            return type.extractValue(profile);
        } catch (final IllegalStateException e) {
            log.warn(questPackage, e.getMessage(), e);
            return "";
        }
    }
}
