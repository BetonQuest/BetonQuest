package org.betonquest.betonquest.quest.objective.arrow;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
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
 * Requires the player to shoot a target with a bow.
 */
public class ArrowShootObjective extends Objective implements Listener {

    /**
     * Location where the arrow should hit.
     */
    private final Variable<Location> location;

    /**
     * Range around the location where the arrow should hit.
     */
    private final Variable<Number> range;

    /**
     * Constructor for the ArrowShootObjective.
     *
     * @param instruction the instruction that created this objective
     * @param location    the location where the arrow should hit
     * @param range       the range around the location where the arrow should hit
     * @throws QuestException if there is an error in the instruction
     */
    public ArrowShootObjective(final Instruction instruction, final Variable<Location> location, final Variable<Number> range) throws QuestException {
        super(instruction);
        this.location = location;
        this.range = range;
    }

    /**
     * Check if the arrow hit the right location.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(final ProjectileHitEvent event) {
        final Projectile arrow = event.getEntity();
        if (arrow.getType() != EntityType.ARROW) {
            return;
        }
        if (!(arrow.getShooter() instanceof final Player player)) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        qeHandler.handle(() -> {
            final Location location = this.location.getValue(onlineProfile);
            final double pRange = range.getValue(onlineProfile).doubleValue();
            // check if the arrow is in the right place in the next tick
            // wait one tick, let the arrow land completely
            new BukkitRunnable() {
                @Override
                public void run() {
                    final Location arrowLocation = arrow.getLocation();
                    if (arrowLocation.getWorld().equals(location.getWorld())
                            && arrowLocation.distanceSquared(location) < pRange * pRange
                            && checkConditions(onlineProfile)) {
                        completeObjective(onlineProfile);
                    }
                }
            }.runTask(BetonQuest.getInstance());
        });
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
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
