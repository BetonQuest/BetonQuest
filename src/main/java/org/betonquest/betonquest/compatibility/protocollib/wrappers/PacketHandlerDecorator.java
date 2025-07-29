package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

/**
 * A decorator for {@link PacketHandler} that allows extending the functionality of existing packet handlers.
 * This class implements the {@link PacketHandler} interface and delegates calls to the wrapped packet handler.
 */
public abstract class PacketHandlerDecorator implements PacketHandler {
    /**
     * The wrapped packet handler that this decorator extends.
     */
    private final PacketHandler packetHandler;

    /**
     * Creates a new packet handler decorator that wraps the given packet handler.
     *
     * @param packetHandler the packet handler to wrap
     */
    protected PacketHandlerDecorator(final PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    protected final PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public PacketContainer getHandle() {
        return packetHandler.getHandle();
    }

    @Override
    public PacketType getType() {
        return packetHandler.getType();
    }

    @Override
    public void sendPacket(final Player receiver) {
        packetHandler.sendPacket(receiver);
    }

    @Override
    public void broadcastPacket() {
        packetHandler.broadcastPacket();
    }

    @Override
    public void receivePacket(final Player sender) {
        packetHandler.receivePacket(sender);
    }
}
