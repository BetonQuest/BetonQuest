package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("PMD.CommentRequired")
public class PickupObjective extends CountingObjective implements Listener {

    private final Item[] pickupItems;

    public PickupObjective(final Instruction instruction) throws QuestException {
        super(instruction, "items_to_pickup");
        pickupItems = instruction.getItemList();
        targetAmount = instruction.get(instruction.getOptional("amount", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(final EntityPickupItemEvent event) {
        if (isValidItem(event.getItem().getItemStack()) && event.getEntity() instanceof Player) {
            final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getEntity());

            if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                final ItemStack pickupItem = event.getItem().getItemStack();
                getCountingData(onlineProfile).progress(pickupItem.getAmount());
                completeIfDoneOrNotify(onlineProfile);
            }
        }
    }

    private boolean isValidItem(final ItemStack itemStack) {
        for (final Item item : pickupItems) {
            if (item.isItemEqual(itemStack)) {
                return true;
            }
        }
        return false;
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
