package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Base of all chat conversation outputs.
 */
@SuppressWarnings("PMD.CommentRequired")
public abstract class ChatConvIO implements ConversationIO, Listener {
    protected final Conversation conv;

    protected final OnlineProfile onlineProfile;

    protected final ConversationColors colors;

    private final double maxNpcDistance;

    private final BetonQuestLogger log;

    protected int optionsCount;

    protected Map<Integer, Component> options;

    protected Component npcText;

    protected Component npcName;

    /**
     * Creates a new ChatConvIO instance.
     *
     * @param conv          the conversation this IO is part of
     * @param onlineProfile the online profile of the player participating in the conversation
     * @param colors        the colors used in the conversation
     */
    @SuppressWarnings("NullAway.Init")
    public ChatConvIO(final Conversation conv, final OnlineProfile onlineProfile, final ConversationColors colors) {
        this.log = BetonQuest.getInstance().getLoggerFactory().create(ChatConvIO.class);
        this.options = new HashMap<>();
        this.conv = conv;
        this.onlineProfile = onlineProfile;
        this.colors = colors;

        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        maxNpcDistance = BetonQuest.getInstance().getPluginConfig().getDouble("conversation.stop.distance");
    }

    @EventHandler(ignoreCancelled = true)
    public void onWalkAway(final PlayerMoveEvent event) {
        if (!event.getPlayer().equals(onlineProfile.getPlayer())) {
            return;
        }
        if (!event.getTo().getWorld().equals(conv.getCenter().getWorld()) || event.getTo()
                .distance(conv.getCenter()) > maxNpcDistance) {
            if (conv.isMovementBlock() || !conv.state.isStarted()) {
                moveBack(event);
            } else {
                conv.endConversation();
            }
        }
    }

    /**
     * Moves the player back a few blocks in the conversation's center
     * direction.
     *
     * @param event PlayerMoveEvent event, for extracting the necessary data
     */
    private void moveBack(final PlayerMoveEvent event) {
        if (!event.getTo().getWorld().equals(conv.getCenter().getWorld()) || event.getTo()
                .distance(conv.getCenter()) > maxNpcDistance * 2) {
            event.getPlayer().teleport(conv.getCenter());
            return;
        }
        final float yaw = event.getTo().getYaw();
        final float pitch = event.getTo().getPitch();
        Vector vector = new Vector(conv.getCenter().getX() - event.getTo().getX(),
                conv.getCenter().getY() - event.getTo().getY(), conv.getCenter().getZ() - event.getTo().getZ());
        vector = vector.multiply(1 / vector.length());
        final Location newLocation = event.getTo().clone();
        newLocation.add(vector);
        newLocation.setPitch(pitch);
        newLocation.setYaw(yaw);
        event.getPlayer().teleport(newLocation);
        if (BetonQuest.getInstance().getPluginConfig().getBoolean("conversation.stop.notify")) {
            try {
                conv.sendMessage(BetonQuest.getInstance().getPluginMessage().getMessage(onlineProfile, "pullback"));
            } catch (final QuestException e) {
                log.warn("Failed to get pullback message: " + e.getMessage(), e);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onReply(final AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(onlineProfile.getPlayer())) {
            return;
        }
        final String message = event.getMessage().trim();
        try {
            final int answerIndex = Integer.parseInt(message);
            final Component answer = options.get(answerIndex);
            if (answer != null) {
                conv.sendMessage(colors.getAnswer().append(colors.getPlayer().append(Component.text(onlineProfile.getPlayer().getName())))
                        .append(Component.text(": ")).append(answer));
                conv.passPlayerAnswer(answerIndex);
                event.setCancelled(true);
                return;
            }
            log.debug("Invalid answer from player: " + message);
        } catch (final NumberFormatException e) {
            log.debug("Invalid answer from player: " + message, e);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                display();
            }
        }.runTask(BetonQuest.getInstance());
    }

    @Override
    public void setNpcResponse(final Component npcName, final Component response) {
        this.npcName = npcName;
        this.npcText = response;
    }

    @Override
    public void addPlayerOption(final Component option, final ConfigurationSection properties) {
        optionsCount++;
        options.put(optionsCount, option);
    }

    @Override
    public void display() {
        if (Component.empty().equals(npcText) && options.isEmpty()) {
            end(() -> {
            });
            return;
        }

        conv.sendMessage(colors.getText().append(colors.getNpc().append(npcName)).append(Component.text(": ")).append(npcText));
    }

    @Override
    public void clear() {
        optionsCount = 0;
        options.clear();
        npcText = Component.empty();
    }

    @Override
    public void end(final Runnable callback) {
        HandlerList.unregisterAll(this);
        callback.run();
    }
}
