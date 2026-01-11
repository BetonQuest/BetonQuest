package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.npc.GenericReverseIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Allows to get NpcIds for a Citizens NPC.
 */
public class CitizensReverseIdentifier extends GenericReverseIdentifier<NPC> {

    /**
     * Registry of NPC to identify.
     */
    private final NPCRegistry registry;

    /**
     * Create a new Identifier.
     *
     * @param registry the source registry for identifiable NPC
     */
    public CitizensReverseIdentifier(final NPCRegistry registry) {
        super("citizens", NPC.class, original -> String.valueOf(original.getId()),
                original -> original.getName() + " byName");
        this.registry = registry;
    }

    @Override
    public Set<NpcIdentifier> getIdsFromNpc(final Npc<?> npc, @Nullable final OnlineProfile profile) {
        if (npc.getOriginal() instanceof final NPC citizen && citizen.getOwningRegistry().equals(registry)) {
            return super.getIdsFromNpc(npc, profile);
        }
        return Set.of();
    }
}
