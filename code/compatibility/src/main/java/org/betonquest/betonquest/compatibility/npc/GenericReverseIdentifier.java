package org.betonquest.betonquest.compatibility.npc;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.npc.NpcReverseIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Allows to get NpcIds for a Npc.
 *
 * @param <T> the relevant original Npc class
 */
public abstract class GenericReverseIdentifier<T> implements NpcReverseIdentifier {

    /**
     * The {@link NpcID} prefix.
     */
    protected final String prefix;

    /**
     * Maps the contents of ids to the ids having that content.
     */
    protected final Map<String, Set<NpcID>> idsByInstruction;

    /**
     * Original Npc class.
     */
    private final Class<T> clazz;

    /**
     * Functions to get all Instruction identifier.
     */
    private final Function<T, String>[] functions;

    /**
     * Create a new Generic Reverse identifier for simple instruction matching.
     *
     * @param prefix    the integration prefix used in Instructions
     * @param clazz     the original npc class to check against
     * @param functions the different ways to construct ids for that npc
     */
    @SafeVarargs
    public GenericReverseIdentifier(final String prefix, final Class<T> clazz, final Function<T, String>... functions) {
        this.prefix = prefix + " ";
        this.clazz = clazz;
        this.functions = functions.clone();
        this.idsByInstruction = new HashMap<>();
    }

    @Override
    public Set<NpcID> getIdsFromNpc(final Npc<?> npc, @Nullable final OnlineProfile profile) {
        if (!clazz.isAssignableFrom(npc.getOriginal().getClass())) {
            return Set.of();
        }
        final T original = (T) npc.getOriginal();
        final Set<NpcID> valid = new HashSet<>();
        for (final Function<T, String> function : functions) {
            final Set<NpcID> identifiers = idsByInstruction.get(prefix + function.apply(original));
            if (identifiers != null) {
                valid.addAll(identifiers);
            }
        }
        return valid;
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
