package pl.betoncraft.betonquest.compatibility.protocollib.conversation;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayClientSteerVehicle;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerAnimation;
import pl.betoncraft.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerMount;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.conversation.ChatConvIO;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.ConversationState;
import pl.betoncraft.betonquest.utils.LocalChatPaginator;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyFields", "PMD.TooManyMethods",
        "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public class MenuConvIO extends ChatConvIO {
    /**
     * Thread safety
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // Actions
    protected Map<CONTROL, ACTION> controls = new HashMap<>();
    protected String configControlCancel = "sneak";

    protected int oldSelectedOption;
    protected int selectedOption;
    @SuppressWarnings("PMD.AvoidUsingVolatile")
    protected volatile ConversationState state = ConversationState.CREATED;
    protected PacketAdapter packetAdapter;
    protected BukkitRunnable displayRunnable;
    protected boolean debounce;
    protected BaseComponent[] displayOutput;
    protected String formattedNpcName;
    protected String configControlSelect = "jump,left_click";

    // Configuration
    protected Integer configLineLength = 50;
    protected Integer configRefreshDelay = 180;
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
    private ArmorStand stand;

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public MenuConvIO(final Conversation conv, final String playerID) {
        super(conv, playerID);

        // Load Configuration from custom.yml with some sane defaults, loading our current package last
        for (final ConfigPackage pack : Stream.concat(
                Config.getPackages().values().stream().filter(p -> p != conv.getPackage()),
                Stream.of(conv.getPackage())).collect(Collectors.toList())) {
            final ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("menu_conv_io");
            if (section == null) {
                continue;
            }

            configLineLength = section.getInt("line_length", configLineLength);
            configRefreshDelay = section.getInt("refresh_delay", configRefreshDelay);
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
            for (final CONTROL control : Arrays.stream(configControlCancel.split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf)
                    .collect(Collectors.toList())) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.CANCEL);
                }
            }
        } catch (final IllegalArgumentException e) {
            LogUtils.getLogger().log(Level.WARNING, conv.getPackage().getName() + ": Invalid data for 'control_cancel': " + configControlCancel);
            LogUtils.logThrowable(e);
        }
        try {
            for (final CONTROL control : Arrays.stream(configControlSelect.split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf)
                    .collect(Collectors.toList())) {

                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.SELECT);
                }
            }
        } catch (final IllegalArgumentException e) {
            LogUtils.getLogger().log(Level.WARNING, conv.getPackage().getName() + ": Invalid data for 'control_select': " + configControlSelect);
            LogUtils.logThrowable(e);
        }
        try {
            for (final CONTROL control : Arrays.stream(configControlMove.split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf)
                    .collect(Collectors.toList())) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.MOVE);
                }
            }
        } catch (final IllegalArgumentException e) {
            LogUtils.getLogger().log(Level.WARNING, conv.getPackage().getName() + ": Invalid data for 'control_move': " + configControlMove);
            LogUtils.logThrowable(e);
        }
    }

    @SuppressWarnings("deprecation")
    private void start() {
        if (state.isStarted()) {
            return;
        }

        lock.writeLock().lock();
        try {
            if (state.isStarted()) {
                return;
            }
            state = ConversationState.ACTIVE;

            // Create something painful looking for the player to sit on and make it invisible.
            stand = player.getWorld().spawn(player.getLocation().clone().add(0, -1.1, 0), ArmorStand.class);

            stand.setGravity(false);
            stand.setVisible(false);

            // Mount the player to it using packets
            final WrapperPlayServerMount mount = new WrapperPlayServerMount();
            mount.setEntityID(stand.getEntityId());
            mount.setPassengerIds(player.getEntityId());

            // Send Packets
            mount.sendPacket(player);

            // Display Actionbar to hide the dismount message
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));

            // Intercept Packets
            packetAdapter = getPacketAdapter();
            ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);

            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition"})
    private PacketAdapter getPacketAdapter() {
        return new PacketAdapter(BetonQuest.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Client.STEER_VEHICLE,
                PacketType.Play.Server.ANIMATION
        ) {

            @Override
            public void onPacketSending(final PacketEvent event) {
                if (!event.getPacketType().equals(PacketType.Play.Server.ANIMATION)) {
                    return;
                }
                final WrapperPlayServerAnimation animation = new WrapperPlayServerAnimation(event.getPacket());
                if (animation.getEntityID() == player.getEntityId()) {
                    event.setCancelled(true);
                }
            }

            @Override
            public void onPacketReceiving(final PacketEvent event) {
                if (!event.getPlayer().equals(player) || options.size() == 0) {
                    return;
                }
                if (!event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)) {
                    return;
                }
                final WrapperPlayClientSteerVehicle steerEvent = new WrapperPlayClientSteerVehicle(event.getPacket());
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
                        case MOVE:
                        default:
                            break;
                    }
                } else if (steerEvent.getForward() < 0 && selectedOption < options.size() - 1 && controls.containsKey(CONTROL.MOVE) && !debounce) {
                    // Player moved Backwards
                    oldSelectedOption = selectedOption;
                    selectedOption++;
                    debounce = true;
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> updateDisplay());
                } else if (steerEvent.getForward() > 0 && selectedOption > 0 && controls.containsKey(CONTROL.MOVE) && !debounce) {
                    // Player moved Forwards
                    oldSelectedOption = selectedOption;
                    selectedOption--;
                    debounce = true;
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> updateDisplay());
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
                        case MOVE:
                        default:
                            break;
                    }
                } else if (Math.abs(steerEvent.getForward()) < 0.01) {
                    debounce = false;
                }
                event.setCancelled(true);
            }
        };
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

        // Only want to hook the player when there are player options
        if (!options.isEmpty()) {
            start();
        }

        // Update the Display automatically if configRefreshDelay is > 0
        if (configRefreshDelay > 0) {
            displayRunnable = new BukkitRunnable() {

                @Override
                public void run() {
                    showDisplay();

                    if (state.isEnded()) {
                        this.cancel();
                    }
                }
            };

            displayRunnable.runTaskTimerAsynchronously(BetonQuest.getInstance(), configRefreshDelay, configRefreshDelay);
        }

        updateDisplay();
    }

    // Override this event from our parent
    @SuppressWarnings("deprecation")
    @Override
    @EventHandler(ignoreCancelled = true)
    public void onReply(final AsyncPlayerChatEvent event) {
        // Empty
    }

    /**
     * Set the text of response chosen by the NPC. Should be called once per
     * conversation cycle.
     *
     * @param npcName  the name of the NPC
     * @param response the text the NPC chose
     */
    @Override
    public void setNpcResponse(final String npcName, final String response) {
        super.setNpcResponse(npcName, response);
        formattedNpcName = configNpcNameFormat
                .replace("{npc_name}", npcName);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerInteractEntityEvent(final PlayerInteractEntityEvent event) {
        if (!state.isActive()) {
            return;
        }

        lock.readLock().lock();
        try {
            if (!state.isActive()) {
                return;
            }

            if (!event.getPlayer().equals(player)) {
                return;
            }

            event.setCancelled(true);

            if (debounce) {
                return;
            }

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
                    case MOVE:
                    default:
                        break;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerInteractEvent(final PlayerInteractEvent event) {
        if (!state.isActive()) {
            return;
        }

        lock.readLock().lock();
        try {
            if (!state.isActive()) {
                return;
            }

            if (!event.getPlayer().equals(player)) {
                return;
            }

            event.setCancelled(true);

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
                            case MOVE:
                            default:
                                break;
                        }
                    }
                case PHYSICAL:
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                default:
                    break;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    protected void showDisplay() {
        if (displayOutput != null) {
            conv.sendMessage(displayOutput);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.NPathComplexity"})
    protected void updateDisplay() {
        if (npcText == null) {
            displayOutput = null;
            return;
        }

        // NPC Text
        final String msgNpcText = configNpcText
                .replace("{npc_text}", npcText)
                .replace("{npc_name}", npcName);

        final List<String> npcLines = Arrays.stream(LocalChatPaginator.wordWrap(
                        Utils.replaceReset(StringUtils.stripEnd(msgNpcText, "\n"), configNpcTextReset), configLineLength, configNpcWrap))
                .collect(Collectors.toList());

        // Provide for as many options as we can fit but if there is lots of npcLines we will reduce this as necessary
        // own to a minimum of 1.
        int linesAvailable = Math.max(1, 10 - npcLines.size());

        if ("chat".equals(configNpcNameType)) {
            linesAvailable = Math.max(1, linesAvailable - 1);
        }

        // Add space for the up/down arrows
        if (options.size() > 0) {
            linesAvailable = Math.max(1, linesAvailable - 2);
        }

        // Displaying options is tricky. We need to deal with if the selection has moved, multi-line options and less
        // pace for all options due to npc text
        final List<String> optionsSelected = new ArrayList<>();
        int currentOption = selectedOption;
        int currentDirection = selectedOption == oldSelectedOption ? 1 : selectedOption - oldSelectedOption;
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
                optionIndex = currentOption + (-optionIndex);
                if (optionIndex > options.size() - 1) {
                    break;
                }
                currentDirection = -currentDirection;
            }

            if (topOption > optionIndex) {
                topOption = optionIndex;
            }

            final List<String> optionLines;

            if (i == 0) {
                final String optionText = configOptionSelected
                        .replace("{option_text}", options.get(optionIndex + 1))
                        .replace("{npc_name}", npcName);

                optionLines = Arrays.stream(LocalChatPaginator.wordWrap(
                                Utils.replaceReset(StringUtils.stripEnd(optionText, "\n"), configOptionSelectedReset),
                                configLineLength, configOptionSelectedWrap))
                        .collect(Collectors.toList());


            } else {
                final String optionText = configOptionText
                        .replace("{option_text}", options.get(optionIndex + 1))
                        .replace("{npc_name}", npcName);

                optionLines = Arrays.stream(LocalChatPaginator.wordWrap(
                                Utils.replaceReset(StringUtils.stripEnd(optionText, "\n"), configOptionTextReset),
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
        final StringBuilder displayBuilder = new StringBuilder();

        // If NPC name type is chat_top, show it
        if ("chat".equals(configNpcNameType)) {
            switch (configNpcNameAlign) {
                case "right":
                    for (int i = 0; i < Math.max(0, configLineLength - npcName.length()); i++) {
                        displayBuilder.append(' ');
                    }
                    break;
                case "center":
                case "middle":
                    for (int i = 0; i < Math.max(0, configLineLength / 2 - npcName.length() / 2); i++) {
                        displayBuilder.append(' ');
                    }
                    break;
                default:
                    break;
            }
            displayBuilder.append(formattedNpcName).append('\n');
        }

        // We aim to try have a blank line at the top. It looks better
        if (linesAvailable > 0) {
            displayBuilder.append(" \n");
            linesAvailable--;
        }

        displayBuilder.append(String.join("\n", npcLines)).append('\n');

        // Put clear lines between NPC text and Options
        for (int i = 0; i < linesAvailable; i++) {
            displayBuilder.append(" \n");
        }

        if (options.size() > 0) {
            // Show up arrow if options exist above our view
            if (topOption > 0) {
                for (int i = 0; i < 8; i++) {
                    displayBuilder.append(ChatColor.BOLD).append(' ');
                }
                displayBuilder.append(ChatColor.WHITE).append("↑\n");
            } else {
                displayBuilder.append(" \n");
            }

            // Display Options
            displayBuilder.append(String.join("\n", optionsSelected)).append('\n');

            // Show down arrow if options exist below our view
            if (topOption + optionsSelected.size() < options.size()) {
                for (int i = 0; i < 8; i++) {
                    displayBuilder.append(ChatColor.BOLD).append(' ');
                }
                displayBuilder.append(ChatColor.WHITE).append("↓\n");
            } else {
                displayBuilder.append(" \n");
            }
        }

        displayOutput = TextComponent.fromLegacyText(StringUtils.stripEnd(displayBuilder.toString(), "\n"));

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

        super.clear();
    }

    /**
     * Ends the work of this conversation IO. Should be called when the
     * conversation ends.
     */
    @Override
    public void end() {
        if (state.isEnded()) {
            return;
        }
        lock.writeLock().lock();
        try {
            if (state.isEnded()) {
                return;
            }
            state = ConversationState.ENDED;

            if (packetAdapter != null) {
                ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);
            }
            if (stand != null) {
                Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
                    stand.remove();
                    stand = null;
                });
            }

            // Stop updating display
            if (displayRunnable != null) {
                displayRunnable.cancel();
                displayRunnable = null;
            }

            super.end();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @return if this conversationIO should send messages to the player when the conversation starts and ends
     */
    @Override
    public boolean printMessages() {
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void entityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
        if (state.isInactive()) {
            return;
        }

        lock.readLock().lock();
        try {
            if (state.isInactive()) {
                return;
            }

            if (!event.getDamager().equals(player)) {
                return;
            }

            event.setCancelled(true);

            if (debounce) {
                return;
            }

            if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && controls.containsKey(CONTROL.LEFT_CLICK)) {
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
                    case MOVE:
                    default:
                        break;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @SuppressWarnings("PMD.NPathComplexity")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerItemHeldEvent(final PlayerItemHeldEvent event) {
        if (state.isInactive()) {
            return;
        }

        lock.readLock().lock();
        try {
            if (state.isInactive()) {
                return;
            }

            if (!event.getPlayer().equals(player)) {
                return;
            }

            if (!controls.containsKey(CONTROL.SCROLL)) {
                return;
            }

            event.setCancelled(true);

            if (debounce) {
                return;
            }

            // Cheat and assume the closest distance between previous and new slots is the direction scrolled
            final int slotDistance = event.getPreviousSlot() - event.getNewSlot();

            if ((slotDistance > 5 || slotDistance < 0 && slotDistance >= -5) && selectedOption < options.size() - 1) {
                oldSelectedOption = selectedOption;
                selectedOption++;
                updateDisplay();
                debounce = true;
            } else if (slotDistance != 0 && selectedOption > 0) {
                oldSelectedOption = selectedOption;
                selectedOption--;
                updateDisplay();
                debounce = true;
            }
        } finally {
            lock.readLock().unlock();
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
}
