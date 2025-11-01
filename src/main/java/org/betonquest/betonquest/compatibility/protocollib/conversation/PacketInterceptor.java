package org.betonquest.betonquest.compatibility.protocollib.conversation;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a packet interceptor that catches all chat packets sent to a player.
 */
public class PacketInterceptor implements Interceptor, Listener {

    /**
     * A prefix that marks messages to be ignored by this interceptor.
     * To be invisible if the interceptor was closed before the message was sent, the tag is a color code.
     * The actual tags colors are the hex-representation of the ASCII representing the string '_bq_'.
     */
    private static final String MESSAGE_PASSTHROUGH_TAG = "§5§f§6§2§7§1§5§f";

    /**
     * Player to send the messages.
     */
    protected final Player player;

    /**
     * Intercepted messages.
     */
    private final List<PacketContainer> messages;

    /**
     * Adapter to intercept messages.
     */
    private final PacketAdapter packetAdapter;

    /**
     * Component index to use in pre 1.19 chat packets.
     */
    private int baseComponentIndex = -1;

    /**
     * Create a new Packet Interceptor.
     *
     * @param onlineProfile the online profile to send the messages
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    public PacketInterceptor(final OnlineProfile onlineProfile) {
        this.player = onlineProfile.getPlayer();
        this.messages = new ArrayList<>();

        packetAdapter = new PacketAdapter(BetonQuest.getInstance(), ListenerPriority.LOWEST, getPacketTypes()) {
            @SuppressWarnings("PMD.CyclomaticComplexity")
            @Override
            public void onPacketSending(final PacketEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }
                final PacketContainer packet = event.getPacket();
                final PacketType packetType = packet.getType();
                // TODO version switch:
                //  Remove this code when only 1.19+ is supported
                if (PaperLib.isVersion(19, 0)) {
                    if (packetType.equals(PacketType.Play.Server.SYSTEM_CHAT)) {
                        final String message = PaperLib.isVersion(20, 4)
                                ? packet.getChatComponents().read(0).getJson()
                                : packet.getStrings().read(0);
                        if (message != null && message.contains("\"" + MESSAGE_PASSTHROUGH_TAG + "\"")) {
                            return;
                        }
                    }
                } else {
                    if (baseComponentIndex == -1) {
                        if (packet.getModifier().read(1) instanceof BaseComponent[]) {
                            baseComponentIndex = 1;
                        } else {
                            baseComponentIndex = 2;
                        }
                    }
                    final BaseComponent[] components = (BaseComponent[]) packet.getModifier().read(baseComponentIndex);
                    if (components != null && components.length > 0 && ((TextComponent) components[0]).getText().contains(MESSAGE_PASSTHROUGH_TAG)) {
                        return;
                    }
                    if (packet.getChatTypes().read(0) == EnumWrappers.ChatType.GAME_INFO) {
                        return;
                    }
                }
                event.setCancelled(true);
                messages.add(packet);
            }
        };
    }

    private static List<PacketType> getPacketTypes() {
        final List<PacketType> packets = new ArrayList<>();
        packets.add(PacketType.Play.Server.CHAT);
        if (PaperLib.isVersion(19, 0)) {
            packets.add(PacketType.Play.Server.SYSTEM_CHAT);
        }
        if (PaperLib.isVersion(19, 3)) {
            packets.add(PacketType.Play.Server.DISGUISED_CHAT);
        }
        return packets;
    }

    @Override
    public void begin() {
        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
    }

    @Override
    public void sendMessage(final Component message) {
        player.sendMessage(Component.text(MESSAGE_PASSTHROUGH_TAG).append(message));
    }

    @Override
    public void end() {
        // Stop Listening for Packets
        ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);

        //Send all messages to player
        for (final PacketContainer message : messages) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, message);
        }
    }
}
