package org.betonquest.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.utils.LocalChatPaginator;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class SlowTellrawConvIO extends TellrawConvIO {
    private final String npcTextColor;

    private final int messageDelay;

    @Nullable
    private List<String> endLines;

    public SlowTellrawConvIO(final Conversation conv, final OnlineProfile onlineProfile) {
        super(conv, onlineProfile);
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.get("text")) {
            string.append(color);
        }
        this.npcTextColor = string.toString();
        int delay = BetonQuest.getInstance().getPluginConfig().getInt("conversation_IO_config.slowtellraw.message_delay", 10);
        if (delay <= 0) {
            BetonQuest.getInstance().getLogger().warning("Invalid message delay of " + delay + " for SlowTellraw Conversation IO, using default value of 10 ticks");
            delay = 10;
        }
        this.messageDelay = delay;
    }

    @Override
    public void display() {
        if (npcText == null && options.isEmpty()) {
            end();
            return;
        }

        // NPC Text
        final String[] lines = LocalChatPaginator.wordWrap(
                Utils.replaceReset(textFormat.replace("%npc%", npcName) + npcText, npcTextColor), 50);
        endLines = new ArrayList<>();

        new BukkitRunnable() {
            private int lineCount;

            @Override
            @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
            public void run() {
                if (lineCount == lines.length) {
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

                    // Display endLines
                    for (final String message : endLines) {
                        SlowTellrawConvIO.super.print(message);
                    }

                    endLines = null;

                    this.cancel();
                    return;
                }
                conv.sendMessage(lines[lineCount++]);
            }
        }.runTaskTimer(BetonQuest.getInstance(), 0, messageDelay);
    }

    @Override
    public void print(@Nullable final String message) {
        if (endLines == null) {
            super.print(message);
            return;
        }

        // If endLines is defined, we add to it to be outputted after we have outputted our previous text
        endLines.add(message);
    }
}
