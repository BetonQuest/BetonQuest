package org.betonquest.betonquest.quest.placeholder.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholder;
import org.betonquest.betonquest.quest.placeholder.location.LocationFormationMode;
import org.jetbrains.annotations.Nullable;

import static org.betonquest.betonquest.quest.placeholder.npc.NPCArgument.LOCATION;

/**
 * Provides information about a npc.
 */
public class NpcPlaceholder implements NullablePlaceholder {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Id of the npc.
     */
    private final Argument<NpcIdentifier> npcID;

    /**
     * The key to defining the value.
     */
    private final NPCArgument key;

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
     * Construct a new NPCPlaceholder that allows for resolution of information about a NPC.
     *
     * @param featureApi    the Feature API
     * @param npcID         the npc id
     * @param key           the key to defining the value
     * @param formationMode the location formation mode to use for location resolution
     * @param decimalPlaces the number of decimal places to use for location resolution
     * @throws IllegalArgumentException when a location argument is given without a location placeholder
     */
    public NpcPlaceholder(final FeatureApi featureApi, final Argument<NpcIdentifier> npcID, final NPCArgument key,
                          @Nullable final LocationFormationMode formationMode, final int decimalPlaces) {
        this.featureApi = featureApi;
        this.npcID = npcID;
        this.key = key;
        this.formationMode = formationMode;
        if (key == LOCATION && formationMode == null) {
            throw new IllegalArgumentException("The location argument requires a location placeholder!");
        }
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final Npc<?> npc = featureApi.getNpc(npcID.getValue(profile), profile);
        return key.resolve(npc, formationMode, decimalPlaces);
    }
}
