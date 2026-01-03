package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Changes list keys from singular to plural.
 */
public class ListNamesRenameToPlural implements QuestMigration {

    /**
     * Creates a new List names to plural migration.
     */
    public ListNamesRenameToPlural() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        replace(config, "objectives", value -> true, value -> value
                .replace(" event:", " events:")
                .replace(" condition:", " conditions:"));
        replace(config, "events", value -> true, value -> value
                .replace(" condition:", " conditions:"));
        final ConfigurationSection menus = config.getConfigurationSection("menus");
        if (menus != null) {
            replaceMenuConditions(config, menus);
        }
        final ConfigurationSection conversations = config.getConfigurationSection("conversations");
        if (conversations != null) {
            replaceConversationKeys(config, conversations);
        }
    }

    private void replaceMenuConditions(final MultiConfiguration root, final ConfigurationSection menus)
            throws InvalidConfigurationException {
        for (final String menuKey : menus.getKeys(false)) {
            final ConfigurationSection items = menus.getConfigurationSection(menuKey + ".items");
            if (items == null) {
                continue;
            }
            for (final String itemKey : items.getKeys(false)) {
                final ConfigurationSection item = items.getConfigurationSection(itemKey);
                if (item != null) {
                    replaceKeyInSection(root, item, "condition", "conditions");
                }
            }
        }
    }

    private void replaceConversationKeys(final MultiConfiguration root, final ConfigurationSection conversations)
            throws InvalidConfigurationException {
        for (final String conversationKey : conversations.getKeys(false)) {
            final ConfigurationSection conversation = conversations.getConfigurationSection(conversationKey);
            if (conversation == null) {
                continue;
            }
            replaceInOption(root, conversation, "NPC_options");
            replaceInOption(root, conversation, "player_options");
        }
    }

    private void replaceInOption(final MultiConfiguration root, final ConfigurationSection conversation,
                                 final String optionName) throws InvalidConfigurationException {
        final ConfigurationSection options = conversation.getConfigurationSection(optionName);
        if (options == null) {
            return;
        }
        for (final String optionKey : options.getKeys(false)) {
            final ConfigurationSection option = options.getConfigurationSection(optionKey);
            if (option == null) {
                continue;
            }
            replaceKeyInSection(root, option, "condition", "conditions");
            replaceKeyInSection(root, option, "event", "events");
            replaceKeyInSection(root, option, "pointer", "pointers");
            replaceKeyInSection(root, option, "extend", "extends");
        }
    }
}
