package org.betonquest.betonquest.quest.objective.shear;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Requires the player to shear a sheep.
 */
public class ShearObjective extends CountingObjective implements Listener {

    /**
     * The color of the sheep to shear.
     */
    @Nullable
    private final Variable<DyeColor> color;

    /**
     * The name of the sheep to shear.
     */
    @Nullable
    private final String name;

    /**
     * Constructor for the ShearObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of sheep to shear
     * @param name         the name of the sheep to shear
     * @param color        the color of the sheep to shear
     * @throws QuestException if there is an error in the instruction
     */
    public ShearObjective(final Instruction instruction, final Variable<Number> targetAmount, @Nullable final String name,
                          @Nullable final Variable<DyeColor> color) throws QuestException {
        super(instruction, targetAmount, "sheep_to_shear");
        this.name = name;
        this.color = color;
    }

    /**
     * Check if the player sheared the right sheep.
     *
     * @param event the event that triggered when the player sheared the sheep
     */
    @EventHandler(ignoreCancelled = true)
    public void onShear(final PlayerShearEntityEvent event) {
        if (event.getEntity().getType() != EntityType.SHEEP) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        qeHandler.handle(() -> {
            if (containsPlayer(onlineProfile)
                    && (name == null || name.equals(event.getEntity().getCustomName()))
                    && (color == null || color.getValue(onlineProfile).equals(((Sheep) event.getEntity()).getColor()))
                    && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
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
}
