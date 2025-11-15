package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.function.Function;

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
     * Retrieves the message component from the given packet wrapper.
     *
     * @param packetWrapper the packet wrapper
     * @return the message component
     */
    Component getMessage(T packetWrapper);

    /**
     * Sets the message component in the given packet wrapper.
     *
     * @param packetWrapper the packet wrapper
     * @param message       the message component to set
     */
    void setMessage(T packetWrapper, Component message);

    /**
     * Transforms the given packet wrapper, potentially into a different type, that can be sent to the player.
     *
     * @param packetWrapper      the packet wrapper to transform
     * @param messageTransformer a function to transform the message component
     * @return the transformed packet wrapper
     */
    PacketWrapper<?> transform(T packetWrapper, Function<Component, Component> messageTransformer);
}
