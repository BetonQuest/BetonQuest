package org.betonquest.betonquest.conversation.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * A conversation input/output that displays messages using the tellraw command with a delay between messages.
 * This allows for a more natural conversation flow, simulating a slower response time.
 */
public class SlowTellrawConvIO extends TellrawConvIO {
    /**
     * The delay in ticks between messages sent in the conversation.
     */
    private final int messageDelay;

    /**
     * Plugin instance to start tasks.
     */
    private final Plugin plugin;

    /**
     * The component line wrapper used for the conversation.
     */
    private final FixedComponentLineWrapper componentLineWrapper;

    /**
     * The list of lines to print in the conversation.
     */
    private final List<Component> linesToPrint = new ArrayList<>();

    /**
     * Whether the player can reply to the conversation, disabled while the NPC is talking, enabled when options are displayed.
     */
    private boolean canReply;

    /**
     * Creates a new SlowTellrawConvIO instance.
     *
     * @param plugin               the plugin to start tasks
     * @param conv                 the conversation this IO is part of
     * @param onlineProfile        the online profile of the player participating in the conversation
     * @param messageDelay         the delay in ticks between messages sent in the conversation
     * @param componentLineWrapper the component line wrapper used for formatting conversation messages
     * @param colors               the colors used in the conversation
     */
    public SlowTellrawConvIO(final Plugin plugin, final Conversation conv, final OnlineProfile onlineProfile,
                             final int messageDelay, final FixedComponentLineWrapper componentLineWrapper, final ConversationColors colors) {
        super(conv, onlineProfile, colors);
        this.plugin = plugin;
        this.componentLineWrapper = componentLineWrapper;
        this.messageDelay = messageDelay;
        this.canReply = false;
    }

    /**
     * If canReply is false, we ignore the event, otherwise handle it as normal.
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
        if (Component.empty().equals(npcText)) {
            if (options.isEmpty()) {
                end(() -> {
                });
                return;
            }
            throw new IllegalStateException("NPC text must be set before displaying options.");
        }
        canReply = false;

        linesToPrint.addAll(new ArrayList<>(componentLineWrapper.splitWidth(colors.getText()
                .append(colors.getNpc().append(npcName)).append(Component.text(": ")).append(npcText))));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (linesToPrint.isEmpty()) {
                    displayText();
                    canReply = true;
                    this.cancel();
                    return;
                }
                conv.sendMessage(linesToPrint.remove(0));
            }
        }.runTaskTimer(plugin, 0, messageDelay);
    }

    @Override
    public void end(final Runnable callback) {
        if (canReply) {
            super.end(callback);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (canReply) {
                        SlowTellrawConvIO.super.end(callback);
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 1, 1);
        }
    }
}
