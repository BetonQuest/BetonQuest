package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires the player to manually brew a potion.
 */
@SuppressWarnings("PMD.CommentRequired")
public class
BrewObjective extends CountingObjective implements Listener {
    private final QuestItem potion;

    private final Map<Location, Profile> locations = new HashMap<>();

    public BrewObjective(final Instruction instruction) throws QuestException {
        super(instruction, "potions_to_brew");
        potion = instruction.getQuestItem();
        targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onIngredientPut(final InventoryClickEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getWhoClicked());
        if (!containsPlayer(onlineProfile)) {
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
                    locations.put(brewingStand.getLocation(), onlineProfile);
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
        final Profile profile = locations.remove(event.getBlock().getLocation());
        if (profile == null) {
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

            if (progress > 0 && checkConditions(profile)) {
                getCountingData(profile).progress(progress);
                final boolean completed = completeIfDoneOrNotify(profile);
                if (completed) {
                    final Set<Location> removals = locations.entrySet().stream()
                            .filter(location -> profile.equals(location.getValue()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet());
                    removals.forEach(locations::remove);
                }
            }
        });
    }

    /**
     * Generates an array that matches potions in slots to if they are match the quest item. A filter array can be
     * provided that excludes all indices that are {@code true}.
     *
     * @param inventory  the brewer inventory to check
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
    private boolean checkPotion(@Nullable final ItemStack item) {
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
}
