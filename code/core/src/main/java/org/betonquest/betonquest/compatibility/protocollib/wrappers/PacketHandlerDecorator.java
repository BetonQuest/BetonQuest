package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public abstract class PacketHandlerDecorator implements PacketHandler {

    private final PacketHandler packetHandler;

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
