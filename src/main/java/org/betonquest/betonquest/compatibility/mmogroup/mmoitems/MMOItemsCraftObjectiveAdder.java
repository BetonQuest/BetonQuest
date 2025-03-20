package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.crafting.event.MythicCraftItemEvent;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.quest.objective.crafting.CraftingObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Allows a {@link CraftingObjective} to also process crafting items using MMOItems.
 */
public class MMOItemsCraftObjectiveAdder implements Listener {

    /**
     * Profile Provider to get profiles from Players.
     */
    private final ProfileProvider profileProvider;

    /**
     * Quest Type API to get active Objectives.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Constructs a new {@link MMOItemsCraftObjectiveAdder}.
     *
     * @param profileProvider the profile provider to use
     * @param questTypeAPI    the Quest Type API to get active {@link CraftingObjective}s
     */
    public MMOItemsCraftObjectiveAdder(final ProfileProvider profileProvider, final QuestTypeAPI questTypeAPI) {
        this.profileProvider = profileProvider;
        this.questTypeAPI = questTypeAPI;
    }

    /**
     * This event is called by MMOItems "recipe-amounts" crafting system.
     *
     * @param event the event to process
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final MythicCraftItemEvent event) {
        final ItemStack craftedItem = event.getCache().getResultOfOperation().getResultInventory().getFirst();
        if (craftedItem != null) {
            progressCraftObjective((Player) event.getTrigger().getWhoClicked(), craftedItem);
        }
    }

    /**
     * This listener handles items that were crafted in a MMOItems Craftingstation GUI.
     *
     * @param event the event to process
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final PlayerUseCraftingStationEvent event) {
        final PlayerUseCraftingStationEvent.StationAction action = event.getInteraction();
        if (action != PlayerUseCraftingStationEvent.StationAction.INTERACT_WITH_RECIPE
                && action != PlayerUseCraftingStationEvent.StationAction.CANCEL_QUEUE
                && event.getRecipe() instanceof CraftingRecipe) {
            progressCraftObjective(event.getPlayer(), event.getResult());
        }
    }

    private void progressCraftObjective(final Player player, final ItemStack craftedItem) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        for (final Objective objective : questTypeAPI.getPlayerObjectives(onlineProfile)) {
            if (objective instanceof CraftingObjective craftingObjective) {
                craftingObjective.handleCustomCraft(onlineProfile, craftedItem, craftedItem.getAmount());
            }
        }
    }
}
