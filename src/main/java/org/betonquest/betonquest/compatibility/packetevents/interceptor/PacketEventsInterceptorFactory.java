package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.ChatHistory;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;

/**
 * A factory for creating PacketEventsInterceptor instances.
 */
public class PacketEventsInterceptorFactory implements InterceptorFactory {
    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * The chat history manager.
     */
    private final ChatHistory chatHistory;

    /**
     * Constructs a PacketEventsInterceptorFactory.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param chatHistory     the chat history manager
     */
    public PacketEventsInterceptorFactory(final PacketEventsAPI<?> packetEventsAPI, final ChatHistory chatHistory) {
        this.packetEventsAPI = packetEventsAPI;
        this.chatHistory = chatHistory;
    }

    @Override
    public Interceptor create(final OnlineProfile onlineProfile) {
        return new PacketEventsInterceptor(packetEventsAPI, chatHistory, onlineProfile);
    }
}
