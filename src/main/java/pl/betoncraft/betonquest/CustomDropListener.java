package pl.betoncraft.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.List;
import java.util.logging.Level;

public class CustomDropListener implements Listener {

    public CustomDropListener() {
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
                LogUtils.getLogger().log(Level.WARNING, "Error when dropping custom item from entity: " + e.getMessage());
                LogUtils.logThrowable(e);
            }
            dropIndex++;
        }
    }

}
