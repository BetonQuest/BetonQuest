package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class PickupObjective extends CountingObjective implements Listener {

    private final Item[] pickupItems;

    public PickupObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "items_to_pickup");
        pickupItems = instruction.getItemList();
        targetAmount = instruction.getInt(instruction.getOptional("amount"), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(final EntityPickupItemEvent event) {
        if (isValidItem(event.getItem().getItemStack()) && event.getEntity() instanceof Player) {
            final OnlineProfile profile = PlayerConverter.getID((Player) event.getEntity());

            if (containsPlayer(profile) && checkConditions(profile)) {
                final ItemStack pickupItem = event.getItem().getItemStack();
                getCountingData(profile).progress(pickupItem.getAmount());
                completeIfDoneOrNotify(profile);
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
