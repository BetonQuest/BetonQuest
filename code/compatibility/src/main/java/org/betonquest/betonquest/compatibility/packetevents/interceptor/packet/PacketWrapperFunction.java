package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.Map;

/**
 * A function interface for handling different packet wrappers related to chat messages.
 *
 * @param <T> the type of PacketWrapper being handled
 */
public interface PacketWrapperFunction<T extends PacketWrapper<?>> {

    /**
     * A map linking PacketTypes to their corresponding PacketWrapperFunction implementations.
     */
    Map<PacketTypeCommon, PacketWrapperFunction<?>> PACKET_WRAPPER_FUNCTION_MAP = Map.of(
            PacketType.Play.Server.CHAT_MESSAGE, new FunctionWrapperPlayServerChatMessage(),
            PacketType.Play.Server.SYSTEM_CHAT_MESSAGE, new FunctionWrapperPlayServerSystemChatMessage(),
            PacketType.Play.Server.DISGUISED_CHAT, new FunctionWrapperPlayServerDisguisedChat()
    );

    /**
     * Gets the packet wrapper from the given PacketSendEvent.
     *
     * @param event the PacketSendEvent
     * @return the corresponding PacketWrapper
     */
    T getPacketWrapper(PacketSendEvent event);

    /**
     * Transforms the given packet wrapper, potentially into a different type, that can be sent to the player.
     *
     * @param packetWrapper the packet wrapper to transform
     * @return the transformed packet wrapper
     */
    default PacketWrapper<?> transform(final T packetWrapper) {
        return copy(packetWrapper);
    }

    /**
     * Creates a copy of the given packet wrapper.
     *
     * @param packetWrapper the packet wrapper to copy
     * @return the copied packet wrapper
     */
    T copy(T packetWrapper);
}
