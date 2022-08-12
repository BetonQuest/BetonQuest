package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * Wrapper for ENTITY METADATA packet
 */
public class WrapperPlayServerEntityMetadata extends PacketHandlerDecorator{

    /**
     * field of ENTITY METADATA packet type
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    /**
     * Creating Packet Handler
     */
    public WrapperPlayServerEntityMetadata() {
        this(new DefaultPacketHandler(TYPE));
    }

    /**
     * Creating Packet from PacketHandler
     * @param packetHandler PacketHandler for Creating PacketContainer
     */
    public WrapperPlayServerEntityMetadata(final PacketHandler packetHandler) {
        super(packetHandler);
        if (getPacketHandler().getType() != TYPE) {
            throw new IllegalArgumentException(getPacketHandler().getType() + " is not a packet of type " + TYPE);
        }
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return getHandle().getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(final int value) {
        getHandle().getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(final World world) {
        return getHandle().getEntityModifier(world).read(0);
    }

    /**
     * Retrieve Metadata.
     *
     * @return WatchableObject of Entity Metadata
     */
    public List<WrappedWatchableObject> getMetadata() {
        return getHandle().getWatchableCollectionModifier().read(0);
    }

    /**
     * Set Metadata.
     *
     * @param value - WatchableObject Value
     */
    public void setMetadata(final List<WrappedWatchableObject> value) {
        getHandle().getWatchableCollectionModifier().write(0, value);
    }
}
