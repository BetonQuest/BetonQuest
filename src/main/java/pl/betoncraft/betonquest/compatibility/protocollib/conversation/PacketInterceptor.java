/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.betoncraft.betonquest.compatibility.protocollib.conversation;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerChat;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.Interceptor;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provide a packet interceptor to get all chat packets to player
 */
public class PacketInterceptor implements Interceptor, Listener {

    protected final Conversation conv;
    protected final Player player;
    private ArrayList<WrapperPlayServerChat> messages = new ArrayList<>();
    private PacketAdapter packetAdapter;

    public PacketInterceptor(Conversation conv, String playerID) {
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);

        // Intercept Packets
        packetAdapter = new PacketAdapter(BetonQuest.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Server.CHAT

        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPlayer() != player) {
                    return;
                }

                if (event.getPacketType().equals(PacketType.Play.Server.CHAT)) {
                    PacketContainer packet = event.getPacket();
                    BaseComponent[] bc = (BaseComponent[]) packet.getModifier().read(1);
                    if (bc != null && bc.length > 0 && ((TextComponent) bc[0]).getText().contains("_bq_")) {
                        packet.getModifier().write(1, Arrays.copyOfRange(bc, 1, bc.length));
                        event.setPacket(packet);
                        return;
                    }

                    // Else save message to replay later
                    WrapperPlayServerChat chat = new WrapperPlayServerChat(event.getPacket());
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
    public void sendMessage(String message) {
        sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        // Tag the message. Is there a better way? Perhaps an invisible method is better
        BaseComponent[] components = (BaseComponent[]) ArrayUtils.addAll(new TextComponent[]{new TextComponent("_bq_")}, message);
        player.spigot().sendMessage(components);
    }


    @Override
    public void end() {
        // Stop Listening for Packets
        ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);

        // Send all messages to player
        for (WrapperPlayServerChat message : messages) {
            message.sendPacket(player);
        }
    }
}
