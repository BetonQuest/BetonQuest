package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Requires the player to shoot a target with a bow
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class ArrowShootObjective extends Objective implements Listener {

    private final CompoundLocation loc;
    private final VariableNumber range;

    public ArrowShootObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        loc = instruction.getLocation();
        range = instruction.getVarNum();
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(final ProjectileHitEvent event) {
        // check if it's the arrow shot by the player with active objectve
        final Projectile arrow = event.getEntity();
        if (arrow.getType() != EntityType.ARROW) {
            return;
        }
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        final Player player = (Player) arrow.getShooter();
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID)) {
            return;
        }
        try {
            final Location location = loc.getLocation(playerID);
            // check if the arrow is in the right place in the next tick
            // wait one tick, let the arrow land completely
            new BukkitRunnable() {
                @Override
                @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
                public void run() {
                    final Location arrowLocation = arrow.getLocation();
                    try {
                        final double pRange = range.getDouble(playerID);
                        if (arrowLocation.getWorld().equals(location.getWorld())
                                && arrowLocation.distanceSquared(location) < pRange * pRange
                                && checkConditions(playerID)) {
                            completeObjective(playerID);
                        }
                    } catch (final QuestRuntimeException e) {
                        LOG.warning(instruction.getPackage(), "Could not resolve range variable: " + e.getMessage(), e);
                    }
                }
            }.runTask(BetonQuest.getInstance());
        } catch (final QuestRuntimeException e) {
            LOG.warning(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
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
