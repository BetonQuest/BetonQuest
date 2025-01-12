package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

@SuppressWarnings("PMD.CommentRequired")
public class BreedObjective extends CountingObjective implements Listener {

    private final EntityType type;

    public BreedObjective(final Instruction instruction) throws QuestException {
        super(instruction, "animals_to_breed");
        type = instruction.getEntity();
        targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getEntityType() == type && event.getBreeder() instanceof Player) {
            final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getBreeder());
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
