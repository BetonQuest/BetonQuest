package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.hologram.Hologram;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.util.NpcLocation;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Optional;

/**
 * ZNPCsPlus Compatibility Adapter for general BetonQuest NPC behaviour.
 */
public class ZNPCsPlusAdapter implements Npc<NpcEntry> {
    /**
     * The ZNPCsPlus NPC instance.
     */
    private final NpcEntry entry;

    /**
     * Create a new ZNPCsPlus NPC Adapter.
     *
     * @param npcEntry the FancyNpcs NPC entry
     */
    public ZNPCsPlusAdapter(final NpcEntry npcEntry) {
        this.entry = npcEntry;
    }

    @Override
    public NpcEntry getOriginal() {
        return entry;
    }

    @Override
    public String getName() {
        return ChatColor.stripColor(getHoloName());
    }

    @Override
    public String getFormattedName() {
        return getHoloName();
    }

    @Override
    public Optional<Location> getLocation() {
        return Optional.of(entry.getNpc().getLocation().toBukkitLocation(entry.getNpc().getWorld()));
    }

    @Override
    public Optional<Location> getEyeLocation() {
        return getLocation();
    }

    @Override
    public void teleport(final Location location) {
        entry.getNpc().setLocation(new NpcLocation(location));
        Bukkit.getPluginManager().callEvent(new NpcVisibilityUpdateEvent(this));
    }

    @Override
    public boolean isSpawned() {
        return entry.getNpc().isEnabled();
    }

    @Override
    public void spawn(final Location location) {
        entry.getNpc().setEnabled(true);
        teleport(location);
    }

    @Override
    public void despawn() {
        entry.getNpc().setEnabled(false);
        Bukkit.getPluginManager().callEvent(new NpcVisibilityUpdateEvent(this));
    }

    @Override
    public void show(final OnlineProfile onlineProfile) {
        entry.getNpc().show(onlineProfile.getPlayer());
    }

    @Override
    public void hide(final OnlineProfile onlineProfile) {
        entry.getNpc().hide(onlineProfile.getPlayer());
    }

    private String getHoloName() {
        final Hologram hologram = entry.getNpc().getHologram();
        if (hologram.lineCount() == 0) {
            return "";
        }
        return hologram.getLine(0);
    }
}
