package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.util.Utils;
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
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Constructs a new MCMMOQuestItemHandler.
     *
     * @param profileProvider the profile provider to use for accessing player profiles
     */
    public MCMMOQuestItemHandler(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    /**
     * Handles the McMMOPlayerSalvageCheckEvent to prevent salvaging QuestItems.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuestItemSalvaging(final McMMOPlayerSalvageCheckEvent event) {
        if (Utils.isQuestItem(event.getSalvageItem())) {
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
        if (Utils.isQuestItem(itemInMainHand) || Journal.isJournal(profileProvider.getProfile(event.getPlayer()), itemInMainHand)) {
            event.setCancelled(true);
        }
    }
}
