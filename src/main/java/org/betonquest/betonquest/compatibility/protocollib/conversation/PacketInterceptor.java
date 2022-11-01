package org.betonquest.betonquest.compatibility.protocollib.conversation;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.async.AsyncListenerHandler;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.Interceptor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
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
    private int baseComponentIndex = -1;

    @SuppressWarnings("PMD.CognitiveComplexity")
    public PacketInterceptor(final Conversation conv, final Profile profile) {
        this.conv = conv;
        this.player = profile.getOfflinePlayer().getPlayer();

        // Intercept Packets
        packetAdapter = new PacketAdapter(BetonQuest.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Server.CHAT

        ) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }

                if (event.getPacketType().equals(PacketType.Play.Server.CHAT)) {
                    final PacketContainer packet = event.getPacket();
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

                    final WrapperPlayServerChat chat = new WrapperPlayServerChat(packet);
                    if (chat.getChatType() == EnumWrappers.ChatType.GAME_INFO) {
                        return;
                    }

                    event.setCancelled(true);
                    messages.add(chat);
                }
            }
        };

        final AsyncListenerHandler handler = ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(packetAdapter);
        handler.start();
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
        final BaseComponent[] components = ArrayUtils.addAll(new TextComponent[]{new TextComponent(MESSAGE_PASSTHROUGH_TAG)}, message);
        player.spigot().sendMessage(components);
    }


    @Override
    public void end() {
        // Stop Listening for Packets
        ProtocolLibrary.getProtocolManager().getAsynchronousManager().unregisterAsyncHandler(packetAdapter);

        //Send all messages to player
        for (final WrapperPlayServerChat message : messages) {
            message.sendPacket(player);
        }
    }
}
