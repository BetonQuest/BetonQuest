package org.betonquest.betonquest.compatibility.npcs.abstractnpc.variable.npc;

import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.variables.LocationVariable;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * The type of information to retrieve for the NPC: name, full_name, or location.
 */
public enum Argument {
    /**
     * Retrieve the name of the NPC.
     */
    NAME((npc, loc) -> npc.getName()),

    /**
     * Retrieve the full name of the NPC.
     */
    FULL_NAME((npc, loc) -> npc.getFormattedName()),

    /**
     * Retrieve the location of the NPC.
     */
    LOCATION((npc, loc) -> loc.getForLocation(npc.getLocation()));

    /**
     * Function to resolve this argument from an NPC instance and optional {@link LocationVariable}.
     */
    private final BiFunction<BQNPCAdapter<?>, LocationVariable, String> resolveFunction;

    Argument(final BiFunction<BQNPCAdapter<?>, LocationVariable, String> resolve) {
        this.resolveFunction = resolve;
    }

    /**
     * Resolve this argument from the given NPC. The location variable is optional.
     *
     * @param npc      NPC to resolve from
     * @param location location variable to use for resolving
     * @return the value that the variable resolved to
     */
    public String resolve(final BQNPCAdapter<?> npc, @Nullable final LocationVariable location) {
        return resolveFunction.apply(npc, location);
    }
}
