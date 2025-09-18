package org.betonquest.betonquest.compatibility.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.worldguard.npc.NpcRegionConditionFactory;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for WorldGuard.
 */
public class WorldGuardIntegrator implements Integrator {

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
    public void hook(final BetonQuestApi api) {
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        questRegistries.condition().register("region", new RegionConditionFactory(api.getLoggerFactory(), data));
        questRegistries.objective().register("region", new RegionObjectiveFactory());
        questRegistries.condition().registerCombined("npcregion", new NpcRegionConditionFactory(api.getFeatureApi(), data));
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
