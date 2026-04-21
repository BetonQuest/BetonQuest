package org.betonquest.betonquest.listener;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Add QuestItem Drops if the entity is marked.
 */
public class CustomDropListener implements Listener {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The item manager.
     */
    private final ItemManager itemManager;

    /**
     * Identifier Factory.
     */
    private final IdentifierFactory<ItemIdentifier> identifierFactory;

    /**
     * Create a new custom drop Listener.
     *
     * @param log               the custom logger for exceptions
     * @param itemManager       the item manager
     * @param identifierFactory the identifier factory
     */
    public CustomDropListener(final BetonQuestLogger log, final ItemManager itemManager,
                              final IdentifierFactory<ItemIdentifier> identifierFactory) {
        this.identifierFactory = identifierFactory;
        this.log = log;
        this.itemManager = itemManager;
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
            final NamespacedKey key = new NamespacedKey("betonquest", "betonquest-drops-" + dropIndex);
            dataContainerValue = event.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (dataContainerValue != null) {
                final int separatorIndex = dataContainerValue.indexOf(':');
                try {
                    event.getDrops().add(itemManager.getItem(null, identifierFactory.parseIdentifier(null, dataContainerValue.substring(0, separatorIndex)))
                            .generate(Integer.parseInt(dataContainerValue.substring(separatorIndex + 1))));
                } catch (final QuestException e) {
                    log.warn("Error when dropping custom item from entity: " + e.getMessage(), e);
                }
            }
            dropIndex++;
        } while (dataContainerValue != null);
    }
}
