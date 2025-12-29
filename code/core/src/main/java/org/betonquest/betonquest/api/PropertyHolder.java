package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profile.Profile;

/**
 * A class implementing this interface exposes properties in the form of key-value pairs resolvable per {@link Profile}.
 */
@FunctionalInterface
public interface PropertyHolder {

    /**
     * This method should return various properties of the objective formatted as readable Strings.
     * An example would be <code>5h 5min</code> for <code>time_left</code>
     * keyword in {@link org.betonquest.betonquest.quest.objective.delay.DelayObjective}
     * or <code>12</code> for keyword <code>mobs_killed</code> in
     * {@link org.betonquest.betonquest.quest.objective.kill.MobKillObjective}.
     * By default, it should return an empty string.
     *
     * @param name    the name of the property you need to return; you can parse it
     *                to extract additional information
     * @param profile the {@link Profile} for which the property is to be returned
     * @return the property for the given name or an empty string if the property is unrecognized
     * @throws QuestException when the property cannot be resolved
     */
    String getProperty(String name, Profile profile) throws QuestException;
}
