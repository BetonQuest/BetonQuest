package org.betonquest.betonquest.quest.objective.variable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates placeholders based on what player is typing, not just from event.
 */
public class ChatVariableObjective extends VariableObjective {

    /**
     * Pattern to match the chat variable format.
     */
    private static final Pattern CHAT_VARIABLE_PATTERN = Pattern.compile("^(?<key>[a-zA-Z]+): (?<value>.+)$");

    /**
     * Creates a new VariableObjective instance which also captures chat input.
     *
     * @param service the objective factory service
     * @throws QuestException if there is an error in the instruction
     */
    public ChatVariableObjective(final ObjectiveFactoryService service) throws QuestException {
        super(service);
    }

    /**
     * Handles chat input.
     *
     * @param event         the AsyncPlayerChatEvent
     * @param onlineProfile the profile of the player that typed the message
     */
    public void onChat(final AsyncPlayerChatEvent event, final OnlineProfile onlineProfile) {
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        final Matcher chatVariableMatcher = CHAT_VARIABLE_PATTERN.matcher(event.getMessage());
        if (chatVariableMatcher.matches()) {
            event.setCancelled(true);
            final String key = chatVariableMatcher.group("key").toLowerCase(Locale.ROOT);
            final String value = chatVariableMatcher.group("value");
            getVariableData(onlineProfile).add(key, value);
            event.getPlayer().sendMessage(Component.text("✓", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));
        }
    }
}
