package pl.betoncraft.betonquest.compatibility.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Player has to enter the WorldGuard region
 */
public class RegionObjective extends Objective implements Listener {

    private final String name;
    private final boolean entry;
    private final boolean exit;
    private final Map<UUID, Boolean> playersInsideRegion;

    public RegionObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        name = instruction.next();
        entry = instruction.hasArgument("entry");
        exit = instruction.hasArgument("exit");
        playersInsideRegion = new HashMap<>();
    }

    /**
     * Return true if location is inside region
     *
     * @param loc Location to Check
     * @return boolean True if in region
     */
    private boolean isInsideRegion(final Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return false;
        }

        final WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        final RegionManager manager = worldguardPlatform.getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
        if (manager == null) {
            return false;
        }

        final ProtectedRegion region = manager.getRegion(name);
        if (region == null) {
            return false;
        }

        return region.contains(BukkitAdapter.asBlockVector(loc));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        checkLocation(event.getEntity(), event.getEntity().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        checkLocation(event.getPlayer(), event.getRespawnLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent event) {
        onMove(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        checkLocation(event.getPlayer(), event.getTo());
    }

    private void checkLocation(final Player player, final Location location) {
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID)) {
            return;
        }

        final boolean inside = isInsideRegion(location);

        if (!entry && !exit) {
            if (inside && checkConditions(playerID)) {
                completeObjective(playerID);
            }
            return;
        }
        if (!playersInsideRegion.containsKey(player.getUniqueId())) {
            playersInsideRegion.put(player.getUniqueId(), isInsideRegion(player.getLocation()));
        }
        final boolean fromInside = playersInsideRegion.get(player.getUniqueId());

        if (entry && inside && !fromInside && checkConditions(playerID) || exit && fromInside && !inside && checkConditions(playerID)) {
            completeObjective(playerID);
            playersInsideRegion.remove(player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String playerID = PlayerConverter.getID(player);
        if (containsPlayer(playerID)) {
            final boolean inside = isInsideRegion(player.getLocation());
            if (!entry && !exit && inside && checkConditions(playerID)) {
                completeObjective(playerID);
            } else {
                playersInsideRegion.put(event.getPlayer().getUniqueId(), inside);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        playersInsideRegion.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
