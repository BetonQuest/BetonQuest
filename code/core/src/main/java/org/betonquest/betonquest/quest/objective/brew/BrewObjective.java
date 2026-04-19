package org.betonquest.betonquest.quest.objective.brew;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.lib.profile.ProfileValueMap;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires the player to manually brew a potion.
 */
public class BrewObjective extends CountingObjective {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The potion item to brew.
     */
    private final Argument<ItemWrapper> potion;

    /**
     * A cache of brewing stands and their owners.
     */
    private final Map<Location, Profile> locations;

    /**
     * The target amount of potions to brew.
     *
     * @param service         the objective service
     * @param targetAmount    the target amount of potions to brew
     * @param plugin          the plugin instance
     * @param profileProvider the profile provider to get the profile of the player
     * @param potion          the potion item to brew
     * @throws QuestException if there is an error in the instruction
     */
    public BrewObjective(final ObjectiveService service, final Argument<Number> targetAmount, final Plugin plugin,
                         final ProfileProvider profileProvider, final Argument<ItemWrapper> potion) throws QuestException {
        super(service, targetAmount, "potions_to_brew");
        this.plugin = plugin;
        this.potion = potion;
        this.locations = new ProfileValueMap<>(profileProvider);
    }

    /**
     * Checks if the player put an ingredient into the brewing stand.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that put the ingredient
     */
    public void onIngredientPut(final InventoryClickEvent event, final OnlineProfile onlineProfile) {
        final Inventory topInventory = event.getView().getTopInventory();
        if (event.getRawSlot() < 0 || topInventory.getType() != InventoryType.BREWING) {
            return;
        }

        final ItemStack[] contentBefore = topInventory.getContents();
        new BukkitRunnable() {
            @Override
            public void run() {
                final ItemStack[] contentAfter = topInventory.getContents();
                if (itemsAdded(contentBefore, contentAfter)) {
                    final BrewingStand brewingStand = (BrewingStand) topInventory.getHolder();
                    if (brewingStand != null) {
                        locations.put(brewingStand.getLocation(), onlineProfile);
                    }
                }
            }
        }.runTask(plugin);
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
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onBrew(final BrewEvent event) throws QuestException {
        final Profile profile = locations.remove(event.getBlock().getLocation());
        if (profile == null || !getService().containsProfile(profile) || !getService().checkConditions(profile)) {
            return;
        }
        final QuestItem potion = this.potion.getValue(profile).getItem(profile);
        final List<ItemStack> results = event.getResults();
        final ItemStack[] currentContents = event.getContents().getStorageContents();

        int progress = 0;
        for (int index = 0; index < Math.min(results.size(), 3); index++) {
            if (!results.get(index).equals(currentContents[index])
                    && potion.matches(currentContents[index])) {
                progress++;
            }
        }

        if (progress > 0) {
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
    }
}
