package org.betonquest.betonquest.compatibility.packetevents.passenger;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A FakeArmorStandPassenger that listens to steering input packets and delegates the inputs to a SteeringControl implementation.
 */
public class FakeArmorStandPassengerController extends FakeArmorStandPassenger implements PacketListener {
    /**
     * The steering control implementation.
     */
    private final SteeringControl control;

    /**
     * The registered packet listener.
     */
    @Nullable
    private PacketListenerCommon registeredListener;

    /**
     * Constructs a new FakeArmorStandPassenger that also created a new entity ID for the armor stand.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param player          the player to mount
     * @param control         the steering control implementation
     */
    public FakeArmorStandPassengerController(final PacketEventsAPI<?> packetEventsAPI, final Player player, final SteeringControl control) {
        super(packetEventsAPI, player);
        this.control = control;
    }

    @Override
    public void mount(final Location location) {
        super.mount(location);
        this.registeredListener = packetEventsAPI.getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @Override
    public void unmount() {
        if (registeredListener != null) {
            packetEventsAPI.getEventManager().unregisterListener(registeredListener);
        }
        super.unmount();
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.isCancelled() || !player.equals(event.getPlayer())) {
            return;
        }
        // TODO version switch:
        //  Remove this code when only 1.21.3+ is supported
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            steer(event);
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_INPUT) {
            playerInput(event);
        }
    }

    private void steer(final PacketReceiveEvent event) {
        final WrapperPlayClientSteerVehicle steerVehicle = new WrapperPlayClientSteerVehicle(event);
        if (steerVehicle.isUnmount()) {
            control.unmount();
        }
        if (steerVehicle.isJump()) {
            control.jump();
        }
        if (steerVehicle.getForward() > 0) {
            control.forward();
        }
        if (steerVehicle.getForward() < 0) {
            control.back();
        }
        if (steerVehicle.getSideways() < 0) {
            control.left();
        }
        if (steerVehicle.getSideways() > 0) {
            control.right();
        }
    }

    private void playerInput(final PacketReceiveEvent event) {
        final WrapperPlayClientPlayerInput playerInput = new WrapperPlayClientPlayerInput(event);
        if (playerInput.isShift()) {
            control.unmount();
        }
        if (playerInput.isJump()) {
            control.jump();
        }
        if (playerInput.isForward()) {
            control.forward();
        }
        if (playerInput.isBackward()) {
            control.back();
        }
        if (playerInput.isLeft()) {
            control.left();
        }
        if (playerInput.isRight()) {
            control.right();
        }
    }

    /**
     * Interface for steering control inputs.
     */
    public interface SteeringControl {
        /**
         * Processes the unmount input.
         */
        void unmount();

        /**
         * Processes the jump input.
         */
        void jump();

        /**
         * Processes the forward input.
         */
        void forward();

        /**
         * Processes the backwards input.
         */
        void back();

        /**
         * Processes the left input.
         */
        void left();

        /**
         * Processes the right input.
         */
        void right();
    }
}
