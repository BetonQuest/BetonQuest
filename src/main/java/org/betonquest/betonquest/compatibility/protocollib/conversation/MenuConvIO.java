package org.betonquest.betonquest.compatibility.protocollib.conversation;

import com.comphenix.packetwrapper.WrapperPlayClientSteerVehicle;
import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerMount;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.WrapperPlayClientSteerVehicleUpdated;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.conversation.ChatConvIO;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationState;
import org.betonquest.betonquest.util.LocalChatPaginator;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyFields", "PMD.TooManyMethods",
        "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.CouplingBetweenObjects"})
public class MenuConvIO extends ChatConvIO {
    /**
     * The type of NPC name to display in the conversation.
     */
    private static final String NPC_NAME_TYPE_CHAT = "chat";

    /**
     * Thread safety
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * All players that are currently on cooldown are in this list.
     * The cooldown is used to prevent players from spamming through the conversation or skipping through it by accident.
     */
    private final List<Player> selectionCooldowns = new ArrayList<>();

    // Actions
    protected Map<CONTROL, ACTION> controls = new EnumMap<>(CONTROL.class);

    protected String configControlCancel = "sneak";

    protected int oldSelectedOption;

    protected int selectedOption;

    @SuppressWarnings("PMD.AvoidUsingVolatile")
    protected volatile ConversationState state = ConversationState.CREATED;

    protected PacketAdapter packetAdapter;

    @Nullable
    protected BukkitRunnable displayRunnable;

    protected boolean debounce;

    @Nullable
    protected BaseComponent[] displayOutput;

    protected String formattedNpcName;

    protected String configControlSelect = "jump,left_click";

    // Configuration
    protected Integer configStartNewLines = 10;

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

    protected boolean configNpcNameNewlineSeparator = true;

    protected boolean configNpcTextFillNewLines = true;

    /**
     * The amount of ticks a player must wait before selecting another option after selecting an option.
     */
    private int configSelectionCooldown = 10;

    @Nullable
    private ArmorStand stand;

