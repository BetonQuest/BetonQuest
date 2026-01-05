package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Renames the "events" to "actions" to prevent name conflict with actual event system.
 */
public class EventsToActionsRename implements QuestMigration {

    /**
     * The 'actions' string.
     */
    private static final String ACTIONS = "actions";

    /**
     * Empty default constructor.
     */
    public EventsToActionsRename() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        renameSection(config, "events", ACTIONS);
        replaceConversations(config);

        replaceKeyInSections(config, "menus", "open_events", "open_actions");
        replaceKeyInSections(config, "menus", "close_events", "close_actions");
        replaceEventsKeysInSection(config, "cancel");
        replaceEventsKeysInSection(config, "schedules");

        replace(config, "objectives", value -> true, value -> value
                .replace(" events:", " actions:"));
        replaceValueInSection(config, "objectives", "command", " failEvents:", " failActions:");
        replaceValueInSection(config, ACTIONS, "runForAll", " events:", " actions:");
        replaceValueInSection(config, ACTIONS, "runIndependent", " events:", " actions:");
    }

    private void replaceConversations(final MultiConfiguration root) throws InvalidConfigurationException {
        final ConfigurationSection conversations = root.getConfigurationSection("conversations");
        if (conversations == null) {
            return;
        }
        replaceKeyInSection(root, conversations, "final_events", "final_actions");
        for (final String key : conversations.getKeys(false)) {
            final ConfigurationSection conversation = conversations.getConfigurationSection(key);
            if (conversation == null) {
                continue;
            }
            replaceEventsKeysInSection(root, "conversations." + key + ".NPC_options");
            replaceEventsKeysInSection(root, "conversations." + key + ".player_options");
        }
    }

    private void replaceEventsKeysInSection(final MultiConfiguration root, final String path) throws InvalidConfigurationException {
        replaceKeyInSections(root, path, "events", ACTIONS);
    }
}
