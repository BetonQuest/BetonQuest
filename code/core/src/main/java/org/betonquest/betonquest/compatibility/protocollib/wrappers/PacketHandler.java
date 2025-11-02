package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

/**
 * Interface for handling packets in BetonQuest using ProtocolLib.
 * This interface defines methods for sending, receiving, and broadcasting packets,
 * as well as retrieving the raw packet data and its type.
 */
public interface PacketHandler {

    /**
     * Retrieve a handle to the raw packet data.
     *
     * @return Raw packet data.
     */
    PacketContainer getHandle();

    /**
     * Retrieve the packet type.
     *
     * @return Packet type.
     */
    PacketType getType();

    /**
     * Send the current packet to the given receiver.
     *
     * @param receiver - the receiver.
     * @throws UncheckedPacketException If the packet cannot be sent.
     */
    void sendPacket(Player receiver);

    /**
     * Send the current packet to all online players.
     *
     * @throws UncheckedPacketException If the packet cannot be sent.
     */
    void broadcastPacket();

    /**
     * Simulate receiving the current packet from the given sender.
     *
     * @param sender - the sender.
     * @throws UncheckedPacketException if the packet cannot be received.
     */
    void receivePacket(Player sender);
}
