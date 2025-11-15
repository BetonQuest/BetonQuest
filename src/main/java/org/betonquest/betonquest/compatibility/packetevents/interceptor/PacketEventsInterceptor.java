package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.ChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.betonquest.betonquest.compatibility.packetevents.interceptor.packet.PacketWrapperFunction.PACKET_WRAPPER_FUNCTION_MAP;

/**
 * An interceptor that captures chat packets sent to a player, allowing for message interception and history management,
 * based on PacketEvents.
 */
public class PacketEventsInterceptor implements Interceptor, PacketListener {
    /**
     * A prefix that marks messages to be ignored by this interceptor.
     */
    private static final ComponentTagger TAGGER = new ComponentTagger("BetonQuest-Message-Interceptor-Passthrough-Tag");

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
    private final List<PacketWrapper<?>> messages;

    /**
     * The read-write lock for thread-safe when sending messages and ending interception.
     */
    private final ReadWriteLock lock;

    /**
     * Indicates whether the interception has ended.
     */
    private final AtomicBoolean ended;

    /**
     * The registered packet listener common.
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
        this.messages = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock(true);
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
        lock.readLock().lock();
        try {
            if (TAGGER.acceptIfTagged(packetWrapperFunction.getMessage(packetWrapper),
                    untagged -> packetWrapperFunction.setMessage(packetWrapper, untagged)) || ended.get()) {
                return;
            }
            event.setCancelled(true);
            messages.add(packetWrapper);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void begin() {
        this.packetListenerCommon = packetEventsAPI.getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @Override
    public void sendMessage(final Component component) {
        onlineProfile.getPlayer().sendMessage(TAGGER.tag(chatHistory.addBypass(component)));
    }

    @Override
    public void end() {
        lock.writeLock().lock();
        try {
            ended.set(true);
            final User user = packetEventsAPI.getPlayerManager().getUser(onlineProfile.getPlayer());
            new BukkitRunnable() {
                @Override
                public void run() {
                    chatHistory.sendHistory(onlineProfile.getPlayer());
                }
            }.runTaskLater(BetonQuest.getInstance(), 1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    messages.forEach(user::sendPacket);
                }
            }.runTaskLater(BetonQuest.getInstance(), 2);
            new BukkitRunnable() {
                @Override
                public void run() {
                    packetEventsAPI.getEventManager().unregisterListener(packetListenerCommon);
                }
            }.runTaskLater(BetonQuest.getInstance(), 20);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
