package org.betonquest.betonquest.quest.variable.npc;

import org.apache.commons.lang3.function.TriFunction;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;
import org.jetbrains.annotations.Nullable;

/**
 * The type of information to retrieve for the NPC: name, full_name, or location.
 */
public enum Argument {
    /**
     * Retrieve the name of the NPC.
     */
    NAME((npc, loc, dec) -> npc.getName()),

    /**
     * Retrieve the full name of the NPC.
     */
    FULL_NAME((npc, loc, dec) -> npc.getFormattedName()),

    /**
     * Retrieve the location of the NPC with the given location formation and decimal places.
     */
    LOCATION((npc, loc, dec) -> loc.getFormattedLocation(npc.getLocation(), dec));

    /**
     * Function to resolve this argument from an NPC instance and optional {@link LocationFormationMode} with decimal places.
     */
    private final TriFunction<Npc<?>, LocationFormationMode, Integer, String> resolveFunction;

    Argument(final TriFunction<Npc<?>, LocationFormationMode, Integer, String> resolveFunction) {
        this.resolveFunction = resolveFunction;
    }

    /**
     * Resolve this argument from the given NPC.
     *
     * @param npc           NPC to resolve from
     * @param location      location formation mode to use for location resolution
     * @param decimalPlaces the number of decimal places to use for location resolution
     * @return the value that the variable resolved to
     */
    public String resolve(final Npc<?> npc, @Nullable final LocationFormationMode location, final int decimalPlaces) {
        return resolveFunction.apply(npc, location, decimalPlaces);
    }
}
