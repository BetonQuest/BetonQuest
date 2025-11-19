package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;
import net.kyori.adventure.text.Component;

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
    public Component getMessage(final WrapperPlayServerDisguisedChat packetWrapper) {
        return packetWrapper.getMessage();
    }

    @Override
    public void setMessage(final WrapperPlayServerDisguisedChat packetWrapper, final Component message) {
        packetWrapper.setMessage(message);
    }

    @Override
    public WrapperPlayServerDisguisedChat transform(final WrapperPlayServerDisguisedChat packetWrapper) {
        return new WrapperPlayServerDisguisedChat(getMessage(packetWrapper), packetWrapper.getChatFormatting());
    }

    @Override
    public WrapperPlayServerDisguisedChat copy(final WrapperPlayServerDisguisedChat packetWrapper) {
        return new WrapperPlayServerDisguisedChat(getMessage(packetWrapper), packetWrapper.getChatFormatting());
    }
}
