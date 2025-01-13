package org.betonquest.betonquest.compatibility.citizens.variable.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.variables.LocationVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Provides information about a npc.
 */
public class CitizensVariable implements PlayerlessVariable {
    /**
     * The integer ID of the NPC.
     */
    private final int npcId;

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
     * Construct a new Citizens NPC Variable that allows for resolution of information about a NPC.
     *
     * @param npcId    the id of the npc
     * @param key      the argument defining the value
     * @param location the location to provide when
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public CitizensVariable(final int npcId, final Argument key, @Nullable final LocationVariable location) {
        this.npcId = npcId;
        this.key = key;
        this.location = location;
        if (key == Argument.LOCATION && location == null) {
            throw new IllegalArgumentException("The location argument requires a location variable!");
        }
    }

    @Override
    public String getValue() throws QuestException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestException("No NPC with id '" + npcId + "' found!");
        }

        return key.resolve(npc, location);
    }
}
