package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.npc.NpcEntry;
import org.betonquest.betonquest.compatibility.npc.GenericReverseIdentifier;

/**
 * Allows to get NpcIds for a ZNPCsPlus Npc.
 */
public class ZNPCsPlusIdentifier extends GenericReverseIdentifier<NpcEntry> {

    /**
     * Create a new ZNPCsPlus Identifier.
     *
     * @param prefix the prefix of relevant Ids
     */
    public ZNPCsPlusIdentifier(final String prefix) {
        super(prefix, NpcEntry.class, NpcEntry::getId);
    }
}
