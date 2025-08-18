package org.betonquest.betonquest.api.config.quest;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Interface for managing quest packages.
 */
@FunctionalInterface
public interface QuestPackageManager {
    /**
     * Get all QuestPackages.
     *
     * @return a map of all QuestPackages, with the name as key
     */
    Map<String, QuestPackage> getPackages();

    /**
     * Get a QuestPackage by its name.
     *
     * @param name the name of the QuestPackage to retrieve
     * @return the QuestPackage with the specified name, or null if no such package exists
     */
    @Nullable
    default QuestPackage getPackage(final String name) {
        return this.getPackages().get(name);
    }
}
