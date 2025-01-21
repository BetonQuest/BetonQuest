package org.betonquest.betonquest.compatibility.citizens.variable.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;
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
     * The location formation mode to use for location resolution.
     */
    @Nullable
    private final LocationFormationMode formationMode;

    /**
     * The number of decimal places to use for location resolution.
     */
    private final int decimalPlaces;

    /**
     * Construct a new Citizens NPC Variable that allows for resolution of information about a NPC.
     *
     * @param npcId         the id of the npc
     * @param key           the argument defining the value
     * @param formationMode the location formation mode to use for location resolution
     * @param decimalPlaces the number of decimal places to use for location resolution
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public CitizensVariable(final int npcId, final Argument key, @Nullable final LocationFormationMode formationMode, final int decimalPlaces) {
        this.npcId = npcId;
        this.key = key;
        this.formationMode = formationMode;
        if (key == Argument.LOCATION && formationMode == null) {
            throw new IllegalArgumentException("The location argument requires a location variable!");
        }
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public String getValue() throws QuestException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestException("No NPC with id '" + npcId + "' found!");
        }

        return key.resolve(npc, formationMode, decimalPlaces);
    }
}
