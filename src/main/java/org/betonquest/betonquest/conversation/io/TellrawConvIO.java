package org.betonquest.betonquest.conversation.io;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.ChatConvIO;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Adds tellraw command handling to the SimpleConvIO
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition"})
public class TellrawConvIO extends ChatConvIO {
    static {
        new UnknownCommandTellrawListener();
    }

    protected Map<Integer, String> hashes;

    private int count;

    @SuppressWarnings("NullAway.Init")
    public TellrawConvIO(final Conversation conv, final OnlineProfile onlineProfile, final ConversationColors colors) {
        super(conv, onlineProfile, colors);
        hashes = new HashMap<>();
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandAnswer(final PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(onlineProfile.getPlayer())) {
            return;
        }
        if (!event.getMessage().toLowerCase(Locale.ROOT).startsWith("/betonquestanswer ")) {
            return;
        }
        event.setCancelled(true);
        final String[] parts = event.getMessage().split(" ");
        if (parts.length != 2) {
            return;
        }
        final String hash = parts[1];
        for (int j = 1; j <= hashes.size(); j++) {
            if (hash.equals(hashes.get(j))) {
                conv.sendMessage(colors.getAnswer().append(colors.getPlayer().append(Component.text(onlineProfile.getPlayer().getName())))
                        .append(Component.text(": ")).append(options.get(j)));
                conv.passPlayerAnswer(j);
                return;
            }
        }
    }

    @Override
    public void display() {
        super.display();
        displayText();
    }

    protected void displayText() {
        for (int j = 1; j <= options.size(); j++) {
            final TextComponent message = Component.empty().clickEvent(ClickEvent.runCommand("/betonquestanswer " + hashes.get(j)))
                    .append(colors.getOption().append(colors.getNumber().append(Component.text(j)).append(Component.text(". ")))
                            .append(options.get(j)));

            conv.sendMessage(message);
        }
    }

    @Override
    public void addPlayerOption(final Component option, final ConfigurationSection properties) {
        super.addPlayerOption(option, properties);
        count++;
        hashes.put(count, UUID.randomUUID().toString());
    }

    @Override
    public void clear() {
        super.clear();
        hashes.clear();
        count = 0;
    }

    public static class UnknownCommandTellrawListener implements Listener {

        public UnknownCommandTellrawListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler(priority = EventPriority.HIGH)
        public void onCommand(final PlayerCommandPreprocessEvent event) {
            if (event.getMessage().toLowerCase(Locale.ROOT).startsWith("/betonquestanswer ")) {
                event.setCancelled(true);
            }
        }
    }
}
