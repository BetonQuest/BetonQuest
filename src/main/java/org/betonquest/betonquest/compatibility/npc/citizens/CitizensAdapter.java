package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.protocollib.hider.CitizensHider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Citizens Compatibility Adapter for BetonQuest Npcs.
 */
public class CitizensAdapter implements Npc<NPC> {
    /**
     * The Citizens NPC instance.
     */
    private final NPC npc;

    /**
     * Create a new Citizens Npc Adapter.
     *
     * @param npc the Citizens NPC instance
     */
    public CitizensAdapter(final NPC npc) {
        this.npc = npc;
    }

    @Override
    public NPC getOriginal() {
        return npc;
    }

    @Override
    public String getName() {
        return npc.getName();
    }

    @Override
    public String getFormattedName() {
        return npc.getFullName();
    }

    @Override
    public Location getLocation() {
        return npc.getEntity().getLocation();
    }

    @Override
    public Location getEyeLocation() {
        final Entity entity = npc.getEntity();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity) entity).getEyeLocation();
        }
        return npc.getEntity().getLocation();
    }

    @Override
    public void teleport(final Location location) {
        CitizensIntegrator.getCitizensMoveInstance().stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
        if (npc.isSpawned()) {
            npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    @Override
    public boolean isSpawned() {
        return npc.isSpawned();
    }

    @Override
    public void spawn(final Location location) {
        if (!npc.isSpawned()) {
            npc.spawn(location, SpawnReason.PLUGIN);
        }
    }

    @Override
    public void despawn() {
        if (npc.isSpawned()) {
            npc.despawn();
        }
    }

    @Override
    public void show(final OnlineProfile onlineProfile) {
        final CitizensHider npcHider = CitizensHider.getInstance();
        if (npcHider != null) {
            npcHider.show(onlineProfile, npc);
        }
    }

    @Override
    public void hide(final OnlineProfile onlineProfile) {
        final CitizensHider npcHider = CitizensHider.getInstance();
        if (npcHider != null) {
            npcHider.hide(onlineProfile, npc);
        }
    }
}
