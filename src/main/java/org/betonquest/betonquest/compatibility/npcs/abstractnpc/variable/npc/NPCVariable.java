package org.betonquest.betonquest.compatibility.npcs.abstractnpc.variable.npc;

import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.variables.LocationVariable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Provides information about a npc.
 */
public class NPCVariable implements PlayerlessVariable {
    /**
     * The Supplier for the NPC.
     */
    private final Supplier<BQNPCAdapter> npcSupplier;

    /**
     * The type of information to retrieve for the NPC: name, full_name, or location.
     */
    private final Argument key;

    /**
     * A wrapper for the location property of the NPC.
     */
    @Nullable
    private final LocationVariable location;

    /**
     * Construct a new NPCVariable that allows for resolution of information about a NPC.
     *
     * @param npcSupplier the supplier for the npc
     * @param key         the argument defining the value
     * @param location    the location to provide when
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public NPCVariable(final Supplier<BQNPCAdapter> npcSupplier, final Argument key, @Nullable final LocationVariable location) {
        this.npcSupplier = npcSupplier;
        this.key = key;
        this.location = location;
        if (key == Argument.LOCATION && location == null) {
            throw new IllegalArgumentException("The location argument requires a location variable!");
        }
    }

    @Override
    public String getValue() {
        final BQNPCAdapter npc = npcSupplier.get();
        if (npc == null) {
            return "";
        }

        return key.resolve(npc, location);
    }
}
