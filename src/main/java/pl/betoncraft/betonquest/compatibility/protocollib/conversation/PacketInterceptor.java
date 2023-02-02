package pl.betoncraft.betonquest.compatibility.protocollib.conversation;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.async.AsyncListenerHandler;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.Interceptor;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
    private final List<PacketContainer> messages;
    private final PacketAdapter packetAdapter;
    private int baseComponentIndex = -1;

    @SuppressWarnings("PMD.CognitiveComplexity")
    public PacketInterceptor(final Conversation conv, final String playerID) {
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);
        this.messages = new ArrayList<>();

        packetAdapter = new PacketAdapter(BetonQuest.getInstance(), ListenerPriority.HIGHEST, getPacketTypes()) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }
                final PacketContainer packet = event.getPacket();
                final PacketType packetType = packet.getType();
                if (MinecraftVersion.WILD_UPDATE.atOrAbove()) {
                    if (packetType.equals(PacketType.Play.Server.SYSTEM_CHAT)) {
                        final String message = packet.getStrings().read(0);
                        if (message != null && message.contains("{\"extra\":[{\"text\":\"" + MESSAGE_PASSTHROUGH_TAG + "\"}")) {
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

        final AsyncListenerHandler handler = ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(packetAdapter);
        handler.start();
    }

    private static List<PacketType> getPacketTypes() {
        final List<PacketType> packets = new ArrayList<>();
        packets.add(PacketType.Play.Server.CHAT);
        if (MinecraftVersion.WILD_UPDATE.atOrAbove()) {
            packets.add(PacketType.Play.Server.SYSTEM_CHAT);
        }
        if (MinecraftVersion.FEATURE_PREVIEW_UPDATE.atOrAbove()) {
            packets.add(PacketType.Play.Server.DISGUISED_CHAT);
        }
        return packets;
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
        for (final PacketContainer message : messages) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, message);
        }
    }
}
