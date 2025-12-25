package org.betonquest.betonquest.quest.objective.ride;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Optional;

/**
 * Requires the player to ride a vehicle.
 */
public class RideObjective extends Objective implements Listener {

    /**
     * The type of vehicle that is required or an empty optional if any vehicle is allowed.
     */
    private final Argument<Optional<EntityType>> vehicle;

    /**
     * Constructor for the RideObjective.
     *
     * @param instruction the instruction that created this objective
     * @param vehicle     the type of vehicle that is required, or null if any vehicle is allowed
     * @throws QuestException if there is an error in the instruction
     */
    public RideObjective(final Instruction instruction, final Argument<Optional<EntityType>> vehicle) throws QuestException {
        super(instruction);
        this.vehicle = vehicle;
    }

    /**
     * Check if the player is riding the right vehicle.
     *
     * @param event the event to check
     */
    @EventHandler(ignoreCancelled = true)
    public void onMount(final EntityMountEvent event) {
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }
        qeHandler.handle(() -> {
            final OnlineProfile onlineProfile = profileProvider.getProfile(player);
            final Optional<EntityType> entityType = vehicle.getValue(onlineProfile);
            final boolean matchType = entityType.map(type -> type == event.getMount().getType()).orElse(true);
            if (containsPlayer(onlineProfile) && matchType && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        });
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
