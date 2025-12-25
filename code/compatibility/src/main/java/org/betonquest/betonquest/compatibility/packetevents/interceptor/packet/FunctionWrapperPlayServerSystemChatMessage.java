package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;

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
    public WrapperPlayServerSystemChatMessage copy(final WrapperPlayServerSystemChatMessage packetWrapper) {
        return new WrapperPlayServerSystemChatMessage(packetWrapper.isOverlay(), packetWrapper.getMessage());
    }
}
