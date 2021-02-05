package org.betonquest.betonquest.compatibility.protocollib.conversation;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.Interceptor;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provide a packet interceptor to get all chat packets to player
 */
@SuppressWarnings("PMD.CommentRequired")
public class PacketInterceptor implements Interceptor, Listener {

    /**
     * A prefix that marks messages to be ignored by this interceptor.
     * To be invisible if the interceptor was closed before the message was sent the tag is a color code.
     * The actual tags colors are the hex-representation of the ASCII representing the string '_bq_'.
     */
    private static final String MESSAGE_PASSTHROUGH_TAG = "§5§f§6§2§7§1§5§f";

    protected final Conversation conv;
    protected final Player player;
    private final List<WrapperPlayServerChat> messages = new ArrayList<>();
    private final PacketAdapter packetAdapter;

    public PacketInterceptor(final Conversation conv, final String playerID) {
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);

        // Intercept Packets
        packetAdapter = new PacketAdapter(BetonQuest.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Server.CHAT

        ) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                if (event.getPlayer() != player) {
                    return;
                }

                if (event.getPacketType().equals(PacketType.Play.Server.CHAT)) {
                    final PacketContainer packet = event.getPacket();
                    final BaseComponent[] components = (BaseComponent[]) packet.getModifier().read(1);
                    if (components != null && components.length > 0 && ((TextComponent) components[0]).getText().contains(MESSAGE_PASSTHROUGH_TAG)) {
                        packet.getModifier().write(1, Arrays.copyOfRange(components, 1, components.length));
                        event.setPacket(packet);
                        return;
                    }

                    // Else save message to replay later
                    final WrapperPlayServerChat chat = new WrapperPlayServerChat(event.getPacket());
                    event.setCancelled(true);
                    messages.add(chat);
                }
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
    }

    /**
     * Send message, bypassing Interceptor
     */
    @Override
    public void sendMessage(final String message) {
        sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(final BaseComponent... message) {
        final BaseComponent[] components = (BaseComponent[]) ArrayUtils.addAll(new TextComponent[]{new TextComponent(MESSAGE_PASSTHROUGH_TAG)}, message);
        player.spigot().sendMessage(components);
    }


    @Override
    public void end() {
        // Stop Listening for Packets
        ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);

        //Send all messages to player
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final WrapperPlayServerChat message : messages) {
                    message.sendPacket(player);
                }
            }
        }.runTaskLater(BetonQuest.getInstance(), 20);
    }
}
