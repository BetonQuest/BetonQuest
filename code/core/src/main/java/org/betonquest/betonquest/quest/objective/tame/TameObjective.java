package org.betonquest.betonquest.quest.objective.tame;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * The player must tame specified amount of specified mobs.
 */
public class TameObjective extends CountingObjective implements Listener {

    /**
     * The entity type to be tamed.
     */
    private final Variable<EntityType> type;

    /**
     * Constructor for the TameObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of entities to be tamed
     * @param type         the entity type to be tamed
     * @throws QuestException if there is an error in the instruction
     */
    public TameObjective(final Instruction instruction, final Variable<Number> targetAmount,
                         final Variable<EntityType> type) throws QuestException {
        super(instruction, targetAmount, "animals_to_tame");
        this.type = type;
    }

    /**
     * Handles the taming event.
     *
     * @param event the taming event
     */
    @EventHandler(ignoreCancelled = true)
    public void onTaming(final EntityTameEvent event) {
        qeHandler.handle(() -> {
            if (event.getOwner() instanceof final Player player) {
                final OnlineProfile onlineProfile = profileProvider.getProfile(player);
                if (containsPlayer(onlineProfile) && type.getValue(onlineProfile) == event.getEntity().getType() && checkConditions(onlineProfile)) {
                    getCountingData(onlineProfile).progress();
                    completeIfDoneOrNotify(onlineProfile);
                }
            }
        });
    }
}
