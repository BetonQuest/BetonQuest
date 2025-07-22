package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * FancyNpcs Compatibility Adapter for general BetonQuest NPC behaviour.
 */
public class FancyAdapter implements org.betonquest.betonquest.api.quest.npc.Npc<Npc> {
    /**
     * The FancyNpcs NPC instance.
     */
    private final Npc npc;

    /**
     * Create a new FancyNpcs NPC Adapter.
     *
     * @param npc the FancyNpcs NPC instance
     */
    public FancyAdapter(final Npc npc) {
        this.npc = npc;
    }

    @Override
    public void teleport(final Location location) {
        npc.getData().setLocation(location);
        npc.moveForAll();
    }

    @Override
    public void spawn(final Location location) {
        if (!npc.getData().isSpawnEntity()) {
            npc.getData().setLocation(location);
            npc.spawnForAll();
        }
    }

    @Override
    public void despawn() {
        npc.removeForAll();
    }

    @Override
    public void show(final OnlineProfile onlineProfile) {
        Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
            final Player player = onlineProfile.getPlayer();
            final Boolean isVisible = npc.getIsVisibleForPlayer().get(player.getUniqueId());
            if (isVisible == null || !isVisible) {
                npc.spawn(player);
            }
        });
    }

    @Override
    public void hide(final OnlineProfile onlineProfile) {
        Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
            final Player player = onlineProfile.getPlayer();
            final Boolean isVisible = npc.getIsVisibleForPlayer().get(player.getUniqueId());
            if (isVisible == null || isVisible) {
                npc.remove(player);
            }
        });
    }

    @Override
    public Npc getOriginal() {
        return npc;
    }

    @Override
    public String getName() {
        return npc.getData().getName();
    }

    @Override
    public String getFormattedName() {
        return npc.getData().getDisplayName();
    }

    @Override
    public Location getLocation() {
        return npc.getData().getLocation().clone();
    }

    @Override
    public Location getEyeLocation() {
        return getLocation().add(0, npc.getEyeHeight(), 0);
    }

    @Override
    public boolean isSpawned() {
        return npc.getData().isSpawnEntity();
    }
}
