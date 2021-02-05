package org.betonquest.betonquest.conversation;

import org.bukkit.ChatColor;

/**
 * Simple chat-based conversation output.
 */
@SuppressWarnings("PMD.CommentRequired")
public class SimpleConvIO extends ChatConvIO {

    private final String optionFormat;

    public SimpleConvIO(final Conversation conv, final String playerID) {
        super(conv, playerID);
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.get("number")) {
            string.append(color);
        }
        string.append("%number%. ");
        for (final ChatColor color : colors.get("option")) {
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
