package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.packetwrapper.WrapperPlayClientSteerVehicle;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

/**
 * Wrapper for the PacketPlayClientSteerVehicle class.
 */
public class WrapperPlayClientSteerVehicleUpdated extends WrapperPlayClientSteerVehicle {

    /**
     * Default constructor.
     */
    public WrapperPlayClientSteerVehicleUpdated() {
        super();

    }

    /**
     * Constructor to wrap existing packet.
     * @param packet - the packet to wrap.
     */
    public WrapperPlayClientSteerVehicleUpdated(final PacketContainer packet) {
        super(packet);
    }

    @Override
    public float getSideways() {
        final StructureModifier<Boolean> input = this.getHandle().getStructures().read(0).getBooleans();

        float sideways = 0;

        if (input.read(2)) {
            sideways += 1;
        }
        if (input.read(3)) {
            sideways -= 1;
        }

        return sideways;
    }

    @Override
    public void setSideways(final float value) {
        final StructureModifier<Boolean> input = this.getHandle().getStructures().read(0).getBooleans();

        if (value > 0) {
            input.write(2, true);
            input.write(3, false);
        } else if (value < 0) {
            input.write(2, false);
            input.write(3, true);
        } else {
            input.write(2, false);
            input.write(3, false);
        }
    }

    @Override
    public float getForward() {
        final StructureModifier<Boolean> input = this.getHandle().getStructures().read(0).getBooleans();

        float forward = 0;

        if (input.read(0)) {
            forward += 1;
        }
        if (input.read(1)) {
            forward -= 1;
        }

        return forward;

    }

    @Override
    public void setForward(final float value) {
        final StructureModifier<Boolean> input = this.getHandle().getStructures().read(0).getBooleans();
        if (value > 0) {
            input.write(0, true);
            input.write(1, false);
        } else if (value < 0) {
            input.write(0, false);
            input.write(1, true);
        } else {
            input.write(0, false);
            input.write(1, false);
        }
    }

    @Override
    public boolean isJump() {
        return this.handle.getStructures().read(0).getBooleans().read(4);
    }

    @Override
    public void setJump(final boolean value) {
        this.handle.getStructures().read(0).getBooleans().write(4, value);
    }

    @Override
    public boolean isUnmount() {
        return this.handle.getStructures().read(0).getBooleans().read(5);
    }

    @Override
    public void setUnmount(final boolean value) {
        this.handle.getStructures().read(0).getBooleans().write(5, value);
    }

}
