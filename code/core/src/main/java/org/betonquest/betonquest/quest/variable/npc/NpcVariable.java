package org.betonquest.betonquest.quest.variable.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;
import org.jetbrains.annotations.Nullable;

import static org.betonquest.betonquest.quest.variable.npc.Argument.LOCATION;

/**
 * Provides information about a npc.
 */
public class NpcVariable implements NullableVariable {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Id of the npc.
     */
    private final Argument<NpcID> npcID;

    /**
     * The type of information to retrieve for the NPC: name, full_name, or location.
     */
    private final org.betonquest.betonquest.quest.variable.npc.Argument key;

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
     * @param featureApi    the Feature API
     * @param npcID         the npc id
     * @param key           the argument defining the value
     * @param formationMode the location formation mode to use for location resolution
     * @param decimalPlaces the number of decimal places to use for location resolution
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public NpcVariable(final FeatureApi featureApi, final Argument<NpcID> npcID, final org.betonquest.betonquest.quest.variable.npc.Argument key,
                       @Nullable final LocationFormationMode formationMode, final int decimalPlaces) {
        this.featureApi = featureApi;
        this.npcID = npcID;
        this.key = key;
        this.formationMode = formationMode;
        if (key == LOCATION && formationMode == null) {
            throw new IllegalArgumentException("The location argument requires a location variable!");
        }
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final Npc<?> npc = featureApi.getNpc(npcID.getValue(profile), profile);
        return key.resolve(npc, formationMode, decimalPlaces);
    }
}
