package org.betonquest.betonquest.compatibility.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.compatibility.worldguard.npc.NpcRegionConditionFactory;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for WorldGuard.
 */
public class WorldGuardIntegrator implements Integration {

    /**
     * The default constructor.
     */
    public WorldGuardIntegrator() {
    }

    /**
     * Return true if location is inside region.
     *
     * @param loc        Location to Check
     * @param regionName The name of the region
     * @return boolean True if in region
     */
    public static boolean isInsideRegion(@Nullable final Location loc, final String regionName) {
        if (loc == null || loc.getWorld() == null) {
            return false;
        }

        final WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        final RegionManager manager = worldguardPlatform.getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
        if (manager == null) {
            return false;
        }

        final ProtectedRegion region = manager.getRegion(regionName);
        return region != null && region.contains(BukkitAdapter.asBlockVector(loc));
    }

    @Override
    public void enable(final BetonQuestApi api) {
        api.conditions().registry().register("region", new RegionConditionFactory());
        api.objectives().registry().register("region", new RegionObjectiveFactory());
        api.conditions().registry().registerCombined("npcregion", new NpcRegionConditionFactory(api.npcs().manager()));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
