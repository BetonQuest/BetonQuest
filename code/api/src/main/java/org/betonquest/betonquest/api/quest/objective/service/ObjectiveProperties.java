package org.betonquest.betonquest.api.quest.objective.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * Any objective may have properties in the form of key-value pairs resolvable per {@link Profile}.
 * Those properties are accessible via this interface.
 */
public interface ObjectiveProperties {

    /**
     * This method should return various properties of the objective formatted as readable strings.
     * By default, it should return an empty string.
     * To define the properties, use {@link #setProperty(String, QuestFunction)}.
     *
     * @param name    the name of the property to retrieve
     * @param profile the {@link Profile} for which the property is resolved
     * @return the property for the given name or an empty string if the property is unrecognized
     * @throws QuestException if the property cannot be resolved
     */
    String getProperty(String name, Profile profile) throws QuestException;

    /**
     * This method should set a property for the objective by defining a {@link QuestFunction} for retrieval.
     * The function should return a string representing the property value and will be called if the property
     * is requested via {@link #getProperty(String, Profile)}.
     *
     * @param name     the name of the property
     * @param property the function to retrieve the property
     */
    void setProperty(String name, QuestFunction<Profile, String> property);

    /**
     * This method should add all properties from the given {@link ObjectiveProperties} to this one.
     * Those new properties will be available for retrieval via {@link #getProperty(String, Profile)}
     * and will be overwritten by {@link #setProperty(String, QuestFunction)}.
     * There can only be a single parent objective properties instance.
     *
     * @param properties the properties to add
     */
    void setParentProperties(ObjectiveProperties properties);
}
