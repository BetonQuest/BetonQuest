package pl.betoncraft.betonquest.compatibility.worldguard;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player is in specified region
 */
public class RegionCondition extends Condition {

    private final String name;

    public RegionCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        name = instruction.next();
    }

    @Override
    protected Boolean execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        final WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        final RegionManager manager = worldguardPlatform.getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        if (manager == null) {
            return false;
        }
        final ProtectedRegion region = manager.getRegion(name);
        final ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        for (final ProtectedRegion compare : set) {
            if (compare.equals(region)) {
                return true;
            }
        }
        return false;
    }

}
