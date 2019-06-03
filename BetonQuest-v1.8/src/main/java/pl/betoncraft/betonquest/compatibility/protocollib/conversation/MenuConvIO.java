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
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayClientSteerVehicle;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerAnimation;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerAttachEntity;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerChat;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerEntityDestroy;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerEntityMetadata;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerSpawnEntityLiving;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.ConversationColors;
import pl.betoncraft.betonquest.conversation.ConversationIO;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocalChatPaginator;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuConvIO implements Listener, ConversationIO {

    protected final Conversation conv;
    protected final String name;
    protected final Player player;
    protected final HashMap<String, ChatColor[]> colors;
    // Actions
    protected Map<CONTROL, ACTION> controls = new HashMap<>();
    protected String configControlCancel = "sneak";
    protected List<String> options;
    protected int oldSelectedOption = 0;
    protected int selectedOption = 0;
    protected String npcText;
    protected String npcName;
    protected boolean ended = false;
    protected PacketAdapter packetAdapter;
    protected BukkitRunnable displayRunnable;
    protected boolean debounce = false;
    protected String displayOutput;
    protected String formattedNpcName;
    protected String configControlSelect = "jump,left_click";

    // Configuration
    protected Integer configLineLength = 60;
    protected String configNpcWrap = "&l &r".replace('&', '§');
    protected String configNpcText = "&l &r&f{npc_text}".replace('&', '§');
    protected String configNpcTextReset = "&f".replace('&', '§');
    protected String configOptionWrap = "&r&l &l &l &l &r".replace('&', '§');
    protected String configOptionText = "&l &l &l &l &r&8[ &b{option_text}&8 ]".replace('&', '§');
    protected String configOptionTextReset = "&b".replace('&', '§');
    protected String configOptionSelected = "&l &r &r&7»&r &8[ &f&n{option_text}&8 ]".replace('&', '§');
    protected String configOptionSelectedReset = "&f".replace('&', '§');
    protected String configOptionSelectedWrap = "&r&l &l &l &l &r&f&n".replace('&', '§');
    protected String configControlMove = "scroll,move";
    protected String configNpcNameType = "chat";
    protected String configNpcNameAlign = "center";
    protected String configNpcNameFormat = "&e{npc_name}&r".replace('&', '§');
    private WrapperPlayServerSpawnEntityLiving stand = null;

    public MenuConvIO(Conversation conv, String playerID) {
        this.options = new ArrayList<>();
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);
        this.name = player.getName();
        this.colors = ConversationColors.getColors();

        // Load Configuration from custom.yml with some sane defaults, loading our current package last
        for (ConfigPackage pack : Stream.concat(
                Config.getPackages().values().stream()
                        .filter(p -> p != conv.getPackage()),
                Stream.of(conv.getPackage()))
                .collect(Collectors.toList())) {
            ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("menu_conv_io");
            if (section == null) {
                continue;
            }

            configLineLength = section.getInt("line_length", configLineLength);
            configNpcWrap = section.getString("npc_wrap", configNpcWrap).replace('&', '§');
            configNpcText = section.getString("npc_text", configNpcText).replace('&', '§');
            configNpcTextReset = section.getString("npc_text_reset", configNpcTextReset).replace('&', '§');
            configOptionWrap = section.getString("option_wrap", configOptionWrap).replace('&', '§');
            configOptionText = section.getString("option_text", configOptionText).replace('&', '§');
            configOptionTextReset = section.getString("option_text_reset", configOptionTextReset).replace('&', '§');
            configOptionSelected = section.getString("option_selected", configOptionSelected).replace('&', '§');
            configOptionSelectedReset = section.getString("option_selected_reset", configOptionSelectedReset).replace('&', '§');
            configOptionSelectedWrap = section.getString("option_selected_wrap", configOptionWrap).replace('&', '§');
            configControlCancel = section.getString("control_cancel", configControlCancel);
            configControlSelect = section.getString("control_select", configControlSelect);
            configControlMove = section.getString("control_move", configControlMove);
            configNpcNameType = section.getString("npc_name_type", configNpcNameType);
            configNpcNameAlign = section.getString("npc_name_align", configNpcNameAlign);
            configNpcNameFormat = section.getString("npc_name_format", configNpcNameFormat).replace('&', '§');
        }

        // Sort out Controls
        try {
            for (CONTROL control : Arrays.stream(configControlCancel.split(","))
                    .map(String::toUpperCase)
                    .map(CONTROL::valueOf)
                    .collect(Collectors.toList())) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.CANCEL);
                }
            }
        } catch (IllegalArgumentException e) {
            Debug.error(conv.getPackage().getName() + ": Invalid data for 'control_cancel': " + configControlCancel);
        }
        try {
            for (CONTROL control : Arrays.stream(configControlSelect.split(","))
                    .map(String::toUpperCase)
                    .map(CONTROL::valueOf)
                    .collect(Collectors.toList())) {

                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.SELECT);
                }
            }
        } catch (IllegalArgumentException e) {
            Debug.error(conv.getPackage().getName() + ": Invalid data for 'control_select': " + configControlSelect);
        }
        try {
            for (CONTROL control : Arrays.stream(configControlMove.split(","))
                    .map(String::toUpperCase)
                    .map(CONTROL::valueOf)
                    .collect(Collectors.toList())) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.MOVE);
                }
            }
        } catch (IllegalArgumentException e) {
            Debug.error(conv.getPackage().getName() + ": Invalid data for 'control_move': " + configControlMove);
        }

        // Create something painful looking for the player to sit on and make it invisible.
        stand = new WrapperPlayServerSpawnEntityLiving();
        stand.setType(EntityType.ARMOR_STAND);
        stand.setEntityID(7777);
        stand.setX(player.getLocation().getX());
        stand.setY(player.getLocation().getY() - 1.1);
        stand.setZ(player.getLocation().getZ());

        stand.sendPacket(player);

        // Make it invisible
        WrapperPlayServerEntityMetadata wdw = new WrapperPlayServerEntityMetadata();
        wdw.setEntityId(stand.getEntityID());
        wdw.setMetadata(Collections.singletonList(new WrappedWatchableObject(0, (byte) 0x20)));
        wdw.sendPacket(player);

        // Mount the player to it
        WrapperPlayServerAttachEntity mount = new WrapperPlayServerAttachEntity();
        mount.setEntityId(player.getEntityId());
        mount.setVehicleId(stand.getEntityID());
        mount.sendPacket(player);

        // Display Actionbar to hide the dismount message
        WrapperPlayServerChat wpc = new WrapperPlayServerChat();
        wpc.setChatType(EnumWrappers.ChatType.GAME_INFO);
        wpc.setPosition((byte) 2);
        wpc.setMessage(WrappedChatComponent.fromText("Talking"));
        wpc.sendPacket(player);

        // Intercept Packets
        packetAdapter = new PacketAdapter(BetonQuest.getInstance().getJavaPlugin(), ListenerPriority.HIGHEST,
                PacketType.Play.Client.STEER_VEHICLE,
                PacketType.Play.Server.ANIMATION
        ) {

            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType().equals(PacketType.Play.Server.ANIMATION)) {
                    WrapperPlayServerAnimation animation = new WrapperPlayServerAnimation(event.getPacket());

                    if (animation.getEntityID() == player.getEntityId()) {
                        event.setCancelled(true);
                    }
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPlayer() != player || options.size() == 0) {
                    return;
                }

                if (event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)) {
                    WrapperPlayClientSteerVehicle steerEvent = new WrapperPlayClientSteerVehicle(event.getPacket());

                    if (steerEvent.isJump() && controls.containsKey(CONTROL.JUMP) && !debounce) {
                        // Player Jumped
                        debounce = true;

                        switch (controls.get(CONTROL.JUMP)) {
                            case CANCEL:
                                if (!conv.isMovementBlock()) {
                                    conv.endConversation();
                                }
                                break;
                            case SELECT:
                                conv.passPlayerAnswer(selectedOption + 1);
                                break;
                        }
                    } else if (steerEvent.getForward() < 0 && selectedOption < options.size() - 1 && controls.containsKey(CONTROL.MOVE) && !debounce) {
                        // Player moved Backwards
                        oldSelectedOption = selectedOption;
                        selectedOption++;
                        debounce = true;
                        updateDisplay();
                    } else if (steerEvent.getForward() > 0 && selectedOption > 0 && controls.containsKey(CONTROL.MOVE) && !debounce) {
                        // Player moved Forwards

                        oldSelectedOption = selectedOption;
                        selectedOption--;
                        debounce = true;
                        updateDisplay();

                    } else if (steerEvent.isUnmount() && controls.containsKey(CONTROL.SNEAK) && !debounce) {
                        // Player Dismounted
                        debounce = true;

                        switch (controls.get(CONTROL.SNEAK)) {
                            case CANCEL:
                                if (!conv.isMovementBlock()) {
                                    conv.endConversation();
                                }
                                break;
                            case SELECT:
                                conv.passPlayerAnswer(selectedOption + 1);
                                break;
                        }
                    } else if (Math.abs(steerEvent.getForward()) < 0.01) {
                        debounce = false;
                    }

                    event.setCancelled(true);
                    return;
                }

            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);

        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance().getJavaPlugin());
    }

    /**
     * Displays all data to the player. Should be called after setting all
     * options.
     */
    @Override
    public void display() {
        if (npcText == null && options.isEmpty()) {
            end();
            return;
        }

        updateDisplay();

        // Update the Display
        displayRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                showDisplay();

                if (ended) {
                    this.cancel();
                }
            }
        };

        displayRunnable.runTaskTimerAsynchronously(BetonQuest.getInstance().getJavaPlugin(), 0, 180);
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
        if (event.getPlayer() != player) {
            return;
        }

        // If the player has moved away somehow we cancel everything
        if (Math.abs(event.getFrom().getX() - event.getTo().getX()) + Math.abs(event.getFrom().getY() - event.getTo().getY()) + Math.abs(event.getFrom().getZ() - event.getTo().getZ()) > 3) {
            conv.endConversation();
        }
    }

    /**
     * Set the text of response chosen by the NPC. Should be called once per
     * conversation cycle.
     *
     * @param npcName  the name of the NPC
     * @param response the text the NPC chose
     */
    @Override
    public void setNpcResponse(String npcName, String response) {
        this.npcName = npcName;
        this.npcText = response;
        formattedNpcName = configNpcNameFormat
                .replace("{npc_name}", npcName);
    }

    /**
     * Adds the text of the player option. Should be called for each option in a
     * conversation cycle.
     *
     * @param option the text of an option
     */
    @Override
    public void addPlayerOption(String option) {
        options.add(option);
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getPlayer() != player) {
            return;
        }

        if (debounce) {
            return;
        }

        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:

                if (controls.containsKey(CONTROL.LEFT_CLICK)) {

                    switch (controls.get(CONTROL.LEFT_CLICK)) {
                        case CANCEL:
                            if (!conv.isMovementBlock()) {
                                conv.endConversation();
                            }
                            debounce = true;
                            break;
                        case SELECT:
                            conv.passPlayerAnswer(selectedOption + 1);
                            debounce = true;
                            break;
                    }
                }

                event.setCancelled(true);
        }

    }

    protected void showDisplay() {
        if (displayOutput != null) {
            player.spigot().sendMessage(TextComponent.fromLegacyText(displayOutput));
        }
    }

    protected void updateDisplay() {
        if (npcText == null) {
            displayOutput = null;
            return;
        }

        // NPC Text
        String msgNpcText = configNpcText
                .replace("{npc_text}", npcText)
                .replace("{npc_name}", npcName);

        List<String> npcLines = Arrays.stream(LocalChatPaginator.wordWrap(
                Utils.replaceReset(msgNpcText, configNpcTextReset), configLineLength, configNpcWrap))
                .collect(Collectors.toList());

        // Provide for as many options as we can fit but if there is lots of npcLines we will reduce this as necessary down to a minimum of 1.
        int linesAvailable = Math.max(1, 10 - npcLines.size());

        if (configNpcNameType.equals("chat")) {
            linesAvailable = Math.max(1, linesAvailable - 1);
        }

        // Add space for the up/down arrows
        if (options.size() > 0) {
            linesAvailable = Math.max(1, linesAvailable - 2);
        }

        // Displaying options is tricky. We need to deal with if the selection has moved, multi-line options and less space for all options due to npc text
        List<String> optionsSelected = new ArrayList<>();
        int currentOption = selectedOption;
        int currentDirection = selectedOption != oldSelectedOption ? selectedOption - oldSelectedOption : 1;
        int topOption = options.size();
        for (int i = 0; i < options.size() && linesAvailable > (i < 2 ? 0 : 1); i++) {
            int optionIndex = currentOption + (i * currentDirection);
            if (optionIndex > options.size() - 1) {
                optionIndex = currentOption - (optionIndex - (options.size() - 1));
                currentDirection = -currentDirection;
                if (optionIndex < 0) {
                    break;
                }
            } else if (optionIndex < 0) {
                optionIndex = currentOption + (0 - optionIndex);
                if (optionIndex > options.size() - 1) {
                    break;
                }
                currentDirection = -currentDirection;
            }

            if (topOption > optionIndex) {
                topOption = optionIndex;
            }

            List<String> optionLines;

            if (i == 0) {
                String optionText = configOptionSelected
                        .replace("{option_text}", options.get(optionIndex))
                        .replace("{npc_name}", npcName);

                optionLines = Arrays.stream(LocalChatPaginator.wordWrap(
                        Utils.replaceReset(optionText, i == 0 ? configOptionSelectedReset : configOptionTextReset),
                        configLineLength, configOptionSelectedWrap))
                        .collect(Collectors.toList());


            } else {
                String optionText = configOptionText
                        .replace("{option_text}", options.get(optionIndex))
                        .replace("{npc_name}", npcName);

                optionLines = Arrays.stream(LocalChatPaginator.wordWrap(
                        Utils.replaceReset(optionText, i == 0 ? configOptionSelectedReset : configOptionTextReset),
                        configLineLength, configOptionWrap))
                        .collect(Collectors.toList());

            }

            if (linesAvailable < optionLines.size()) {
                break;
            }

            linesAvailable -= optionLines.size();

            if (currentDirection > 0) {
                optionsSelected.add(String.join("\n", optionLines));
            } else {
                optionsSelected.add(0, String.join("\n", optionLines));
            }

            currentOption = optionIndex;
            currentDirection = -currentDirection;
        }

        // Build the displayOutput
        StringBuilder displayBuilder = new StringBuilder();

        if (options.size() > 0) {

            // Put clear lines in buffer, but this may cause flicker so consider removing
            for (int i = 0; i < 10; i++) {
                displayBuilder.append(" \n");
            }

            // If NPC name type is chat_top, show it
            if (configNpcNameType.equals("chat")) {
                switch (configNpcNameAlign) {
                    case "right":
                        for (int i = 0; i < Math.max(0, configLineLength - npcName.length()); i++) {
                            displayBuilder.append(" ");
                        }
                        break;
                    case "center":
                    case "middle":
                        for (int i = 0; i < Math.max(0, (configLineLength / 2) - npcName.length() / 2); i++) {
                            displayBuilder.append(" ");
                        }
                        break;
                }
                displayBuilder.append(formattedNpcName).append("\n");
            }

            // We aim to try have a blank line at the top. It looks better
            if (linesAvailable > 0) {
                displayBuilder.append(" \n");
                linesAvailable--;
            }

            displayBuilder.append(String.join("\n", npcLines)).append("\n");

            // Put clear lines between NPC text and Options
            for (int i = 0; i < linesAvailable; i++) {
                displayBuilder.append(" \n");
            }

            if (topOption > 0) {
                displayBuilder
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.WHITE).append("↑\n");
            } else {
                displayBuilder.append(" \n");
            }

            displayBuilder.append(String.join("\n", optionsSelected)).append("\n");

            if (topOption + optionsSelected.size() < options.size()) {
                displayBuilder
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.BOLD).append(" ")
                        .append(ChatColor.WHITE).append("↓");
            }
        } else {
            // Put clear lines above NPC Text
            for (int i = 0; i < 90 + linesAvailable - 1; i++) {
                displayBuilder.append(" \n");
            }

            displayBuilder.append(String.join("\n", npcLines)).append("\n");

            if (linesAvailable > 0) {
                displayBuilder.append(" \n");
            }
        }

        displayOutput = displayBuilder.toString();

        showDisplay();
    }

    /**
     * Clears the data. Should be called before the cycle begins to ensure
     * nothing is left from previous one.
     */
    @Override
    public void clear() {
        if (displayRunnable != null) {
            displayRunnable.cancel();
            displayRunnable = null;
        }

        selectedOption = 0;
        oldSelectedOption = 0;

        options.clear();
        npcText = null;

//        // Clear conversation
//        for (int i = 0; i < 100; i++) {
//            player.sendMessage(" \n");
//        }
    }

    /**
     * Ends the work of this conversation IO. Should be called when the
     * conversation ends.
     */
    @Override
    public void end() {
        ended = true;

        // Stop Listening for Packets
        ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);

        // Destroy Stand
        WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
        destroyPacket.setEntities(new int[]{stand.getEntityID()});
        destroyPacket.sendPacket(player);

        // Stop updating display
        if (displayRunnable != null) {
            displayRunnable.cancel();
            displayRunnable = null;
        }

        HandlerList.unregisterAll(this);
    }

    /**
     * @return if this conversationIO should send messages to the player when the conversation starts and ends
     */
    @Override
    public boolean printMessages() {
        return false;
    }

    /**
     * Send message through ConversationIO
     *
     * @param message
     */
    @Override
    public void print(String message) {
        if (message != null && message.length() > 0) {
            player.sendMessage(message);
        }
    }

    @EventHandler
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() != player) {
            return;
        }

        if (debounce) {
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {

            if (controls.containsKey(CONTROL.LEFT_CLICK)) {

                switch (controls.get(CONTROL.LEFT_CLICK)) {
                    case CANCEL:
                        if (!conv.isMovementBlock()) {
                            conv.endConversation();
                        }
                        debounce = true;
                        break;
                    case SELECT:
                        conv.passPlayerAnswer(selectedOption + 1);
                        debounce = true;
                        break;
                }
            }

            event.setCancelled(true);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void playerItemHeldEvent(PlayerItemHeldEvent event) {
        if (event.getPlayer() != player) {
            return;
        }

        if (!controls.containsKey(CONTROL.SCROLL)) {
            return;
        }

        // Cheat and assume the closest distance between previous and new slots is the direction scrolled
        int slotDistance = event.getPreviousSlot() - event.getNewSlot();

        if (slotDistance > 5 || (slotDistance < 0 && slotDistance >= -5)) {
            // Scrolled down
            if (selectedOption < options.size() - 1) {
                oldSelectedOption = selectedOption;
                selectedOption++;
                updateDisplay();
            }
        } else if (slotDistance != 0) {
            // Scrolled up
            if (selectedOption > 0) {
                oldSelectedOption = selectedOption;
                selectedOption--;
                updateDisplay();
            }
        }
    }

    public enum ACTION {
        SELECT,
        CANCEL,
        MOVE
    }

    public enum CONTROL {
        JUMP,
        SNEAK,
        SCROLL,
        MOVE,
        LEFT_CLICK
    }

    public enum NAME_TYPE {
        NONE,
        CHAT
    }

}
