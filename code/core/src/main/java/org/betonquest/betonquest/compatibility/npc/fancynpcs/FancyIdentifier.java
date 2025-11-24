package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.compatibility.npc.GenericReverseIdentifier;

/**
 * Allows to get NpcIds for a FancyNpcs Npc.
 */
public class FancyIdentifier extends GenericReverseIdentifier<Npc> {

    /**
     * Create a new Fancy Identifier.
     *
     * @param prefix the prefix of relevant Ids
     */
    public FancyIdentifier(final String prefix) {
        super(prefix, Npc.class, original -> original.getData().getId(),
                original -> original.getData().getName() + " byName");
    }
}
