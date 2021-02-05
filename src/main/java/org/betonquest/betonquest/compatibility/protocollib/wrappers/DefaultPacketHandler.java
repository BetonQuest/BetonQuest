package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("PMD.CommentRequired")
public final class DefaultPacketHandler implements PacketHandler {

    private final PacketContainer handle;

    private final PacketType type;

    /**
     * Constructs a new strongly typed wrapper with a new packet.
     *
     * @param type - the packet type.
     */
    protected DefaultPacketHandler(final PacketType type) {
        this(new PacketContainer(type), type);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new strongly typed wrapper for the given packet.
     *
     * @param handle - handle to the raw packet data.
     * @param type   - the packet type.
     */
    protected DefaultPacketHandler(final PacketContainer handle, final PacketType type) {
        this(handle);
        if (!Objects.equal(handle.getType(), type)) {
            throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);
        }
    }

    /**
     * Constructs a new wrapper for the given packet.
     *
     * @param handle - handle to the raw packet data.
     */
    protected DefaultPacketHandler(final PacketContainer handle) {
        // Make sure we're given a valid packet
        if (handle == null) {
            throw new IllegalArgumentException("Packet handle cannot be NULL.");
        }

        this.handle = handle;
        this.type = handle.getType();
    }

    @Override
    public PacketContainer getHandle() {
        return handle;
    }

    @Override
    public PacketType getType() {
        return type;
    }

    @Override
    public void sendPacket(final Player receiver) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, getHandle());
        } catch (final InvocationTargetException e) {
            throw new UncheckedPacketException("Cannot send packet.", e);
        }
    }

    @Override
    public void broadcastPacket() {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHandle());
    }

    @Override
    public void receivePacket(final Player sender) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, getHandle());
        } catch (final InvocationTargetException | IllegalAccessException e) {
            throw new UncheckedPacketException("Cannot receive packet.", e);
        }
    }
}
