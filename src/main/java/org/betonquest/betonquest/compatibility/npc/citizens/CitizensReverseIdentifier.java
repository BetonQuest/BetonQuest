package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.compatibility.npc.GenericReverseIdentifier;

/**
 * Allows to get NpcIds for a Citizens NPC.
 */
public class CitizensReverseIdentifier extends GenericReverseIdentifier<NPC> {

    /**
     * The default constructor.
     */
    public CitizensReverseIdentifier() {
        super("citizens", NPC.class, original -> String.valueOf(original.getId()),
                original -> original.getName() + " byName");
    }
}
