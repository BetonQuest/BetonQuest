package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Menu conversation output.
 */
public class MenuConvIOFactory implements ConversationIOFactory {
    /**
     * The config accessor to the plugin's configuration.
     */
    private final ConfigAccessor config;

    /**
     * Create a new Menu conversation IO factory.
     *
     * @param config the config accessor to the plugin's configuration
     */
    public MenuConvIOFactory(final ConfigAccessor config) {
        this.config = config;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final MenuConvIOSettings settings = MenuConvIOSettings.fromConfigurationSection(config.getConfigurationSection("conversation.io.menu"));
        return new MenuConvIO(conversation, onlineProfile, settings);
    }
}
