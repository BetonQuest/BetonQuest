package org.betonquest.betonquest.compatibility.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.NPCRegionCondition;
import org.bukkit.Location;


@SuppressWarnings("PMD.CommentRequired")
public class WorldGuardIntegrator implements Integrator {

    private final BetonQuest plugin;

    public WorldGuardIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    /**
     * Return true if location is inside region
     *
     * @param loc        Location to Check
     * @param regionName The name of the region
     * @return boolean True if in region
     */
    public static boolean isInsideRegion(final Location loc, final String regionName) {
        if (loc == null || loc.getWorld() == null) {
            return false;
        }

        final WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        final RegionManager manager = worldguardPlatform.getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
        if (manager == null) {
            return false;
        }

        final ProtectedRegion region = manager.getRegion(regionName);
        if (region == null) {
            return false;
        }

        return region.contains(BukkitAdapter.asBlockVector(loc));
    }

    @Override
    public void hook() {
        plugin.registerConditions("region", RegionCondition.class);
        plugin.registerObjectives("region", RegionObjective.class);
        if (Compatibility.getHooked().contains("Citizens")) {
            plugin.registerConditions("npcregion", NPCRegionCondition.class);
        }
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
