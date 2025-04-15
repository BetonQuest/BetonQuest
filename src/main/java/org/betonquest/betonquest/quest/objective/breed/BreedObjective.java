package org.betonquest.betonquest.quest.objective.breed;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

/**
 * Requires the player to breed a specific type of animal.
 */
public class BreedObjective extends CountingObjective implements Listener {
    /**
     * The type of animal to breed.
     */
    private final EntityType type;

    /**
     * Constructor for the BreedObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param type         the type of animal to breed
     * @param targetAmount the target amount of animals to breed
     * @throws QuestException if there is an error in the instruction
     */
    public BreedObjective(final Instruction instruction, final EntityType type, final VariableNumber targetAmount) throws QuestException {
        super(instruction, "animals_to_breed");
        this.targetAmount = targetAmount;
        this.type = type;
    }

    /**
     * Check if the player is breeding the right type of animal.
     *
     * @param event the event that triggered the breeding
     */
    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getEntityType() == type && event.getBreeder() instanceof Player) {
            final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getBreeder());
            if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
            }
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
}
