package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

@SuppressWarnings("PMD.CommentRequired")
public class WrapperPlayClientSteerVehicle extends PacketHandlerDecorator {

    public static final PacketType TYPE = PacketType.Play.Client.STEER_VEHICLE;

    public WrapperPlayClientSteerVehicle() {
        this(new DefaultPacketHandler(TYPE));
    }

    public WrapperPlayClientSteerVehicle(final PacketContainer packet) {
        this(new DefaultPacketHandler(packet, TYPE));
    }

    public WrapperPlayClientSteerVehicle(final PacketHandler packetHandler) {
        super(packetHandler);
        if (getPacketHandler().getType() != TYPE) {
            throw new IllegalArgumentException(getPacketHandler().getType() + " is not a packet of type " + TYPE);
        }
    }

    /**
     * Retrieve Sideways.
     * <p>
     * Notes: positive to the left of the player
     *
     * @return The current Sideways
     */
    public float getSideways() {
        return getHandle().getFloat().read(0);
    }

    /**
     * Set Sideways.
     *
     * @param value - new value.
     */
    public void setSideways(final float value) {
        getHandle().getFloat().write(0, value);
    }

    /**
     * Retrieve Forward.
     * <p>
     * Notes: positive forward
     *
     * @return The current Forward
     */
    public float getForward() {
        return getHandle().getFloat().read(1);
    }

    /**
     * Set Forward.
     *
     * @param value - new value.
     */
    public void setForward(final float value) {
        getHandle().getFloat().write(1, value);
    }

    public boolean isJump() {
        return getHandle().getBooleans().read(0);
    }

    public void setJump(final boolean value) {
        getHandle().getBooleans().write(0, value);
    }

    public boolean isUnmount() {
        return getHandle().getBooleans().read(1);
    }

    public void setUnmount(final boolean value) {
        getHandle().getBooleans().write(1, value);
    }

}
