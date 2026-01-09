package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A default implementation of the {@link ObjectiveProperties} interface.
 */
public class DefaultObjectiveProperties implements ObjectiveProperties {

    /**
     * The logger to use.
     */
    private final BetonQuestLogger logger;

    /**
     * The properties of the objective.
     */
    private final Map<String, QuestFunction<Profile, String>> properties;

    /**
     * Other properties of the objective.
     */
    @Nullable
    private ObjectiveProperties parentProperties;

    /**
     * Creates a new instance of the objective properties.
     *
     * @param logger the logger to use
     */
    public DefaultObjectiveProperties(final BetonQuestLogger logger) {
        this.properties = new HashMap<>();
        this.parentProperties = null;
        this.logger = logger;
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        final QuestFunction<Profile, String> retriever = properties.get(name);
        if (retriever != null) {
            return retriever.apply(profile);
        }
        if (parentProperties != null) {
            return parentProperties.getProperty(name, profile);
        }
        return "";
    }

    @Override
    public void setProperty(final String name, final QuestFunction<Profile, String> property) {
        final String propertyName = name.toLowerCase(Locale.ROOT);
        if (this.properties.containsKey(propertyName)) {
            logger.warn("Overriding property '%s'. Probably unwanted behavior.".formatted(propertyName));
        }
        this.properties.put(propertyName, property);
    }

    @Override
    public void setParentProperties(final ObjectiveProperties properties) {
        this.parentProperties = properties;
    }
}
