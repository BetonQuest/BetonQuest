package org.betonquest.betonquest.compatibility.packetevents.passenger;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import org.betonquest.betonquest.compatibility.packetevents.conversation.input.ConversationAction;
import org.betonquest.betonquest.compatibility.packetevents.conversation.input.ConversationSession;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * A FakeArmorStandPassenger that listens to input packets and delegates the inputs to a Conversation.
 */
public class FakeArmorStandPassengerController extends FakeArmorStandPassenger implements ConversationSession, PacketListener {

    /**
     * The conversation action to call.
     */
    private final ConversationAction action;

    /**
     * The registered packet listener.
     */
    @Nullable
    private PacketListenerCommon registeredListener;

    /**
     * Constructs a new FakeArmorStandPassenger that also catches control packets for the armor stand.
     *
     * @param plugin          the plugin instance
     * @param packetEventsAPI the PacketEvents API instance
     * @param player          the player to mount
     * @param action          the conversation action to call on input
     */
    public FakeArmorStandPassengerController(final Plugin plugin, final PacketEventsAPI<?> packetEventsAPI, final Player player, final ConversationAction action) {
        super(plugin, packetEventsAPI, player);
        this.action = action;
    }

    @Override
    public void begin() {
        mount(getBlockBelowPlayer(player));
        this.registeredListener = packetEventsAPI.getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @Override
    public void end() {
        if (registeredListener != null) {
            packetEventsAPI.getEventManager().unregisterListener(registeredListener);
        }
        unmount();
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
            action.unmount();
        }
        if (steerVehicle.isJump()) {
            action.jump();
        }
        if (steerVehicle.getForward() > 0) {
            action.forward();
        }
        if (steerVehicle.getForward() < 0) {
            action.back();
        }
        if (steerVehicle.getSideways() < 0) {
            action.left();
        }
        if (steerVehicle.getSideways() > 0) {
            action.right();
        }
    }

    private void playerInput(final PacketReceiveEvent event) {
        final WrapperPlayClientPlayerInput playerInput = new WrapperPlayClientPlayerInput(event);
        if (playerInput.isShift()) {
            action.unmount();
        }
        if (playerInput.isJump()) {
            action.jump();
        }
        if (playerInput.isForward()) {
            action.forward();
        }
        if (playerInput.isBackward()) {
            action.back();
        }
        if (playerInput.isLeft()) {
            action.left();
        }
        if (playerInput.isRight()) {
            action.right();
        }
    }
}
