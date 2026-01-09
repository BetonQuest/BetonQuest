package org.betonquest.betonquest.quest.objective.arrow;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Requires the player to shoot a target with a bow.
 */
public class ArrowShootObjective extends DefaultObjective {

    /**
     * Location where the arrow should hit.
     */
    private final Argument<Location> location;

    /**
     * Range around the location where the arrow should hit.
     */
    private final Argument<Number> range;

    /**
     * Constructor for the ArrowShootObjective.
     *
     * @param service  the objective factory service
     * @param location the location where the arrow should hit
     * @param range    the range around the location where the arrow should hit
     * @throws QuestException if there is an error in the instruction
     */
    public ArrowShootObjective(final ObjectiveFactoryService service, final Argument<Location> location, final Argument<Number> range) throws QuestException {
        super(service);
        this.location = location;
        this.range = range;
    }

    /**
     * Check if the arrow hit the right location.
     *
     * @param onlineProfile the profile of the player that shot the arrow
     * @param event         the event that triggered this method
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onArrowHit(final ProjectileHitEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final Projectile arrow = event.getEntity();
        if (arrow.getType() != EntityType.ARROW) {
            return;
        }
        final Location location = this.location.getValue(onlineProfile);
        final double pRange = range.getValue(onlineProfile).doubleValue();
        // check if the arrow is in the right place in the next tick
        // wait one tick, let the arrow land completely
        new BukkitRunnable() {
            @Override
            public void run() {
                final Location arrowLocation = arrow.getLocation();
                if (arrowLocation.getWorld().equals(location.getWorld())
                        && arrowLocation.distanceSquared(location) < pRange * pRange) {
                    completeObjective(onlineProfile);
                }
            }
        }.runTask(BetonQuest.getInstance());
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
