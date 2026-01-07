package org.betonquest.betonquest.conversation.menu;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.function.TriFunction;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.ChatConvIO;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationState;
import org.betonquest.betonquest.conversation.menu.display.Display;
import org.betonquest.betonquest.conversation.menu.display.Scroll;
import org.betonquest.betonquest.conversation.menu.input.ConversationAction;
import org.betonquest.betonquest.conversation.menu.input.ConversationSession;
import org.bukkit.Bukkit;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An {@link ChatConvIO} implementation that use player ingame movements to control the conversation.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.CouplingBetweenObjects"})
public class MenuConvIO extends ChatConvIO {

    /**
     * The controls that are used in the conversation.
     */
    protected final Map<CONTROL, ACTION> controls;

    /**
     * Thread safety.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Plugin instance to schedule tasks.
     */
    private final Plugin plugin;

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
     * The input object triggering actions.
     */
    private final ConversationSession input;

    /**
     * The current state of the conversation.
     */
    @SuppressWarnings("PMD.AvoidUsingVolatile")
    protected volatile ConversationState state = ConversationState.CREATED;

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
     * Creates a new MenuConvIO instance.
     *
     * @param inputFunction        the function to create the input object with actions
     * @param conv                 the conversation this IO is part of
     * @param onlineProfile        the online profile of the player participating in the conversation
     * @param colors               the colors used in the conversation
     * @param settings             the settings for the conversation IO
     * @param componentLineWrapper the component line wrapper to use for the conversation
     * @param plugin               the plugin instance to run tasks
     * @param controls             the used controls
     */
    public MenuConvIO(final TriFunction<Player, ConversationAction, Boolean, ConversationSession> inputFunction, final Conversation conv,
                      final OnlineProfile onlineProfile, final ConversationColors colors,
                      final MenuConvIOSettings settings, final FixedComponentLineWrapper componentLineWrapper,
                      final Plugin plugin, final Map<CONTROL, ACTION> controls) {
        super(conv, onlineProfile, colors);
        this.plugin = plugin;
        this.settings = settings;
        this.componentLineWrapper = componentLineWrapper;
        this.controls = controls;
        this.input = inputFunction.apply(onlineProfile.getPlayer(), new MenuConversationAction(), settings.setSpeed());
    }

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
            input.begin();
        } finally {
            lock.unlock();
        }
    }

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
            displayRunnable.runTaskTimerAsynchronously(plugin, settings.refreshDelay(), settings.refreshDelay());
        }
    }

    // Override this event from our parent
    @SuppressWarnings("deprecation")
    @Override
    @EventHandler(ignoreCancelled = true)
    public void onReply(final AsyncPlayerChatEvent event) {
        // Empty
    }

    @Override
    public void clear() {
        if (displayRunnable != null) {
            displayRunnable.cancel();
            displayRunnable = null;
        }

        chatDisplay = null;

        super.clear();
    }

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
            input.end();

            if (displayRunnable != null) {
                displayRunnable.cancel();
                displayRunnable = null;
            }

            super.end(callback);
        } finally {
            lock.unlock();
        }
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
        if (state.isInactive() || !event.getPlayer().equals(onlineProfile.getPlayer())) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
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
        if (state.isInactive() || !event.getPlayer().equals(onlineProfile.getPlayer())) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
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
        if (state.isInactive() || !event.getDamager().equals(onlineProfile.getPlayer())) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
                return;
            }

            event.setCancelled(true);

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && controls.containsKey(CONTROL.LEFT_CLICK)) {
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
        }
        selectionCooldowns.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> selectionCooldowns.remove(player), settings.rateLimit());
        return false;
    }

    /**
     * Handles the player item held event.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerItemHeldEvent(final PlayerItemHeldEvent event) {
        if (state.isInactive() || !event.getPlayer().equals(onlineProfile.getPlayer())) {
            return;
        }
        if (!controls.containsKey(CONTROL.SCROLL)) {
            return;
        }

        lock.lock();
        try {
            if (state.isInactive()) {
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
        if (chatDisplay == null && scroll != Scroll.NONE) {
            return;
        }
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
         * The player canceled the conversation.
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

    /**
     * Menu specific controls.
     */
    private class MenuConversationAction implements ConversationAction {

        /**
         * The empty default constructor.
         */
        public MenuConversationAction() {
        }

        @Override
        public void unmount() {
            if (controls.containsKey(CONTROL.SNEAK)) {
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
        }

        @Override
        public void jump() {
            if (controls.containsKey(CONTROL.JUMP)) {
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
            }
        }

        @Override
        public void forward() {
            if (controls.containsKey(CONTROL.MOVE)) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> updateDisplay(Scroll.UP));
            }
        }

        @Override
        public void back() {
            if (controls.containsKey(CONTROL.MOVE)) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> updateDisplay(Scroll.DOWN));
            }
        }

        @Override
        public void left() {
            // Empty
        }

        @Override
        public void right() {
            // Empty
        }
    }
}
