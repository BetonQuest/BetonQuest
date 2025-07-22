package org.betonquest.betonquest.quest.objective.brew;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.profile.ProfileValueMap;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.item.QuestItem;
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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires the player to manually brew a potion.
 */
public class BrewObjective extends CountingObjective implements Listener {

    /**
     * The potion item to brew.
     */
    private final Variable<Item> potion;

    /**
     * A cache of brewing stands and their owners.
     */
    private final Map<Location, Profile> locations;

    /**
     * The target amount of potions to brew.
     *
     * @param instruction     the instruction that created this objective
     * @param targetAmount    the target amount of potions to brew
     * @param profileProvider the profile provider to get the profile of the player
     * @param potion          the potion item to brew
     * @throws QuestException if there is an error in the instruction
     */
    public BrewObjective(final Instruction instruction, final Variable<Number> targetAmount,
                         final ProfileProvider profileProvider, final Variable<Item> potion) throws QuestException {
        super(instruction, targetAmount, "potions_to_brew");
        this.potion = potion;
        this.locations = new ProfileValueMap<>(profileProvider);
    }

    /**
     * Checks if the player put an ingredient into the brewing stand.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onIngredientPut(final InventoryClickEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getWhoClicked());
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

    /**
     * Checks who put the ingredient into the brewing stand and if it was a valid.
     *
     * @param event the event that triggered this method
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBrew(final BrewEvent event) {
        final Profile profile = locations.remove(event.getBlock().getLocation());
        if (profile == null) {
            return;
        }
        qeHandler.handle(() -> {
            final QuestItem potion = this.potion.getValue(profile).getItem(profile);
            final boolean[] alreadyDone = getMatchingPotions(potion, event.getContents());

            Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
                final boolean[] newlyDone = getMatchingPotions(potion, event.getContents(), alreadyDone);

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
    private boolean[] getMatchingPotions(final QuestItem potion, final BrewerInventory inventory, final boolean... exclusions) {
        final boolean[] resultPotions = new boolean[3];
        final ItemStack[] storageContents = inventory.getStorageContents();
        for (int index = 0; index < 3; index++) {
            resultPotions[index] = (exclusions.length <= index || !exclusions[index])
                    && potion.matches(storageContents[index]);
        }
        return resultPotions;
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
