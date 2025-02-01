package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.jetbrains.annotations.Nullable;

/**
 * Teleports a Npc to a given location.
 */
public class NPCTeleportEvent implements NullableEvent {
    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * The npc id.
     */
    private final NpcID npcId;

    /**
     * The location to teleport the Npc to.
     */
    private final VariableLocation location;

    /**
     * Create a new Npc Teleport Event.
     *
     * @param npcProcessor the processor to get npc
     * @param npcId        the npc id
     * @param location     the location the Npc will be teleported to
     */
    public NPCTeleportEvent(final NpcProcessor npcProcessor, final NpcID npcId, final VariableLocation location) {
        this.npcProcessor = npcProcessor;
        this.npcId = npcId;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        npcProcessor.getNpc(npcId).teleport(location.getValue(profile));
    }
}
