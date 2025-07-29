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
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.protocollib.conversation.display.Display;
import org.betonquest.betonquest.compatibility.protocollib.conversation.display.Scroll;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.WrapperPlayClientSteerVehicleUpdated;
import org.betonquest.betonquest.conversation.ChatConvIO;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An {@link ChatConvIO} implementation that use player ingame movements to control the conversation.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyMethods", "PMD.CouplingBetweenObjects"})
public class MenuConvIO extends ChatConvIO {
    /**
     * Thread safety.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * All players that are currently on cooldown are in this list.
     * The cooldown is used to prevent players from spamming through the conversation or skipping through it by accident.
     */
    private final List<Player> selectionCooldowns = new ArrayList<>();

    /**
     * The settings for this conversation IO.
     */
    private final MenuConvIOSettings settings;

    /**
     * The component line wrapper to use for the conversation.
     */
    private final FixedComponentLineWrapper componentLineWrapper;

    /**
     * The controls that are used in the conversation.
     */
    protected Map<CONTROL, ACTION> controls = new EnumMap<>(CONTROL.class);

    /**
     * The current state of the conversation.
     */
    @SuppressWarnings("PMD.AvoidUsingVolatile")
    protected volatile ConversationState state = ConversationState.CREATED;

    /**
     * The packet adapter used to intercept packets.
     */
    protected PacketAdapter packetAdapter;

    /**
     * The runnable that updates the display.
     */
    @Nullable
    protected BukkitRunnable displayRunnable;

    /**
     * The display used to show the conversation.
     */
    @Nullable
    protected Display chatDisplay;

    /**
     * The armor stand used to steer the conversation.
     */
    @Nullable
    private ArmorStand stand;

