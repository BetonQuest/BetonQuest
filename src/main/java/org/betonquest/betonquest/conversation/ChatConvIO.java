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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Nullable
    protected Component npcText;

    protected Component npcName;

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
        for (final Map.Entry<Integer, Component> entry : options.entrySet()) {
            final int index = entry.getKey();
            if (message.equals(Integer.toString(index))) {
                conv.sendMessage(colors.getAnswer().append(colors.getPlayer().append(Component.text(onlineProfile.getPlayer().getName())))
                        .append(Component.text(": ")).append(entry.getValue()));
                conv.passPlayerAnswer(index);
                event.setCancelled(true);
                return;
            }
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
        if (npcText == null && options.isEmpty()) {
            end();
            return;
        }
        Objects.requireNonNull(npcText);

        conv.sendMessage(colors.getText().append(colors.getNpc().append(npcName)).append(Component.text(": ")).append(npcText));
    }

    @Override
    public void clear() {
        optionsCount = 0;
        options.clear();
        npcText = null;
    }

    @Override
    public void end() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void print(@Nullable final Component message) {
        if (message != null && !Component.empty().equals(message)) {
            conv.sendMessage(message);
        }
    }
}
