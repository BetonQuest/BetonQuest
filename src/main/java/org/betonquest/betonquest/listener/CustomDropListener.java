package org.betonquest.betonquest.listener;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Add QuestItem Drops if the entity is marked.
 */
public class CustomDropListener implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Plugin used as namespace.
     */
    private final Plugin plugin;

    /**
     * Create a new custom drop Listener.
     *
     * @param log    the custom logger for exceptions
     * @param plugin the plugin used as namespace
     */
    public CustomDropListener(final BetonQuestLogger log, final Plugin plugin) {
        this.log = log;
        this.plugin = plugin;
    }

    /**
     * Add QuestItem Drops if the entity is marked.
     *
     * @param event the entity death event
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        int dropIndex = 0;
        String dataContainerValue;
        do {
            final NamespacedKey key = new NamespacedKey(plugin, "betonquest-drops-" + dropIndex);
            dataContainerValue = event.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (dataContainerValue != null) {
                final int separatorIndex = dataContainerValue.indexOf(':');
                try {
                    event.getDrops().add(new QuestItem(new ItemID(null, dataContainerValue.substring(0, separatorIndex)))
                            .generate(Integer.parseInt(dataContainerValue.substring(separatorIndex + 1))));
                } catch (final QuestException e) {
                    log.warn("Error when dropping custom item from entity: " + e.getMessage(), e);
                }
            }
            dropIndex++;
        } while (dataContainerValue != null);
    }
}
