package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.ChatColor;

/**
 * Simple chat-based conversation output.
 */
@SuppressWarnings("PMD.CommentRequired")
public class SimpleConvIO extends ChatConvIO {

    private final String optionFormat;

    public SimpleConvIO(final Conversation conv, final OnlineProfile onlineProfile) {
        super(conv, onlineProfile);
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.number()) {
            string.append(color);
        }
        string.append("%number%. ");
        for (final ChatColor color : colors.option()) {
            string.append(color);
        }
        optionFormat = string.toString();
    }

    @Override
    public void display() {
        super.display();
        for (int i = 1; i <= options.size(); i++) {
            conv.sendMessage(optionFormat.replace("%number%", Integer.toString(i)) + options.get(i));
        }
    }
}
