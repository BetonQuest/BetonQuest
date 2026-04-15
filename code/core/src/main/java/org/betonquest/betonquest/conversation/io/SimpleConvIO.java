package org.betonquest.betonquest.conversation.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.ChatConvIO;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.bukkit.plugin.Plugin;

/**
 * Simple chat-based conversation output.
 */
public class SimpleConvIO extends ChatConvIO {

    /**
     * Creates a new SimpleConvIO instance.
     *
     * @param log           the logger that will be used for logging
     * @param config        the plugin configuration accessor
     * @param plugin        the plugin instance
     * @param message       the plugin message instance
     * @param conv          the conversation this IO is part of
     * @param onlineProfile the online profile of the player participating in the conversation
     * @param colors        the colors used in the conversation
     */
    public SimpleConvIO(final BetonQuestLogger log, final ConfigAccessor config, final Plugin plugin,
                        final PluginMessage message, final Conversation conv, final OnlineProfile onlineProfile,
                        final ConversationColors colors) {
        super(log, config, plugin, message, conv, onlineProfile, colors);
    }

    @Override
    public void display() {
        super.display();
        for (int i = 1; i <= options.size(); i++) {
            conv.sendMessage(colors.getOption().append(colors.getNumber().append(Component.text(i))).append(options.get(i)));
        }
    }
}
