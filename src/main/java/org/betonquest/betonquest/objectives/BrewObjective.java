package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Requires the player to manually brew a potion.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class BrewObjective extends Objective implements Listener {

    private final QuestItem potion;
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;
    private final Map<Location, String> locations = new HashMap<>();

    public BrewObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = PotionData.class;
        potion = instruction.getQuestItem();
        amount = instruction.getInt();
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @EventHandler(ignoreCancelled = false)
    public void onIngredientPut(final InventoryClickEvent event) {
        final Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.getType() != InventoryType.BREWING || event.getRawSlot() < 0) {
            return;
        }
        final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
        if (!containsPlayer(playerID)) {
            return;
        }

        final ItemStack[] contentBefore = topInventory.getStorageContents();
        new BukkitRunnable() {
            @Override
            @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
            public void run() {
                final ItemStack[] contentAfter = topInventory.getStorageContents();
                if (!itemsAdded(contentBefore, contentAfter)) {
                    return;
                }
                locations.put(((BrewingStand) topInventory.getHolder()).getLocation(), playerID);
            }
        }.runTask(BetonQuest.getInstance());
    }

    @SuppressWarnings("PMD.UseVarargs")
    private boolean itemsAdded(final ItemStack[] contentBefore, final ItemStack[] contentAfter) {
        for (int i = 0; i < contentBefore.length; i++) {
            final ItemStack before = contentBefore[i];
            final ItemStack after = contentAfter[i];
            if (before == null && after != null) {
                return true;
            }
            if (before != null && after != null) {
                if (before.getType() == after.getType()) {
                    if (before.getAmount() < after.getAmount()) {
                        return true;
                    }
                } else if (after.getType() != Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBrew(final BrewEvent event) {
        final String playerID = locations.remove(event.getBlock().getLocation());
        if (playerID == null) {
            return;
        }
        final PotionData data = (PotionData) dataMap.get(playerID);
        // this tracks how many potions there are in the stand before brewing
        int alreadyExistingTemp = 0;
        for (int i = 0; i < 3; i++) {
            if (checkPotion(event.getContents().getItem(i))) {
                alreadyExistingTemp++;
            }
        }
        // making it final for the runnable
        final int alreadyExisting = alreadyExistingTemp;
        new BukkitRunnable() {
            @Override
            public void run() {
                // unfinaling it for modifications
                int alreadyExistingFinal = alreadyExisting;
                for (int i = 0; i < 3; i++) {
                    // if there were any potions before, don't count them to
                    // prevent cheating
                    if (checkPotion(event.getContents().getItem(i))) {
                        if (alreadyExistingFinal <= 0 && checkConditions(playerID)) {
                            data.brew();
                        }
                        alreadyExistingFinal--;
                    }
                }
                // check if the objective has been completed
                if (data.getAmount() >= amount) {
                    completeObjective(playerID);
                } else if (notify && data.getAmount() % notifyInterval == 0) {
                    try {
                        Config.sendNotify(instruction.getPackage().getName(), playerID, "potions_to_brew",
                                new String[]{String.valueOf(amount - data.getAmount())},
                                "potions_to_brew,info");
                    } catch (final QuestRuntimeException exception) {
                        try {
                            LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'potions_to_brew' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                        } catch (final InstructionParseException e) {
                            LOG.reportException(e);
                        }
                    }
                }
            }
        }.runTask(BetonQuest.getInstance());
    }

    /**
     * Checks if this ItemStack matches a potion defined in "effects" HashMap.
     */
    private boolean checkPotion(final ItemStack item) {
        if (item == null) {
            return false;
        }
        return potion.compare(item);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(amount - ((PotionData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(((PotionData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        locations.clear();
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "0";
    }

    public static class PotionData extends ObjectiveData {

        private int amount;

        public PotionData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void brew() {
            amount++;
            update();
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
