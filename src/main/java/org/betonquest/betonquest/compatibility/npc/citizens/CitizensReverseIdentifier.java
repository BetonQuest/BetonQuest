package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
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
 * Allows to get NpcIds for a Citizens NPC.
 */
public class CitizensReverseIdentifier implements NpcReverseIdentifier {
    /**
     * The Citizens {@link NpcID} prefix.
     */
    private static final String PREFIX = "citizens ";

    /**
     * Maps the contents of ids to the ids having that content.
     */
    private final Map<String, Set<NpcID>> idsByInstruction;

    /**
     * The default constructor.
     */
    public CitizensReverseIdentifier() {
        idsByInstruction = new HashMap<>();
    }

    @Override
    public Set<NpcID> getIdsFromNpc(final Npc<?> npc, @Nullable final OnlineProfile profile) {
        if (!(npc.getOriginal() instanceof final NPC original)) {
            return Set.of();
        }
        final Set<NpcID> valid = new HashSet<>();
        final Set<NpcID> byID = idsByInstruction.get(PREFIX + original.getId());
        if (byID != null) {
            valid.addAll(byID);
        }
        final Set<NpcID> byName = idsByInstruction.get(PREFIX + original.getName() + " byName");
        if (byName != null) {
            valid.addAll(byName);
        }
        return valid;
    }

    @Override
    public void addID(final NpcID npcId) {
        final String instruction = npcId.getInstruction().toString();
        if (instruction.startsWith(PREFIX)) {
            idsByInstruction.computeIfAbsent(instruction, string -> new HashSet<>()).add(npcId);
        }
    }

    @Override
    public void reset() {
        idsByInstruction.clear();
    }
}
