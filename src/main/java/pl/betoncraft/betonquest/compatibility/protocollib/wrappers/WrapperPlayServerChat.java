package pl.betoncraft.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

@SuppressWarnings("PMD.CommentRequired")
public class WrapperPlayServerChat extends PacketHandlerDecorator {

    public static final PacketType TYPE = PacketType.Play.Server.CHAT;

    public WrapperPlayServerChat() {
        this(new DefaultPacketHandler(TYPE));
    }

    public WrapperPlayServerChat(final PacketContainer packet) {
        this(new DefaultPacketHandler(packet, TYPE));
    }

    public WrapperPlayServerChat(final PacketHandler packetHandler) {
        super(packetHandler);
        if (getPacketHandler().getType() != TYPE) {
            throw new IllegalArgumentException(getPacketHandler().getType() + " is not a packet of type " + TYPE);
        }
    }

    /**
     * Retrieve the chat message.
     * <p>
     * Limited to 32767 bytes
     *
     * @return The current message
     */
    public WrappedChatComponent getMessage() {
        return getHandle().getChatComponents().read(0);
    }

    /**
     * Set the message.
     *
     * @param value - new value.
     */
    public void setMessage(final WrappedChatComponent value) {
        getHandle().getChatComponents().write(0, value);
    }

    public ChatType getChatType() {
        return getHandle().getChatTypes().read(0);
    }

    public void setChatType(final ChatType type) {
        getHandle().getChatTypes().write(0, type);
    }
}
