package org.betonquest.betonquest.quest.variable.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.quest.variable.location.LocationFormationMode;
import org.jetbrains.annotations.Nullable;

import static org.betonquest.betonquest.quest.variable.npc.Argument.LOCATION;

/**
 * Provides information about a npc.
 */
public class NpcVariable implements PlayerlessVariable {

    /**
     * The Supplier for the NPC.
     */
    private final NpcProcessor npcProcessor;

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
     * @param npcProcessor  the processor to get npc
     * @param npcID         the npc id
     * @param key           the argument defining the value
     * @param formationMode the location formation mode to use for location resolution
     * @param decimalPlaces the number of decimal places to use for location resolution
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public NpcVariable(final NpcProcessor npcProcessor, final NpcID npcID, final Argument key,
                       @Nullable final LocationFormationMode formationMode, final int decimalPlaces) {
        this.npcProcessor = npcProcessor;
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
        final Npc<?> npc = npcProcessor.getNpc(npcID);
        return key.resolve(npc, formationMode, decimalPlaces);
    }
}
