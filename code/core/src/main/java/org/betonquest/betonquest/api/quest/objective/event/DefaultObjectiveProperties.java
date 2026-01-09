package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;

import java.util.HashMap;
import java.util.Map;

/**
 * A default implementation of the {@link ObjectiveProperties} interface.
 */
public class DefaultObjectiveProperties implements ObjectiveProperties {

    /**
     * The properties of the objective.
     */
    private final Map<String, QuestFunction<Profile, String>> properties;

    /**
     * The logger to use.
     */
    private final BetonQuestLogger logger;

    /**
     * Creates a new instance of the objective properties.
     *
     * @param logger the logger to use
     */
    public DefaultObjectiveProperties(final BetonQuestLogger logger) {
        this.properties = new HashMap<>();
        this.logger = logger;
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        return properties.getOrDefault(name, p -> "").apply(profile);
    }

    @Override
    public void setProperty(final String name, final QuestFunction<Profile, String> property) {
        if (this.properties.containsKey(name)) {
            logger.warn("Overriding property '%s'. Probably unwanted behavior.".formatted(name));
        }
        logger.debug("Setting property '%s' to '%s'.".formatted(name, property));
        this.properties.put(name, property);
    }
}
