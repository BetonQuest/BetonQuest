package org.betonquest.betonquest.quest.objective.ride;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityMountEvent;

/**
 * Requires the player to ride a vehicle.
 */
public class RideObjective extends Objective implements Listener {
    /**
     * The type of vehicle that is required, or null if any vehicle is allowed.
     */
    @Nullable
    private final Variable<EntityType> vehicle;

    /**
     * Constructor for the RideObjective.
     *
     * @param instruction the instruction that created this objective
     * @param vehicle     the type of vehicle that is required, or null if any vehicle is allowed
     * @throws QuestException if there is an error in the instruction
     */
    public RideObjective(final Instruction instruction, @Nullable final Variable<EntityType> vehicle) throws QuestException {
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
            if (containsPlayer(onlineProfile) && (vehicle == null || event.getMount().getType() == vehicle.getValue(onlineProfile)) && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
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