    @SuppressWarnings({"PMD.CognitiveComplexity", "NullAway.Init"})
    public MenuConvIO(final Conversation conv, final OnlineProfile onlineProfile) {
        super(conv, onlineProfile);
        final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

        for (final QuestPackage pack : Stream.concat(
                Config.getPackages().values().stream().filter(p -> !p.equals(conv.getPackage())),
                Stream.of(conv.getPackage())).toList()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("menu_conv_io");
            if (section == null) {
                continue;
            }

            configStartNewLines = section.getInt("start_new_lines", configStartNewLines);
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
            configNpcNameNewlineSeparator = section.getBoolean("npc_name_newline_separator", configNpcNameNewlineSeparator);
            configNpcTextFillNewLines = section.getBoolean("npc_text_fill_new_lines", configNpcTextFillNewLines);
            configSelectionCooldown = section.getInt("selectionCooldown");
        }

        // Sort out Controls
        try {
            for (final CONTROL control : Arrays.stream(configControlCancel.split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf).toList()) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.CANCEL);
                }
            }
        } catch (final IllegalArgumentException e) {
            log.warn(conv.getPackage(), conv.getPackage().getQuestPath() + ": Invalid data for 'control_cancel': " + configControlCancel, e);
        }
        try {
            for (final CONTROL control : Arrays.stream(configControlSelect.split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf).toList()) {

                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.SELECT);
                }
            }
        } catch (final IllegalArgumentException e) {
            log.warn(conv.getPackage(), conv.getPackage().getQuestPath() + ": Invalid data for 'control_select': " + configControlSelect, e);
        }
        try {
            for (final CONTROL control : Arrays.stream(configControlMove.split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf).toList()) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.MOVE);
                }
            }
        } catch (final IllegalArgumentException e) {
            log.warn(conv.getPackage(), conv.getPackage().getQuestPath() + ": Invalid data for 'control_move': " + configControlMove, e);
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

            final Location target = getBlockBelowPlayer(player).add(0, -1, 0);
            // TODO version switch:
            //  Remove this code when only 1.20.2+ is supported
            stand = player.getWorld().spawn(target.add(0, PaperLib.isVersion(20, 2) ? -0.375 : -0.131_25, 0), ArmorStand.class);

            stand.setGravity(false);
            stand.setVisible(false);
            final AttributeInstance attribute = stand.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                attribute.setBaseValue(0);
            }

            // Mount the player to it using packets
            final WrapperPlayServerMount mount = new WrapperPlayServerMount();
            mount.setEntityID(stand.getEntityId());
            mount.setPassengerIds(new int[]{player.getEntityId()});

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

    /**
     * Gets the location on the top of the block below the player.
     * This prevents the conversation's steering armor stand from spawning in the air.
     * <p>
     * This is done by getting the bounding box of the player.
     * Then all bounding boxes of the blocks in the bounding box of the player are checked for collision.
     * The highest collision is then returned.
     * If no collision is found the process is repeated with the player bounding box shifted down by 1.
     *
     * @param player the player to get the location for
     * @return the location on the top of the block below the player
     */
    private Location getBlockBelowPlayer(final Player player) {
        if (player.isFlying()) {
            return player.getLocation();
        }

        final BoundingBox playerBoundingBox = player.getBoundingBox();
        playerBoundingBox.shift(0, -(playerBoundingBox.getMinY() % 1), 0);
        while (playerBoundingBox.getMinY() >= player.getWorld().getMinHeight()) {
            final Set<Block> blocks = getBlocksInBoundingBox(player.getWorld(), playerBoundingBox);

            final List<BoundingBox> boundingBoxes = blocks.stream()
                    .map(block -> block.getCollisionShape().getBoundingBoxes().stream()
                            .map(box -> box.shift(block.getLocation())).toList())
                    .flatMap(Collection::stream)
                    .filter(box -> box.overlaps(playerBoundingBox))
                    .toList();

            if (!boundingBoxes.isEmpty()) {
                final Optional<Double> maxY = boundingBoxes.stream()
                        .map(BoundingBox::getMaxY)
                        .max(Double::compareTo);
                final Location location = player.getLocation();
                location.setY(maxY.get());
                return location;
            }
            playerBoundingBox.shift(0, -1, 0);
        }
        return player.getLocation();
    }

    /**
     * Get the blocks that are at the bottom corners of the player's bounding box.
     * This could be 1, 2 or 4 blocks depending on the player's position.
     *
     * @param world             the world the player is in
     * @param playerBoundingBox the bounding box of the player
     * @return the blocks in the bounding box
     */
    private Set<Block> getBlocksInBoundingBox(final World world, final BoundingBox playerBoundingBox) {
        final Set<Block> blocks = new HashSet<>();
        blocks.add(new Location(world, playerBoundingBox.getMinX(), playerBoundingBox.getMinY(), playerBoundingBox.getMinZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMinX(), playerBoundingBox.getMinY(), playerBoundingBox.getMaxZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMaxX(), playerBoundingBox.getMinY(), playerBoundingBox.getMinZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMaxX(), playerBoundingBox.getMinY(), playerBoundingBox.getMaxZ()).getBlock());
        return blocks;
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

    protected void showDisplay() {
        if (displayOutput != null) {
            conv.sendMessage(displayOutput);
        }
    }

    @SuppressWarnings({"PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity", "PMD.ConsecutiveLiteralAppends"})
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
                .toList();

        // Provide for as many options as we can fit but if there is lots of npcLines we will reduce this as necessary
        // own to a minimum of 1.
        int linesAvailable = Math.max(1, 10 - npcLines.size());

        if (NPC_NAME_TYPE_CHAT.equals(configNpcNameType)) {
            linesAvailable = Math.max(1, linesAvailable - 1);
        }

        // Add space for the up/down arrows
        if (!options.isEmpty()) {
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
                        configLineLength, configOptionSelectedWrap)).toList();
            } else {
                final String optionText = configOptionText
                        .replace("{option_text}", options.get(optionIndex + 1))
                        .replace("{npc_name}", npcName);

                optionLines = Arrays.stream(LocalChatPaginator.wordWrap(
                        Utils.replaceReset(StringUtils.stripEnd(optionText, "\n"), configOptionTextReset),
                        configLineLength, configOptionWrap)).toList();
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
        displayBuilder.append(" \n".repeat(configStartNewLines));

        // If NPC name type is chat_top, show it
        if (NPC_NAME_TYPE_CHAT.equals(configNpcNameType)) {
            switch (configNpcNameAlign) {
                case "right":
                    displayBuilder.append(" ".repeat(Math.max(0, configLineLength - npcName.length())));
                    break;
                case "center":
                case "middle":
                    displayBuilder.append(" ".repeat(Math.max(0, configLineLength / 2 - npcName.length() / 2)));
                    break;
                default:
                    break;
            }
            displayBuilder.append(formattedNpcName).append('\n');
        }

        // We aim to try have a blank line at the top. It looks better
        if (configNpcNameNewlineSeparator && linesAvailable > 0) {
            displayBuilder.append(" \n");
            linesAvailable--;
        }

        displayBuilder.append(String.join("\n", npcLines)).append('\n');
        if (configNpcTextFillNewLines) {
            displayBuilder.append(" \n".repeat(linesAvailable));
        } else {
            displayBuilder.insert(0, " \n".repeat(linesAvailable));
        }

        if (!options.isEmpty()) {
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
                    if (stand != null) {
                        stand.remove();
                        stand = null;
                    }
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

    @SuppressWarnings({"PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
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
                if (!event.getPlayer().equals(player) || options.isEmpty()) {
                    return;
                }
                if (!event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)) {
                    return;
                }

                final WrapperPlayClientSteerVehicle steerEvent;

                if (PaperLib.isVersion(21, 3)) {
                    steerEvent = new WrapperPlayClientSteerVehicleUpdated(event.getPacket());
                } else {
                    steerEvent = new WrapperPlayClientSteerVehicle(event.getPacket());
                }

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
                            if (!isOnCooldown()) {
                                conv.passPlayerAnswer(selectedOption + 1);
                            }
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
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), MenuConvIO.this::updateDisplay);
                } else if (steerEvent.getForward() > 0 && selectedOption > 0 && controls.containsKey(CONTROL.MOVE) && !debounce) {
                    // Player moved Forwards
                    oldSelectedOption = selectedOption;
                    selectedOption--;
                    debounce = true;
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), MenuConvIO.this::updateDisplay);
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
                            if (!isOnCooldown()) {
                                conv.passPlayerAnswer(selectedOption + 1);
                            }
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
     * @return if this conversationIO should send messages to the player when the conversation starts and ends
     */
    @Override
    public boolean printMessages() {
        return false;
    }

    @SuppressWarnings("PMD.CollapsibleIfStatements")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerInteractEvent(final PlayerInteractEvent event) {
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

            event.setCancelled(true);

            if (debounce) {
                return;
            }

            final Action action = event.getAction();
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                if (controls.containsKey(CONTROL.LEFT_CLICK)) {
                    handleSteering(controls.get(CONTROL.LEFT_CLICK));
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerInteractEntityEvent(final PlayerInteractEntityEvent event) {
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

            event.setCancelled(true);

            if (debounce) {
                return;
            }

            if (controls.containsKey(CONTROL.LEFT_CLICK)) {
                handleSteering(controls.get(CONTROL.LEFT_CLICK));
            }
        } finally {
            lock.readLock().unlock();
        }
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
                handleSteering(controls.get(CONTROL.LEFT_CLICK));
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private void handleSteering(final ACTION action) {
        switch (action) {
            case CANCEL -> {
                if (!conv.isMovementBlock()) {
                    conv.endConversation();
                }
                debounce = true;
            }
            case SELECT -> {
                if (isOnCooldown()) {
                    return;
                }
                conv.passPlayerAnswer(selectedOption + 1);
                debounce = true;
            }
            default -> {
            }
        }
    }

    private boolean isOnCooldown() {
        if (selectionCooldowns.contains(player)) {
            return true;
        } else {
            selectionCooldowns.add(player);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(BetonQuest.getInstance(), () -> selectionCooldowns.remove(player), configSelectionCooldown);
        }
        return false;
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

            final Direction scrollDirection = getScrollDirection(event.getPreviousSlot(), event.getNewSlot());

            if (scrollDirection == Direction.DOWN && selectedOption < options.size() - 1) {
                oldSelectedOption = selectedOption;
                selectedOption++;
                updateDisplay();
                debounce = true;
            } else if (scrollDirection == Direction.UP && selectedOption > 0) {
                oldSelectedOption = selectedOption;
                selectedOption--;
                updateDisplay();
                debounce = true;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private Direction getScrollDirection(final int start, final int end) {
        for (int offset = 1; offset <= 4; offset++) {
            if ((start + offset) % 9 == end) {
                return Direction.DOWN;
            }
        }
        return Direction.UP;
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

    public enum Direction {
        @SuppressWarnings("PMD.ShortVariable")
        UP,
        DOWN
    }
}
