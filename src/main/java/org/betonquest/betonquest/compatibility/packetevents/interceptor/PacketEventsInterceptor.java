package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.tagger.ComponentTagger;
import org.betonquest.betonquest.api.common.component.tagger.PrefixComponentTagger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.ChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction.PACKET_WRAPPER_FUNCTION_MAP;

/**
 * An interceptor that captures chat packets sent to a player, allowing for message interception and history management,
 * based on PacketEvents.
 */
public class PacketEventsInterceptor implements Interceptor, PacketListener {

    /**
     * The tagger used to mark messages which should not be intercepted.
     */
    private static final ComponentTagger TAGGER = new PrefixComponentTagger(" BetonQuest-Interceptor-Bypass-Tag ");

    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * The chat history manager.
     */
    private final ChatHistory chatHistory;

    /**
     * The online profile of the player.
     */
    private final OnlineProfile onlineProfile;

    /**
     * The list of intercepted messages.
     */
    private final Queue<PacketWrapper<?>> messages;

    /**
     * Indicates whether the interception has ended.
     */
    private final AtomicBoolean ended;

    /**
     * The registered active packet listener instance.
     */
    @Nullable
    private PacketListenerCommon packetListenerCommon;

    /**
     * Constructs a PacketEventsInterceptor.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param chatHistory     the chat history manager
     * @param onlineProfile   the online profile of the player
     */
    public PacketEventsInterceptor(final PacketEventsAPI<?> packetEventsAPI, final ChatHistory chatHistory, final OnlineProfile onlineProfile) {
        this.packetEventsAPI = packetEventsAPI;
        this.chatHistory = chatHistory;
        this.onlineProfile = onlineProfile;
        this.messages = new ConcurrentLinkedQueue<>();
        this.ended = new AtomicBoolean(false);
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getPlayer() instanceof final Player player)
                || !player.getUniqueId().equals(onlineProfile.getPlayerUUID())) {
            return;
        }
        final PacketWrapperFunction<? extends PacketWrapper<?>> packetWrapperFunction = PACKET_WRAPPER_FUNCTION_MAP.get(event.getPacketType());
        if (packetWrapperFunction == null) {
            return;
        }
        handlePacketWrapperFunction(packetWrapperFunction, event);
    }

    private <T extends PacketWrapper<?>> void handlePacketWrapperFunction(
            final PacketWrapperFunction<T> packetWrapperFunction, final PacketSendEvent event) {
        final T packetWrapper = packetWrapperFunction.getPacketWrapper(event);
        if (TAGGER.acceptIfTagged(packetWrapperFunction.getMessage(packetWrapper),
                untagged -> packetWrapperFunction.setMessage(packetWrapper, untagged))
                || chatHistory.getTagger().isTagged(packetWrapperFunction.getMessage(packetWrapper)) || ended.get()) {
            return;
        }
        event.setCancelled(true);
        final T packetWrapperCopy = packetWrapperFunction.copy(packetWrapper);
        messages.offer(packetWrapperCopy);
    }

    @Override
    public void begin() {
        this.packetListenerCommon = packetEventsAPI.getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @Override
    public void sendMessage(final Component component) {
        onlineProfile.getPlayer().sendMessage(TAGGER.tag(chatHistory.getTagger().tag(component)));
    }

    @Override
    public void end() {
        final User user = packetEventsAPI.getPlayerManager().getUser(onlineProfile.getPlayer());
        chatHistory.sendHistory(onlineProfile.getPlayer());
        while (!messages.isEmpty()) {
            user.sendPacketSilently(messages.poll());
        }
        ended.set(true);
        packetEventsAPI.getEventManager().unregisterListener(packetListenerCommon);
    }
}
