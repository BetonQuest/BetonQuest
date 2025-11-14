package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.google.common.collect.EvictingQueue;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.ComponentTagger;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import static org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction.PACKET_WRAPPER_FUNCTION_MAP;

/**
 * Monitors chat packets and keeps a history of them for each player to be resent on demand.
 */
public class PacketChatHistory implements PacketListener, Listener, ChatHistory {
    /**
     * A prefix that marks messages to be ignored by this history.
     */
    private static final ComponentTagger TAGGER = new ComponentTagger("BetonQuest-Message-History-Bypass-Tag");

    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * The size of the cache for each player, this is usually the number of messages that can be seen in chat.
     */
    private final int cacheSize;

    /**
     * A map storing the chat history for each player by their UUID.
     */
    private final Map<UUID, Queue<PacketWrapper<?>>> history;

    /**
     * Constructs a ChatHistory instance.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param cacheSize       the size of the chat history cache per player,
     *                        this is usually the number of messages that can be seen in chat
     */
    public PacketChatHistory(final PacketEventsAPI<?> packetEventsAPI, final int cacheSize) {
        this.packetEventsAPI = packetEventsAPI;
        this.cacheSize = cacheSize;
        this.history = new HashMap<>();
    }

    private Queue<PacketWrapper<?>> getHistory(final UUID uuid) {
        return history.computeIfAbsent(uuid, k -> EvictingQueue.create(cacheSize));
    }

    @Override
    public void sendHistory(final Player player) {
        final User user = packetEventsAPI.getPlayerManager().getUser(player);
        final Queue<PacketWrapper<?>> history = getHistory(player.getUniqueId());
        user.sendMessage(addBypass(Component.text(Component.newline().content().repeat(cacheSize - history.size()))));
        for (final PacketWrapper<?> packetWrapper : history) {
            user.sendPacket(packetWrapper);
        }
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final PacketWrapperFunction<?> packetWrapperFunction = PACKET_WRAPPER_FUNCTION_MAP.get(event.getPacketType());
        if (packetWrapperFunction == null) {
            return;
        }
        handlePacketWrapperFunction(packetWrapperFunction, event);
    }

    private <T extends PacketWrapper<?>> void handlePacketWrapperFunction(
            final PacketWrapperFunction<T> packetWrapperFunction, final PacketSendEvent event) {
        final T packetWrapper = packetWrapperFunction.getPacketWrapper(event);
        if (TAGGER.acceptIfTagged(packetWrapperFunction.getMessage(packetWrapper),
                untagged -> packetWrapperFunction.setMessage(packetWrapper, untagged))) {
            return;
        }
        getHistory(((Player) event.getPlayer()).getUniqueId()).add(packetWrapperFunction.transform(packetWrapper, this::addBypass));
    }

    /**
     * Cleans up the chat history when a player quits.
     *
     * @param event the {@link PlayerQuitEvent}
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        history.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public Component addBypass(final Component component) {
        return TAGGER.tag(component);
    }
}
