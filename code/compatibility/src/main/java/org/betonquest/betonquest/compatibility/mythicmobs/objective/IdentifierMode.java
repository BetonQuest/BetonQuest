package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import io.lumine.mythic.api.mobs.MythicMob;

import java.util.function.Function;

/**
 * Mode to get an identifier from a {@link MythicMob}.
 */
public enum IdentifierMode {
    /**
     * Use the internal name.
     */
    INTERNAL_NAME(MythicMob::getInternalName),
    /**
     * Use the faction.
     */
    FACTION(MythicMob::getFaction);

    /**
     * Function to get an identifier from mob.
     */
    private final Function<MythicMob, String> identifierFunction;

    IdentifierMode(final Function<MythicMob, String> identifierFunction) {
        this.identifierFunction = identifierFunction;
    }

    /**
     * Gets the identifier from the mob.
     *
     * @param mob the mythic mob to get the identifier for
     * @return the identifier
     */
    public String getIdentifier(final MythicMob mob) {
        return identifierFunction.apply(mob);
    }
}
