package org.betonquest.betonquest.compatibility.packetevents.interceptor.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;
import net.kyori.adventure.text.Component;

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
    public PacketWrapper<?> transform(final WrapperPlayServerChatMessage packetWrapper) {
        final ChatMessage message = packetWrapper.getMessage();
        if (message instanceof final ChatMessage_v1_19_3 chatMessage) {
            final Component chatContent = chatMessage.getUnsignedChatContent().orElse(message.getChatContent());
            return new WrapperPlayServerDisguisedChat(chatContent, chatMessage.getChatFormatting());
        }
        return copy(packetWrapper);
    }

    @Override
    public WrapperPlayServerChatMessage copy(final WrapperPlayServerChatMessage packetWrapper) {
        return new WrapperPlayServerChatMessage(packetWrapper.getMessage());
    }
}
