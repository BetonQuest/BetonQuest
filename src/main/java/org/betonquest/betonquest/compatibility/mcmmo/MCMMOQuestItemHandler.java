package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.util.PlayerConverter;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Prevents affecting QuestItems with MCMMO skills
 * <p>
 * Created on 16.10.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MCMMOQuestItemHandler implements Listener {

    public MCMMOQuestItemHandler() {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuestItemSalvaging(final McMMOPlayerSalvageCheckEvent event) {
        if (Utils.isQuestItem(event.getSalvageItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuestItemDisarm(final McMMOPlayerDisarmEvent event) {
        if (Utils.isQuestItem(event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        } else if (Journal.isJournal(PlayerConverter.getID(event.getPlayer()), event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }
    }
}
