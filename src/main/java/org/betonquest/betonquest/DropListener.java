package org.betonquest.betonquest;

import lombok.CustomLog;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class DropListener implements Listener {

    public DropListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        int dropIndex = 0;
        while (event.getEntity().hasMetadata("betonquest-drops-" + dropIndex)) {
            try {
                final List<MetadataValue> metadata = event.getEntity().getMetadata("betonquest-drops-" + dropIndex);
                for (final MetadataValue m : metadata) {
                    final String item = m.asString();
                    final int separatorIndex = item.indexOf(':');
                    event.getDrops().add(new QuestItem(new ItemID(null, item.substring(0, separatorIndex)))
                            .generate(Integer.parseInt(item.substring(separatorIndex + 1))));
                }
            } catch (InstructionParseException | ObjectNotFoundException e) {
                LOG.warn("Error when dropping custom item from entity: " + e.getMessage(), e);
            }
            dropIndex++;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onJournalDrop(final PlayerDropItemEvent event) {
        if("true".equalsIgnoreCase(Config.getString("config.journal.drop"))){
            return;
        }

        if(!Journal.isJournal(PlayerConverter.getID(event.getPlayer()), event.getItemDrop().getItemStack())){
            return;
        }

        event.setCancelled(true);
    }

}
