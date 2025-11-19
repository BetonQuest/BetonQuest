package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import com.google.common.collect.EvictingQueue;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.betonquest.betonquest.api.common.component.tagger.ComponentTagger;
import org.betonquest.betonquest.api.common.component.tagger.PrefixComponentTagger;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Function;

import static org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction.PACKET_WRAPPER_FUNCTION_MAP;

/**
 * Monitors chat packets and keeps a history of them for each player to be resent on demand.
 */
public class PacketChatHistory implements PacketListener, Listener, ChatHistory {
    /**
     * The tagger used to mark messages which should not be stored in the history.
     */
    private static final ComponentTagger TAGGER = new PrefixComponentTagger(" BetonQuest-Message-History-Bypass-Tag ");

    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * The size of the cache.
     */
    private final int cacheSize;

    /**
     * A map storing the chat history for each player by their UUID.
     */
    private final Map<UUID, Queue<PacketWrapper<?>>> history;

    /**
     * Function to create chat message packets based on server version.
     */
    private final Function<Component, PacketWrapper<?>> messageFunction;

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
        // TODO version switch:
        //  Remove this code when only 1.19.0+ is supported
        this.messageFunction = PaperLib.isVersion(19)
                ? message -> new WrapperPlayServerSystemChatMessage(false, message)
                : message -> new WrapperPlayServerChatMessage(new ChatMessage_v1_16(message,
                ChatTypes.CHAT, new UUID(0L, 0L)));
    }

    private Queue<PacketWrapper<?>> getHistory(final UUID uuid) {
        return history.computeIfAbsent(uuid, k -> EvictingQueue.create(cacheSize));
    }

    @Override
    public void sendHistory(final Player player) {
        final User user = packetEventsAPI.getPlayerManager().getUser(player);
        final Queue<PacketWrapper<?>> history = getHistory(player.getUniqueId());
        final TextComponent message = Component.text(Component.newline().content().repeat(cacheSize - history.size()));
        user.sendPacketSilently(messageFunction.apply(message));
        history.forEach(user::sendPacketSilently);
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
        getHistory(((Player) event.getPlayer()).getUniqueId()).add(packetWrapperFunction.transform(packetWrapper));
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
    public ComponentTagger getTagger() {
        return TAGGER;
    }
}
