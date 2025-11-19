package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;

/**
 * A PacketWrapperFunction implementation for handling WrapperPlayServerDisguisedChat packets.
 */
public class FunctionWrapperPlayServerDisguisedChat implements PacketWrapperFunction<WrapperPlayServerDisguisedChat> {
    /**
     * Constructs a FunctionWrapperPlayServerDisguisedChat instance.
     */
    public FunctionWrapperPlayServerDisguisedChat() {
    }

    @Override
    public WrapperPlayServerDisguisedChat getPacketWrapper(final PacketSendEvent event) {
        return new WrapperPlayServerDisguisedChat(event);
    }

    @Override
    public WrapperPlayServerDisguisedChat copy(final WrapperPlayServerDisguisedChat packetWrapper) {
        return new WrapperPlayServerDisguisedChat(packetWrapper.getMessage(), packetWrapper.getChatFormatting());
    }
}
