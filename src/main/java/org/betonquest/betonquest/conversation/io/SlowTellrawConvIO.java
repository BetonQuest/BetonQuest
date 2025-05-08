package org.betonquest.betonquest.conversation.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("PMD.CommentRequired")
public class SlowTellrawConvIO extends TellrawConvIO {
    private final int messageDelay;

    /**
     * The component line wrapper used for the conversation.
     */
    private final ComponentLineWrapper componentLineWrapper;

    @Nullable
    private List<Component> endLines;

    /**
     * Whether the player can reply to the conversation, disabled while the NPC is talking, enabled when options are displayed
     */
    private boolean canReply;

    public SlowTellrawConvIO(final Conversation conv, final OnlineProfile onlineProfile,
                             final ComponentLineWrapper componentLineWrapper, final ConversationColors colors) {
        super(conv, onlineProfile, colors);
        this.componentLineWrapper = componentLineWrapper;
        int delay = BetonQuest.getInstance().getPluginConfig().getInt("conversation.io.slowtellraw.message_delay", 10);
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
        Objects.requireNonNull(npcText);

        final List<Component> lines = componentLineWrapper.splitWidth(colors.getText().append(colors.getNpc().append(npcName)).append(Component.text(": ")));
        endLines = new ArrayList<>();

        new BukkitRunnable() {
            private int lineCount;

            @SuppressWarnings("NullAway")
            @Override
            public void run() {
                if (lineCount == lines.size()) {
                    displayText();

                    // Display endLines
                    for (final Component message : endLines) {
                        SlowTellrawConvIO.super.print(message);
                    }

                    endLines = null;
                    canReply = true;

                    this.cancel();
                    return;
                }
                conv.sendMessage(lines.get(lineCount++));
            }
        }.runTaskTimer(BetonQuest.getInstance(), 0, messageDelay);
    }

    @Override
    public void print(@Nullable final Component message) {
        if (endLines == null) {
            super.print(message);
            return;
        }

        // If endLines is defined, we add to it to be outputted after we have outputted our previous text
        endLines.add(message);
    }
}
