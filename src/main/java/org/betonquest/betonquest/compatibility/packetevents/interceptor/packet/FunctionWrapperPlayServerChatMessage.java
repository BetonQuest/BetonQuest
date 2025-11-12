package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;
import net.kyori.adventure.text.Component;

import java.util.function.Function;

/**
 * A PacketWrapperFunction implementation for handling WrapperPlayServerChatMessage packets.
 */
public class FunctionWrapperPlayServerChatMessage implements PacketWrapperFunction<WrapperPlayServerChatMessage> {
    /**
     * Constructs a FunctionWrapperPlayServerChatMessage instance.
     */
    public FunctionWrapperPlayServerChatMessage() {
    }

    @Override
    public WrapperPlayServerChatMessage getPacketWrapper(final PacketSendEvent event) {
        return new WrapperPlayServerChatMessage(event);
    }

    @Override
    public Component getMessage(final WrapperPlayServerChatMessage packetWrapper) {
        return packetWrapper.getMessage().getChatContent();
    }

    @Override
    public void setMessage(final WrapperPlayServerChatMessage packetWrapper, final Component message) {
        packetWrapper.getMessage().setChatContent(message);
    }

    @Override
    public PacketWrapper<?> transform(final WrapperPlayServerChatMessage packetWrapper, final Function<Component, Component> messageTransformer) {
        final ChatMessage message = packetWrapper.getMessage();
        if (message instanceof final ChatMessage_v1_19_3 dotThree) {
            return new WrapperPlayServerDisguisedChat(messageTransformer.apply(message.getChatContent()), dotThree.getChatFormatting());
        }
        throw new IllegalStateException("A Player Chat Message can not be processed for older Minecraft versions.");
    }
}
