package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Prevents affecting QuestItems with MCMMO skills
 * <p>
 * Created on 16.10.2018.
 */
public class MCMMOQuestItemHandler implements Listener {

    /**
     * The empty default constructor.
     */
    public MCMMOQuestItemHandler() {
    }

    /**
     * Handles the McMMOPlayerSalvageCheckEvent to prevent salvaging QuestItems.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuestItemSalvaging(final McMMOPlayerSalvageCheckEvent event) {
        if (QuestHandler.isQuestItem(event.getSalvageItem())) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles the McMMOPlayerDisarmEvent to prevent disarming QuestItems.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuestItemDisarm(final McMMOPlayerDisarmEvent event) {
        final ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (QuestHandler.isQuestItem(itemInMainHand) || Journal.isJournal(itemInMainHand)) {
            event.setCancelled(true);
        }
    }
}
