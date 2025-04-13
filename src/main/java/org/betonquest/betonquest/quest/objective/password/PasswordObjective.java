package org.betonquest.betonquest.quest.objective.password;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Requires the player to type a password in chat.
 */
public class PasswordObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Regex pattern to match the password.
     */
    private final Pattern regex;

    /**
     * Prefix to be shown to the player before the password.
     */
    @Nullable
    private final String passwordPrefix;

    /**
     * Events to be triggered on failure.
     */
    private final List<EventID> failEvents;

    /**
     * Constructor for the PasswordObjective.
     *
     * @param instruction    the instruction that created this objective
     * @param log            the logger for this objective
     * @param regex          the regex pattern to match the password
     * @param passwordPrefix the prefix to be shown to the player
     * @param failEvents     the events to be triggered on failure
     * @throws QuestException if there is an error in the instruction
     */
    public PasswordObjective(final Instruction instruction, final BetonQuestLogger log, final Pattern regex, @Nullable final String passwordPrefix, final List<EventID> failEvents) throws QuestException {
        super(instruction);
        this.log = log;
        this.regex = regex;
        this.passwordPrefix = passwordPrefix;
        this.failEvents = failEvents;
    }

    /**
     * Check if the password is correct.
     *
     * @param event the chat event to check
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (chatInput(false, event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }

    /**
     * Capture the command input from the player amd cancel it if the password is correct.
     *
     * @param event the command event to check
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (chatInput(true, event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private boolean chatInput(final boolean fromCommand, final Player player, final String message) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (!containsPlayer(onlineProfile)) {
            return false;
        }
        final String prefix;
        try {
            prefix = passwordPrefix == null
                    ? LegacyComponentSerializer.legacySection().serialize(BetonQuest.getInstance().getPluginMessage().getMessage("password").asComponent(onlineProfile))
                    : passwordPrefix;
        } catch (final QuestException e) {
            log.warn("Failed to get password prefix: " + e.getMessage(), e);
            return false;
        }
        if (!prefix.isEmpty() && !message.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
            return false;
        }
        final String password = message.substring(prefix.length());
        if (checkConditions(onlineProfile)) {
            if (regex.matcher(password).matches()) {
                Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> completeObjective(onlineProfile));
                return !fromCommand || !prefix.isEmpty();
            } else {
                for (final EventID event : failEvents) {
                    BetonQuest.getInstance().getQuestTypeAPI().event(onlineProfile, event);
                }
            }
        }
        return !prefix.isEmpty();
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
