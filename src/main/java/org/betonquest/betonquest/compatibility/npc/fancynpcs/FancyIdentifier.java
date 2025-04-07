package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcReverseIdentifier;
import org.betonquest.betonquest.id.NpcID;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Allows to get NpcIds for a FancyNpcs Npc.
 */
public class FancyIdentifier implements NpcReverseIdentifier {

    /**
     * The {@link NpcID} prefix.
     */
    protected final String prefix;

    /**
     * Maps the contents of ids to the ids having that content.
     */
    protected final Map<String, Set<NpcID>> idsByInstruction;

    /**
     * Create a new Fancy Identifier.
     *
     * @param prefix the prefix of relevant Ids
     */
    public FancyIdentifier(final String prefix) {
        this.prefix = prefix + " ";
        this.idsByInstruction = new HashMap<>();
    }

    @Override
    public Set<NpcID> getIdsFromNpc(final Npc<?> npc, @Nullable final OnlineProfile profile) {
        if (!(npc.getOriginal() instanceof final de.oliver.fancynpcs.api.Npc original)) {
            return Set.of();
        }
        final Set<NpcID> ids = new HashSet<>();
        final Set<NpcID> byID = idsByInstruction.get(prefix + original.getData().getId());
        if (byID != null) {
            ids.addAll(byID);
        }
        final Set<NpcID> byName = idsByInstruction.get(prefix + npc.getName() + " byName");
        if (byName != null) {
            ids.addAll(byName);
        }
        return ids;
    }

    @Override
    public void addID(final NpcID npcId) {
        final String instruction = npcId.getInstruction().toString();
        if (instruction.startsWith(prefix)) {
            idsByInstruction.computeIfAbsent(instruction, string -> new HashSet<>()).add(npcId);
        }
    }

    @Override
    public void reset() {
        idsByInstruction.clear();
    }
}
