package pl.betoncraft.betonquest.compatibility.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.citizens.NPCRegionCondition;


@SuppressWarnings("PMD.CommentRequired")
public class WorldGuardIntegrator implements Integrator {

    private final BetonQuest plugin;

    public WorldGuardIntegrator() {
        plugin = BetonQuest.getInstance();
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


    /**
     * Return true if location is inside region
     *
     * @param loc Location to Check
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
}
