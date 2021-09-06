package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("PMD.CommentRequired")
public class DefaultPacketHandler implements PacketHandler {

    private final PacketContainer gethandle;

    private final PacketType type;

    /**
     * Constructs a new strongly typed wrapper for the given packet.
     *
     * @param handle - handle to the raw packet data.
     * @param type   - the packet type.
     */
    protected DefaultPacketHandler(final PacketContainer handle, final PacketType type) {
        if (handle == null)
            throw new IllegalArgumentException("Packet handle cannot be NULL.");
        if (!Objects.equal(handle.getType(), type))
            throw new IllegalArgumentException(
                    handle.getHandle() + " is not a packet of type " + type);

        this.gethandle = handle;
        this.type = type;
    }

    @Override
    public PacketContainer getHandle() {
        return gethandle;
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
