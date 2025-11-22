package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.compatibility.npc.GenericReverseIdentifier;

/**
 * Allows to get {@link NpcID}s for an {@link ActiveMob}.
 */
public class MythicMobsReverseIdentifier extends GenericReverseIdentifier<ActiveMob> {

    /**
     * The default constructor.
     */
    public MythicMobsReverseIdentifier() {
        super("mythicmobs", ActiveMob.class,
                Type.MYTHIC_MOB::toInstructionString,
                Type.UUID::toInstructionString,
                Type.FACTION::toInstructionString);
    }
}
