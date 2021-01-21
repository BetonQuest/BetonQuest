package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Player has to enter the WorldGuard region
 */
@SuppressWarnings("PMD.CommentRequired")
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String playerID = PlayerConverter.getID(player);
        if (containsPlayer(playerID)) {
            final boolean inside = WorldGuardIntegrator.isInsideRegion(player.getLocation(), name);
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

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void checkLocation(final Player player, final Location location) {
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID)) {
            return;
        }

        final boolean inside = WorldGuardIntegrator.isInsideRegion(location, name);

        if (!entry && !exit) {
            if (inside && checkConditions(playerID)) {
                completeObjective(playerID);
            }
            return;
        }
        if (!playersInsideRegion.containsKey(player.getUniqueId())) {
            playersInsideRegion.put(player.getUniqueId(), WorldGuardIntegrator.isInsideRegion(player.getLocation(), name));
        }
        final boolean fromInside = playersInsideRegion.get(player.getUniqueId());
        playersInsideRegion.put(player.getUniqueId(), inside);

        if ((entry && inside && !fromInside || exit && fromInside && !inside) && checkConditions(playerID)) {
            completeObjective(playerID);
            playersInsideRegion.remove(player.getUniqueId());
        }
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
