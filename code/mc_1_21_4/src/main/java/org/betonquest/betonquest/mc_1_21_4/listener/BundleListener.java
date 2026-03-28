package org.betonquest.betonquest.mc_1_21_4.listener;

import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

/**
 * Prevents interaction of Bundles and {@link QuestHandler#isQuestItem(ItemStack) QuestItems}.
 */
public class BundleListener implements Listener {

    /**
     * Bundle actions.
     */
    private static final Set<InventoryAction> BUNDLE_ACTIONS = EnumSet.of(
            InventoryAction.PICKUP_FROM_BUNDLE, InventoryAction.PICKUP_ALL_INTO_BUNDLE,
            InventoryAction.PICKUP_SOME_INTO_BUNDLE, InventoryAction.PLACE_FROM_BUNDLE,
            InventoryAction.PLACE_ALL_INTO_BUNDLE, InventoryAction.PLACE_SOME_INTO_BUNDLE);

    /**
     * The empty default constructor.
     */
    public BundleListener() {
    }

    /**
     * Prevents moving the Journal and Quest Items into Bundles.
     * <p>
     * Does not affect creative mode.
     *
     * @param event the inventory click event, attempting moving items
     */
    @EventHandler(ignoreCancelled = true)
    public void onBundleClick(final InventoryClickEvent event) {
        if (!BUNDLE_ACTIONS.contains(event.getAction())) {
            return;
        }
        if (!(event.getWhoClicked() instanceof final Player player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final ItemStack item = event.getCurrentItem();
        if (Journal.isJournal(item) || QuestHandler.isQuestItem(item)) {
            event.setCancelled(true);
            return;
        }
        final ItemStack cursor = event.getCursor();
        if (Journal.isJournal(cursor) || QuestHandler.isQuestItem(cursor)) {
            event.setCancelled(true);
        }
    }
}
