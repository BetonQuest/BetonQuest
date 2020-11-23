package pl.betoncraft.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.*;

/**
 * Adds tellraw command handling to the SimpleConvIO
 */
public class TellrawConvIO extends ChatConvIO {

    protected Map<Integer, String> hashes;
    protected ChatColor color;
    protected boolean italic;
    protected boolean bold;
    protected boolean underline;
    protected boolean strikethrough;
    protected boolean magic;
    protected String number;
    private int count = 0;

    static {
        new UnknownCommandTellrawListener();
    }

    public TellrawConvIO(final Conversation conv, final String playerID) {
        super(conv, playerID);
        hashes = new HashMap<>();
        for (final ChatColor color : colors.get("option")) {
            if (color == ChatColor.STRIKETHROUGH) {
                strikethrough = true;
            } else if (color == ChatColor.MAGIC) {
                magic = true;
            } else if (color == ChatColor.ITALIC) {
                italic = true;
            } else if (color == ChatColor.BOLD) {
                bold = true;
            } else if (color == ChatColor.UNDERLINE) {
                underline = true;
            } else {
                this.color = color;
            }
        }
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.get("number")) {
            string.append(color);
        }
        string.append("%number%. ");
        number = string.toString();
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandAnswer(final PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        if (!event.getMessage().toLowerCase().startsWith("/betonquestanswer ")) {
            return;
        }
        event.setCancelled(true);
        final String[] parts = event.getMessage().split(" ");
        if (parts.length != 2) {
            return;
        }
        final String hash = parts[1];
        for (int j = 1; j <= hashes.size(); j++) {
            if (hashes.get(j).equals(hash)) {
                conv.sendMessage(answerFormat + options.get(j));
                conv.passPlayerAnswer(j);
                return;
            }
        }
    }

    @Override
    public void display() {
        super.display();
        for (int j = 1; j <= options.size(); j++) {
            // Build ColorString
            final TextComponent colorComponent = new TextComponent();
            colorComponent.setBold(bold);
            colorComponent.setStrikethrough(strikethrough);
            colorComponent.setObfuscated(magic);
            colorComponent.setColor(color.asBungee());
            final String colorString = colorComponent.toLegacyText();

            // We avoid ComponentBuilder as it's not available pre 1.9
            final List<BaseComponent> parts = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(number.replace("%number%", Integer.toString(j)))));
            parts.addAll(Arrays.asList(TextComponent.fromLegacyText(colorString + Utils.replaceReset(StringUtils.stripEnd(options.get(j), "\n"), colorString))));
            for (final BaseComponent component : parts) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/betonquestanswer " + hashes.get(j)));
            }

            conv.sendMessage(parts.toArray(new BaseComponent[0]));
        }
    }

    @Override
    public void addPlayerOption(final String option) {
        super.addPlayerOption(option);
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
            if (event.getMessage().toLowerCase().startsWith("/betonquestanswer ")) {
                event.setCancelled(true);
            }
        }
    }
}