    /**
     * Creates a new MenuConvIO instance.
     *
     * @param conv                 the conversation this IO is part of
     * @param onlineProfile        the online profile of the player participating in the conversation
     * @param colors               the colors used in the conversation
     * @param settings             the settings for the conversation IO
     * @param componentLineWrapper the component line wrapper to use for the conversation
     */
    @SuppressWarnings("NullAway.Init")
    public MenuConvIO(final Conversation conv, final OnlineProfile onlineProfile, final ConversationColors colors,
                      final MenuConvIOSettings settings, final FixedComponentLineWrapper componentLineWrapper) {
        super(conv, onlineProfile, colors);
        this.settings = settings;
        this.componentLineWrapper = componentLineWrapper;
        final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

        // Sort out Controls
        try {
            for (final CONTROL control : Arrays.stream(settings.controlCancel().split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf).toList()) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.CANCEL);
                }
            }
        } catch (final IllegalArgumentException e) {
            log.warn(conv.getPackage(), conv.getPackage().getQuestPath() + ": Invalid data for 'control_cancel': " + settings.controlCancel(), e);
        }
        try {
            for (final CONTROL control : Arrays.stream(settings.controlSelect().split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf).toList()) {

                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.SELECT);
                }
            }
        } catch (final IllegalArgumentException e) {
            log.warn(conv.getPackage(), conv.getPackage().getQuestPath() + ": Invalid data for 'control_select': " + settings.controlSelect(), e);
        }
        try {
            for (final CONTROL control : Arrays.stream(settings.controlMove().split(","))
                    .map(string -> string.toUpperCase(Locale.ROOT))
                    .map(CONTROL::valueOf).toList()) {
                if (!controls.containsKey(control)) {
                    controls.put(control, ACTION.MOVE);
                }
            }
        } catch (final IllegalArgumentException e) {
            log.warn(conv.getPackage(), conv.getPackage().getQuestPath() + ": Invalid data for 'control_move': " + settings.controlMove(), e);
        }
    }

    @SuppressWarnings("deprecation")
    private void start() {
        if (state.isStarted()) {
            return;
        }

        lock.lock();
        try {
            if (state.isStarted()) {
                return;
            }
            state = ConversationState.ACTIVE;

            final Player player = onlineProfile.getPlayer();
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
            player.sendActionBar(Component.empty());

            // Intercept Packets
            packetAdapter = getPacketAdapter();
            ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);

            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        } finally {
            lock.unlock();
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
        if (Component.empty().equals(npcText) && options.isEmpty()) {
            end(() -> {
            });
            return;
        }

        if (!options.isEmpty()) {
            start();
        }

        updateDisplay();
        if (settings.refreshDelay() > 0) {
            displayRunnable = new BukkitRunnable() {

                @Override
                public void run() {
                    updateDisplay();

                    if (state.isEnded()) {
                        this.cancel();
                    }
                }
            };
            displayRunnable.runTaskTimerAsynchronously(BetonQuest.getInstance(), settings.refreshDelay(), settings.refreshDelay());
        }
    }

    // Override this event from our parent
    @SuppressWarnings("deprecation")
    @Override
    @EventHandler(ignoreCancelled = true)
    public void onReply(final AsyncPlayerChatEvent event) {
        // Empty
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

        chatDisplay = null;

        super.clear();
    }

    /**
     * Ends the work of this conversation IO. Should be called when the
     * conversation ends.
     */
    @Override
    public void end(final Runnable callback) {
        if (state.isEnded()) {
            return;
        }
        lock.lock();
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

            super.end(callback);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
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
                if (animation.getEntityID() == onlineProfile.getPlayer().getEntityId()) {
                    event.setCancelled(true);
                }
            }

            @Override
            public void onPacketReceiving(final PacketEvent event) {
                if (!event.getPlayer().equals(onlineProfile.getPlayer()) || options.isEmpty()) {
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

                if (steerEvent.isJump() && controls.containsKey(CONTROL.JUMP)) {
                    // Player Jumped
                    switch (controls.get(CONTROL.JUMP)) {
                        case CANCEL:
                            if (!conv.isMovementBlock()) {
                                conv.endConversation();
                            }
                            break;
                        case SELECT:
                            lock.lock();
                            try {
                                passPlayerAnswer();
                            } finally {
                                lock.unlock();
                            }
                            break;
                        case MOVE:
                            break;
                    }
                } else if (steerEvent.getForward() < 0 && controls.containsKey(CONTROL.MOVE)) {
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> MenuConvIO.this.updateDisplay(Scroll.DOWN));
                } else if (steerEvent.getForward() > 0 && controls.containsKey(CONTROL.MOVE)) {
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> MenuConvIO.this.updateDisplay(Scroll.UP));
                } else if (steerEvent.isUnmount() && controls.containsKey(CONTROL.SNEAK)) {
                    switch (controls.get(CONTROL.SNEAK)) {
                        case CANCEL:
                            if (!conv.isMovementBlock()) {
                                conv.endConversation();
                            }
                            break;
                        case SELECT:
                            lock.lock();
                            try {
                                if (!isOnCooldown()) {
                                    passPlayerAnswer();
                                }
                            } finally {
                                lock.unlock();
                            }
                            break;
                        case MOVE:
                            break;
                    }
                }
                event.setCancelled(true);
            }
        };
    }

    private void passPlayerAnswer() {
        if (chatDisplay == null || isOnCooldown()) {
            return;
        }
        chatDisplay.getSelection().ifPresent(index -> conv.passPlayerAnswer(index + 1));
    }

    /**
     * Handles the player interact event.
     *
     * @param event the event
     */
    @SuppressWarnings("PMD.CollapsibleIfStatements")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerInteractEvent(final PlayerInteractEvent event) {
        if (state.isInactive()) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
                return;
            }

            if (!event.getPlayer().equals(onlineProfile.getPlayer())) {
                return;
            }

            event.setCancelled(true);

            final Action action = event.getAction();
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                if (controls.containsKey(CONTROL.LEFT_CLICK)) {
                    handleSteering(controls.get(CONTROL.LEFT_CLICK));
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Handles the player interact entity event.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerInteractEntityEvent(final PlayerInteractEntityEvent event) {
        if (state.isInactive()) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
                return;
            }

            if (!event.getPlayer().equals(onlineProfile.getPlayer())) {
                return;
            }

            event.setCancelled(true);

            if (controls.containsKey(CONTROL.LEFT_CLICK)) {
                handleSteering(controls.get(CONTROL.LEFT_CLICK));
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Handles the entity damage by entity event.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void entityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
        if (state.isInactive()) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
                return;
            }

            if (!event.getDamager().equals(onlineProfile.getPlayer())) {
                return;
            }

            event.setCancelled(true);

            if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && controls.containsKey(CONTROL.LEFT_CLICK)) {
                handleSteering(controls.get(CONTROL.LEFT_CLICK));
            }
        } finally {
            lock.unlock();
        }
    }

    private void handleSteering(final ACTION action) {
        switch (action) {
            case CANCEL -> {
                if (!conv.isMovementBlock()) {
                    conv.endConversation();
                }
            }
            case SELECT -> {
                if (!isOnCooldown()) {
                    passPlayerAnswer();
                }
            }
            default -> {
            }
        }
    }

    private boolean isOnCooldown() {
        final Player player = onlineProfile.getPlayer();
        if (selectionCooldowns.contains(player)) {
            return true;
        } else {
            selectionCooldowns.add(player);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(BetonQuest.getInstance(), () -> selectionCooldowns.remove(player), settings.rateLimit());
        }
        return false;
    }

    /**
     * Handles the player item held event.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerItemHeldEvent(final PlayerItemHeldEvent event) {
        if (state.isInactive()) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
                return;
            }

            if (!event.getPlayer().equals(onlineProfile.getPlayer())) {
                return;
            }

            if (!controls.containsKey(CONTROL.SCROLL)) {
                return;
            }

            event.setCancelled(true);

            updateDisplay(getScrollDirection(event.getPreviousSlot(), event.getNewSlot()));
        } finally {
            lock.unlock();
        }
    }

    private void updateDisplay() {
        updateDisplay(Scroll.NONE);
    }

    private void updateDisplay(final Scroll scroll) {
        if (chatDisplay == null) {
            chatDisplay = new Display(settings, componentLineWrapper, npcName, npcText, new ArrayList<>(options.values()));
        }
        conv.sendMessage(chatDisplay.getDisplay(scroll));
    }

    private Scroll getScrollDirection(final int start, final int end) {
        for (int offset = 1; offset <= 4; offset++) {
            if ((start + offset) % 9 == end) {
                return Scroll.DOWN;
            }
        }
        return Scroll.UP;
    }

    /**
     * The actions that can be performed in the menu conversation.
     */
    public enum ACTION {
        /**
         * The player selected an option.
         */
        SELECT,
        /**
         * The player cancelled the conversation.
         */
        CANCEL,
        /**
         * The player moved in the conversation.
         */
        MOVE
    }

    /**
     * The controls that can be used in the menu conversation.
     */
    public enum CONTROL {
        /**
         * The player jumped.
         */
        JUMP,
        /**
         * The player sneaked.
         */
        SNEAK,
        /**
         * The player scrolled.
         */
        SCROLL,
        /**
         * The player moved.
         */
        MOVE,
        /**
         * The player left-clicked.
         */
        LEFT_CLICK
    }
}
