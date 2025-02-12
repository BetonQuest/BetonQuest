package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * The player must tame specified amount of specified mobs.
 */
@SuppressWarnings("PMD.CommentRequired")
public class TameObjective extends CountingObjective implements Listener {

    private final EntityType type;

    public TameObjective(final Instruction instruction) throws QuestException {
        super(instruction, "animals_to_tame");

        type = instruction.getEnum(EntityType.class);
        if (type.getEntityClass() == null || !Tameable.class.isAssignableFrom(type.getEntityClass())) {
            throw new QuestException("Entity cannot be tamed: " + type);
        }

        targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTaming(final EntityTameEvent event) {
        if (event.getOwner() instanceof Player) {
            final ProfileProvider profileProvider = BetonQuest.getInstance().getProfileProvider();
            final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getOwner());
            if (containsPlayer(onlineProfile) && type.equals(event.getEntity().getType()) && checkConditions(onlineProfile)) {
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
