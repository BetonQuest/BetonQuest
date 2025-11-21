package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Moves the {@link org.betonquest.betonquest.menu.MenuItem} into its own section.
 */
public class MoveMenuItems implements QuestMigration {

    /**
     * Empty default constructor.
     */
    public MoveMenuItems() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        final ConfigurationSection section = config.getConfigurationSection("menus");
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            final ConfigurationSection items = section.getConfigurationSection(key + ".items");
            if (items == null) {
                continue;
            }
            migrateMenu(key, items, section, config);
        }
    }

    private void migrateMenu(final String key, final ConfigurationSection items, final ConfigurationSection section,
                             final MultiConfiguration config) throws InvalidConfigurationException {
        for (final String itemsKey : items.getKeys(false)) {
            final String itemPath = "menu_items." + key + "_" + itemsKey;
            final ConfigurationSection item = items.getConfigurationSection(itemsKey);
            config.set(itemPath, item);
            final ConfigurationSection source = config.getSourceConfigurationSection(item.getCurrentPath());
            if (source == null) {
                throw new InvalidConfigurationException("Cannot migrate Menu Item when it is in multiple files defined!");
            }
            config.associateWith(itemPath, source);
        }
        section.set(key + ".items", null);
        final ConfigurationSection slots = section.getConfigurationSection(key + ".slots");
        if (slots == null) {
            return;
        }
        for (final String slotsKey : slots.getKeys(false)) {
            final String[] slot = slots.getString(slotsKey).split(",");
            final String[] newSlot = new String[slot.length];
            for (int i = 0; i < slot.length; i++) {
                newSlot[i] = key + "_" + slot[i];
            }
            slots.set(slotsKey, String.join(",", newSlot));
        }
    }
}
