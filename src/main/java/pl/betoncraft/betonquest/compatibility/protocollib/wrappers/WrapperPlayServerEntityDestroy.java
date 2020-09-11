package pl.betoncraft.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;

import java.util.List;

public class WrapperPlayServerEntityDestroy extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    public WrapperPlayServerEntityDestroy() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityDestroy(final PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the IDs of the entities that will be destroyed.
     *
     * @return The current entities.
     */
    public List<Integer> getEntities() {
        return Ints.asList(handle.getIntegerArrays().read(0));
    }

    /**
     * Set the entities that will be destroyed.
     */
    public void setEntities(final List<Integer> entities) {
        setEntities(Ints.toArray(entities));
    }

    /**
     * Set the entities that will be destroyed.
     */
    public void setEntities(final int[] entities) {
        handle.getIntegerArrays().write(0, entities);
    }
}

