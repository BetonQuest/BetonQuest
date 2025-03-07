package org.betonquest.betonquest.quest.variable.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;
import org.jetbrains.annotations.Nullable;

import static org.betonquest.betonquest.quest.variable.npc.Argument.LOCATION;

/**
 * Provides information about a npc.
 */
public class NpcVariable implements PlayerlessVariable {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Id of the npc.
     */
    private final NpcID npcID;

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
     * Construct a new NPCVariable that allows for resolution of information about a NPC.
     *
     * @param questTypeAPI  the Quest Type API
     * @param npcID         the npc id
     * @param key           the argument defining the value
     * @param formationMode the location formation mode to use for location resolution
     * @param decimalPlaces the number of decimal places to use for location resolution
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public NpcVariable(final QuestTypeAPI questTypeAPI, final NpcID npcID, final Argument key,
                       @Nullable final LocationFormationMode formationMode, final int decimalPlaces) {
        this.questTypeAPI = questTypeAPI;
        this.npcID = npcID;
        this.key = key;
        this.formationMode = formationMode;
        if (key == LOCATION && formationMode == null) {
            throw new IllegalArgumentException("The location argument requires a location variable!");
        }
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public String getValue() throws QuestException {
        final Npc<?> npc = questTypeAPI.getNpc(npcID);
        return key.resolve(npc, formationMode, decimalPlaces);
    }
}
