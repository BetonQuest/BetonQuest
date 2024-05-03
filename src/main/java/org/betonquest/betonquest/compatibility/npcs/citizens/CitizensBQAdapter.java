package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.bukkit.Location;

public class CitizensBQAdapter implements BQNPCAdapter {
    private final NPC npc;

    public CitizensBQAdapter(final NPC npc) {

        this.npc = npc;
    }

    @Override
    public String getName() {
        return npc.getName();
    }

    @Override
    public String getFullName() {
        return npc.getFullName();
    }

    @Override
    public Location getLocation() {
        return npc.getStoredLocation();
    }
}
