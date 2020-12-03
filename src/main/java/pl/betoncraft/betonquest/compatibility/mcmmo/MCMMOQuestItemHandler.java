package pl.betoncraft.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Prevents affecting QuestItems with MCMMO skills
 * <p>
 * Created on 16.10.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MCMMOQuestItemHandler implements Listener {

    public MCMMOQuestItemHandler() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
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
