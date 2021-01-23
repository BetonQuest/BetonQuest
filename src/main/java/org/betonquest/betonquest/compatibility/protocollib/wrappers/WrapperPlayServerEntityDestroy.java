package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class WrapperPlayServerEntityDestroy extends PacketHandlerDecorator {

    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    public WrapperPlayServerEntityDestroy() {
        this(new DefaultPacketHandler(TYPE));
    }

    public WrapperPlayServerEntityDestroy(final PacketContainer packet) {
        this(new DefaultPacketHandler(packet, TYPE));
    }

    public WrapperPlayServerEntityDestroy(final PacketHandler packetHandler) {
        super(packetHandler);
        if (getPacketHandler().getType() != TYPE) {
            throw new IllegalArgumentException(getPacketHandler().getType() + " is not a packet of type " + TYPE);
        }
    }

    /**
     * Retrieve the IDs of the entities that will be destroyed.
     *
     * @return The current entities.
     */
    public List<Integer> getEntities() {
        return Ints.asList(getHandle().getIntegerArrays().read(0));
    }

    /**
     * Set the entities that will be destroyed.
     *
     * @param entities The entities.
     */
    public void setEntities(final List<Integer> entities) {
        setEntities(Ints.toArray(entities));
    }

    /**
     * Set the entities that will be destroyed.
     *
     * @param entities The entities.
     */
    public void setEntities(final int... entities) {
        getHandle().getIntegerArrays().write(0, entities);
    }
}

