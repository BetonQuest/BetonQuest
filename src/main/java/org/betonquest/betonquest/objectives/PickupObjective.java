package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.logging.Level;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class PickupObjective extends Objective implements Listener {

    private final Item[] pickupItems;

    private final int amount;

    private final boolean notify;

    public PickupObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        template = PickupData.class;

        pickupItems = instruction.getItemList();

        amount = instruction.getInt(instruction.getOptional("amount"), 1);
        notify = instruction.hasArgument("notify");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(final EntityPickupItemEvent event) {
        if (isInvalidItem(event.getItem().getItemStack()) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final String playerID = PlayerConverter.getID(player);

        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }

        final PickupData playerData = getPickupData(playerID);
        final ItemStack pickupItem = event.getItem().getItemStack();

        playerData.pickup(pickupItem.getAmount());

        if (playerData.isFinished()) {
            completeObjective(playerID);
            return;
        }

        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "items_to_pickup", new String[]{Integer.toString(playerData.getAmount())}, "items_to_pickup,info");
            } catch (final QuestRuntimeException exception) {
                try {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'items_to_pickup' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                } catch (final InstructionParseException e) {
                    LOG.reportException(e);
                }
            }
        }
    }

    private boolean isInvalidItem(final ItemStack itemStack) {
        for (final Item item : pickupItems) {
            if (item.isItemEqual(itemStack)) {
                return false;
            }
        }
        return true;
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
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "left":
                return Integer.toString(getPickupData(playerID).getAmount());
            case "amount":
                return Integer.toString(amount);
            default:
                return "";
        }
    }

    private PickupData getPickupData(final String playerID) {
        return (PickupData) dataMap.get(playerID);
    }

    public static class PickupData extends ObjectiveData {

        private int amount;

        public PickupData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void pickup(final int pickupAmount) {
            amount = amount - pickupAmount;
            update();
        }

        private boolean isFinished() {
            return amount <= 0;
        }

        private int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }

    }
}
