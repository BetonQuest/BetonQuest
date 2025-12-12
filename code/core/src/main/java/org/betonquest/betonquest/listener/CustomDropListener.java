package org.betonquest.betonquest.listener;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.ItemID;
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
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Plugin used as namespace.
     */
    private final Plugin plugin;

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Create a new custom drop Listener.
     *
     * @param log         the custom logger for exceptions
     * @param packManager the quest package manager to get quest packages from
     * @param plugin      the plugin used as namespace
     * @param featureApi  the Feature API
     */
    public CustomDropListener(final BetonQuestLogger log, final QuestPackageManager packManager, final Plugin plugin,
                              final FeatureApi featureApi) {
        this.log = log;
        this.packManager = packManager;
        this.plugin = plugin;
        this.featureApi = featureApi;
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
                    event.getDrops().add(featureApi.getItem(new ItemID(packManager, null, dataContainerValue.substring(0, separatorIndex)), null)
                            .generate(Integer.parseInt(dataContainerValue.substring(separatorIndex + 1))));
                } catch (final QuestException e) {
                    log.warn("Error when dropping custom item from entity: " + e.getMessage(), e);
                }
            }
            dropIndex++;
        } while (dataContainerValue != null);
    }
}
