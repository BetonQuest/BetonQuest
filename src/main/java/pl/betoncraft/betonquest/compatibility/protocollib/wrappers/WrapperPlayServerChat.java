package pl.betoncraft.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import java.util.Arrays;

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

    /**
     * Retrieve Position.
     * <p>
     * Notes: 0 - Chat (chat box) ,1 - System Message (chat box), 2 - Above
     * action bar
     *
     * @return The current Position
     * @deprecated Magic values replaced by enum,
     * this will be removed in 2.0 release
     */
    // TODO Delete in BQ 2.0.0
    @Deprecated
    public byte getPosition() {
        final Byte position = getHandle().getBytes().readSafely(0);
        if (position == null) {
            return getChatType().getId();
        } else {
            return position;
        }
    }

    /**
     * Set Position.
     *
     * @param value - new value.
     * @deprecated Magic values replaced by enum,
     * this will be removed in 2.0 release
     */
    // TODO Delete in BQ 2.0.0
    @Deprecated
    public void setPosition(final byte value) {
        getHandle().getBytes().writeSafely(0, value);

        if (EnumWrappers.getChatTypeClass() != null) {
            Arrays.stream(ChatType.values()).filter(t -> t.getId() == value).findAny()
                    .ifPresent(t -> getHandle().getChatTypes().writeSafely(0, t));
        }
    }
}
