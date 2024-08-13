package org.betonquest.betonquest.compatibility.npcs.abstractnpc.variable.npc;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.variables.LocationVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Provides information about a npc.
 */
public class NPCVariable implements PlayerlessVariable {
    /**
     * The custom {@link BetonQuestLogger} for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The Supplier for the NPC.
     */
    private final NPCAdapterSupplier npcSupplier;

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
     * @param log         the custom logger to use when the variable cannot be resolved
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public NPCVariable(final NPCAdapterSupplier npcSupplier, final Argument key, @Nullable final LocationVariable location,
                       final BetonQuestLogger log) {
        this.npcSupplier = npcSupplier;
        this.key = key;
        this.location = location;
        if (key == Argument.LOCATION && location == null) {
            throw new IllegalArgumentException("The location argument requires a location variable!");
        }
        this.log = log;
    }

    @Override
    public String getValue() {
        try {
            final BQNPCAdapter<?> npc = npcSupplier.get();
            return key.resolve(npc, location);
        } catch (final QuestRuntimeException exception) {
            log.warn("Can't get NPC for variable!", exception);
            return "";
        }
    }
}
