package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class WrapperPlayServerMount extends PacketHandlerDecorator {

    public static final PacketType TYPE = PacketType.Play.Server.MOUNT;

    public WrapperPlayServerMount() {
        this(new DefaultPacketHandler(TYPE));
    }

    public WrapperPlayServerMount(final PacketContainer packet) {
        this(new DefaultPacketHandler(packet, TYPE));
    }

    public WrapperPlayServerMount(final PacketHandler packetHandler) {
        super(packetHandler);
        if (getPacketHandler().getType() != TYPE) {
            throw new IllegalArgumentException(getPacketHandler().getType() + " is not a packet of type " + TYPE);
        }
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: vehicle's EID
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
     * Retrieve the entity involved in this event.
     *
     * @param world - the current world of the entity.
     * @return The involved entity.
     */
    public Entity getEntity(final World world) {
        return getHandle().getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity involved in this event.
     *
     * @param event - the packet event.
     * @return The involved entity.
     */
    public Entity getEntity(final PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public int[] getPassengerIds() {
        return getHandle().getIntegerArrays().read(0);
    }

    public void setPassengerIds(final int... value) {
        getHandle().getIntegerArrays().write(0, value);
    }

    public List<Entity> getPassengers(final PacketEvent event) {
        return getPassengers(event.getPlayer().getWorld());
    }

    public List<Entity> getPassengers(final World world) {
        final int[] ids = getPassengerIds();
        final List<Entity> passengers = new ArrayList<>();
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        for (final int id : ids) {
            final Entity entity = manager.getEntityFromID(world, id);
            if (entity != null) {
                passengers.add(entity);
            }
        }

        return passengers;
    }

    public void setPassengers(final List<Entity> value) {
        final int[] array = new int[value.size()];
        for (int i = 0; i < value.size(); i++) {
            array[i] = value.get(i).getEntityId();
        }

        setPassengerIds(array);
    }
}
