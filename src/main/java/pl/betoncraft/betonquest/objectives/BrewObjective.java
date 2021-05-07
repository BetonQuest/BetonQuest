package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Requires the player to manually brew a potion.
 */
@SuppressWarnings("PMD.CommentRequired")
public class BrewObjective extends Objective implements Listener {

    private final QuestItem potion;
    private final int targetAmount;
    private final boolean notify;
    private final int notifyInterval;
    private final Map<Location, String> locations = new HashMap<>();

    public BrewObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = PotionData.class;
        potion = instruction.getQuestItem();
        targetAmount = instruction.getInt();
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onIngredientPut(final InventoryClickEvent event) {
        final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
        if (!containsPlayer(playerID)) {
            return;
        }
        final Inventory topInventory = event.getView().getTopInventory();
        if (event.getRawSlot() < 0 || topInventory.getType() != InventoryType.BREWING) {
            return;
        }

        final ItemStack[] contentBefore = topInventory.getContents();
        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
            final ItemStack[] contentAfter = topInventory.getContents();
            if (itemsAdded(contentBefore, contentAfter)) {
                final BrewingStand brewingStand = (BrewingStand) topInventory.getHolder();
                if (brewingStand != null) {
                    locations.put(brewingStand.getLocation(), playerID);
                }
            }
        });
    }

    @SuppressWarnings("PMD.UseVarargs")
    private boolean itemsAdded(final ItemStack[] contentBefore, final ItemStack[] contentAfter) {
        for (int i = 0; i < contentBefore.length; i++) {
            final ItemStack after = contentAfter[i];
            if (after != null && !after.getType().isAir()) {
                final ItemStack before = contentBefore[i];
                if (before == null || !before.isSimilar(after) || before.getAmount() < after.getAmount()) {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrew(final BrewEvent event) {
        final String playerID = locations.remove(event.getBlock().getLocation());
        if (playerID == null) {
            return;
        }
        final boolean[] alreadyDone = getMatchingPotions(event.getContents());

        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
            final boolean[] newlyDone = getMatchingPotions(event.getContents(), alreadyDone);

            int progress = 0;
            for (final boolean brewed : newlyDone) {
                if (brewed) {
                    progress++;
                }
            }

            if (progress > 0 && checkConditions(playerID)) {
                final PotionData data = (PotionData) dataMap.get(playerID);
                final int previousAmount = data.getAmount();
                data.brew(progress);

                if (data.getAmount() >= targetAmount) {
                    completeObjective(playerID);
                    final Set<Location> removals = locations.entrySet().stream()
                            .filter(location -> playerID.equals(location.getValue()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet());
                    removals.forEach(locations::remove);
                } else if (notify && data.getAmount() / notifyInterval != previousAmount / notifyInterval) {
                    try {
                        Config.sendNotify(instruction.getPackage().getName(), playerID, "potions_to_brew",
                                new String[]{String.valueOf(targetAmount - data.getAmount())},
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
        });
    }

    /**
     * Generates an array that matches potions in slots to if they are match the quest item. A filter array can be
     * provided that excludes all indices that are {@code true}.
     *
     * @param inventory the brewer inventory to check
     * @param exclusions the excluded indices
     * @return array mapping slot index to potion match
     */
    private boolean[] getMatchingPotions(final BrewerInventory inventory, final boolean... exclusions) {
        final boolean[] resultPotions = new boolean[3];
        final ItemStack[] storageContents = inventory.getStorageContents();
        for (int index = 0; index < 3; index++) {
            resultPotions[index] = (exclusions.length <= index || !exclusions[index])
                    && checkPotion(storageContents[index]);
        }
        return resultPotions;
    }

    /**
     * Check if the {@link ItemStack} matches the potion defined in the objective.
     */
    private boolean checkPotion(final ItemStack item) {
        return item != null && potion.compare(item);
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

    @Override
    public String getProperty(final String name, final String playerID) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "amount":
                return Integer.toString(((PotionData) dataMap.get(playerID)).getAmount());
            case "left":
                return Integer.toString(targetAmount - ((PotionData) dataMap.get(playerID)).getAmount());
            case "total":
                return Integer.toString(targetAmount);
            default:
                return "";
        }
    }

    public static class PotionData extends ObjectiveData {

        private int amount;

        public PotionData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void brew(final int additional) {
            amount += additional;
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
