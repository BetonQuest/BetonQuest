package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcReverseIdentifier;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores the npc types that can be used in BetonQuest.
 */
public class NpcTypeRegistry extends FactoryTypeRegistry<NpcWrapper<?>> implements NpcRegistry {

    /**
     * Instruction API.
     */
    protected final InstructionApi instructionApi;

    /**
     * Identifier to get {@link NpcIdentifier}s from a specific Npc.
     */
    private final List<NpcReverseIdentifier> reverseIdentifiers;

    /**
     * Create a new npc type registry.
     *
     * @param log            the logger that will be used for logging
     * @param instructionApi the instruction api
     */
    public NpcTypeRegistry(final BetonQuestLogger log, final InstructionApi instructionApi) {
        super(log, "npc");
        this.instructionApi = instructionApi;
        this.reverseIdentifiers = new ArrayList<>();
    }

    @Override
    public void registerIdentifier(final NpcReverseIdentifier identifier) {
        reverseIdentifiers.add(identifier);
    }

    /**
     * Adds the id the potential reachable ids to the reverse search.
     *
     * @param npcId the id to add store in the mapping
     */
    public void addIdentifier(final NpcIdentifier npcId) {
        final String resolved;
        try {
            final Instruction instruction = instructionApi.createInstruction(npcId, npcId.readRawInstruction());
            resolved = instruction.chainForArgument(instruction.toString()).string().get().getValue(null);
        } catch (final QuestException e) {
            log.warn("Could not resolve variables in npc id '" + npcId + "' to add reverse identifier: " + e.getMessage(), e);
            return;
        }
        for (final NpcReverseIdentifier identifier : reverseIdentifiers) {
            identifier.addID(npcId, resolved);
        }
    }

    /**
     * Resets all stored values from the reverse search.
     */
    public void resetIdentifier() {
        for (final NpcReverseIdentifier reverseIdentifier : reverseIdentifiers) {
            reverseIdentifier.reset();
        }
    }

    @Override
    public Set<NpcIdentifier> getIdentifier(final Npc<?> npc, @Nullable final OnlineProfile profile) {
        final Set<NpcIdentifier> npcIds = new HashSet<>();
        for (final NpcReverseIdentifier backFire : reverseIdentifiers) {
            npcIds.addAll(backFire.getIdsFromNpc(npc, profile));
        }
        return npcIds;
    }
}
