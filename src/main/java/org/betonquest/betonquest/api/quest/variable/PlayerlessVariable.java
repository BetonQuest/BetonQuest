package org.betonquest.betonquest.api.quest.variable;

/**
 * Interface for "static" quest-variables.
 * It represents the "static" variable as described in the BetonQuest user documentation.
 * For the normal variable variant see {@link PlayerVariable}.
 */
public interface PlayerlessVariable {
    /**
     * Gets the resolved value.
     *
     * @return the value of this variable
     */
    String getValue();
}
