package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.util.LocalChatPaginator;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class SlowTellrawConvIO extends TellrawConvIO {
    private final String npcTextColor;

    private final int messageDelay;

    @Nullable
    private List<String> endLines;

    /**
     * Whether the player can reply to the conversation, disabled while the NPC is talking, enabled when options are displayed
     */
    private boolean canReply;

    public SlowTellrawConvIO(final Conversation conv, final OnlineProfile onlineProfile) {
        super(conv, onlineProfile);
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.text()) {
            string.append(color);
        }
        this.npcTextColor = string.toString();
        int delay = BetonQuest.getInstance().getPluginConfig().getInt("conversation_IO_config.slowtellraw.message_delay", 10);
        if (delay <= 0) {
            BetonQuest.getInstance().getLogger().warning("Invalid message delay of " + delay + " for SlowTellraw Conversation IO, using default value of 10 ticks");
            delay = 10;
        }
        this.messageDelay = delay;
        this.canReply = false;
    }

    /**
     * if canReply is false, we ignore the event, otherwise handle it as normal
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    @Override
    public void onReply(final AsyncPlayerChatEvent event) {
        if (!canReply) {
            return;
        }
        super.onReply(event);
    }

    @Override
    public void display() {
        if (npcText == null && options.isEmpty()) {
            end();
            return;
        }
        canReply = false;

        // NPC Text
        final String[] lines = LocalChatPaginator.wordWrap(
                Utils.replaceReset(textFormat.replace("%npc%", npcName) + npcText, npcTextColor), 50);
        endLines = new ArrayList<>();

        new BukkitRunnable() {
            private int lineCount;

            @SuppressWarnings("NullAway")
            @Override
            public void run() {
                if (lineCount == lines.length) {
                    displayText();

                    // Display endLines
                    for (final String message : endLines) {
                        SlowTellrawConvIO.super.print(message);
                    }

                    endLines = null;
                    canReply = true;

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
