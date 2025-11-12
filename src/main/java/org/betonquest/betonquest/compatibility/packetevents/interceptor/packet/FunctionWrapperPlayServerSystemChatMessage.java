package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;

import java.util.function.Function;

/**
 * A PacketWrapperFunction implementation for handling WrapperPlayServerSystemChatMessage packets.
 */
public class FunctionWrapperPlayServerSystemChatMessage implements PacketWrapperFunction<WrapperPlayServerSystemChatMessage> {
    /**
     * Constructs a FunctionWrapperPlayServerSystemChatMessage instance.
     */
    public FunctionWrapperPlayServerSystemChatMessage() {
    }

    @Override
    public WrapperPlayServerSystemChatMessage getPacketWrapper(final PacketSendEvent event) {
        return new WrapperPlayServerSystemChatMessage(event);
    }

    @Override
    public Component getMessage(final WrapperPlayServerSystemChatMessage packetWrapper) {
        return packetWrapper.getMessage();
    }

    @Override
    public void setMessage(final WrapperPlayServerSystemChatMessage packetWrapper, final Component message) {
        packetWrapper.setMessage(message);
    }

    @Override
    public WrapperPlayServerSystemChatMessage transform(final WrapperPlayServerSystemChatMessage packetWrapper, final Function<Component, Component> messageTransformer) {
        return new WrapperPlayServerSystemChatMessage(packetWrapper.isOverlay(), messageTransformer.apply(getMessage(packetWrapper)));
    }
}
