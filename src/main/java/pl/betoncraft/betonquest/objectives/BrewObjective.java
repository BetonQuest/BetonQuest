package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Requires the player to manually brew a potion.
 */
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
        notify = instruction.hasArgument("notify") || notifyInterval > 0;
    }

    @EventHandler(ignoreCancelled = true)
    public void onIngredientPut(final InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) {
            return;
        }
        if (event.getRawSlot() == 3 || event.getClick().equals(ClickType.SHIFT_LEFT)) {
            final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
            if (!containsPlayer(playerID)) {
                return;
            }
            locations.put(((BrewingStand) event.getInventory().getHolder()).getLocation(), playerID);
        }
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
                        } catch (final InstructionParseException exep) {
                            LogUtils.logThrowableReport(exep);
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
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(amount - ((PotionData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
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
