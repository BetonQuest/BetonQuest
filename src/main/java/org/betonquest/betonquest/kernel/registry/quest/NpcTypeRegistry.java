package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcReverseIdentifier;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores the npc types that can be used in BetonQuest.
 */
public class NpcTypeRegistry extends FactoryRegistry<TypeFactory<NpcWrapper<?>>> implements NpcRegistry {
    /**
     * Identifier to get {@link NpcID}s from a specific Npc.
     */
    private final List<NpcReverseIdentifier> reverseIdentifiers;

    /**
     * Create a new npc type registry.
     *
     * @param log the logger that will be used for logging
     */
    public NpcTypeRegistry(final BetonQuestLogger log) {
        super(log, "npc");
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
    public void addIdentifier(final NpcID npcId) {
        for (final NpcReverseIdentifier identifier : reverseIdentifiers) {
            identifier.addID(npcId);
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
    public Set<NpcID> getIdentifier(final Npc<?> npc, @Nullable final OnlineProfile profile) {
        final Set<NpcID> npcIds = new HashSet<>();
        for (final NpcReverseIdentifier backFire : reverseIdentifiers) {
            npcIds.addAll(backFire.getIdsFromNpc(npc, profile));
        }
        return npcIds;
    }
}
