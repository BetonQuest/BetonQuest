package org.betonquest.betonquest.quest.objective.password;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
    private final Variable<List<EventID>> failEvents;

    /**
     * Constructor for the PasswordObjective.
     *
     * @param instruction    the instruction that created this objective
     * @param regex          the regex pattern to match the password
     * @param passwordPrefix the prefix to be shown to the player
     * @param failEvents     the events to be triggered on failure
     * @throws QuestException if there is an error in the instruction
     */
    public PasswordObjective(final Instruction instruction, final Pattern regex,
                             @Nullable final String passwordPrefix, final Variable<List<EventID>> failEvents) throws QuestException {
        super(instruction);
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
        qeHandler.handle(() -> {
            if (chatInput(false, event.getPlayer(), event.getMessage())) {
                event.setCancelled(true);
            }
        });
    }

    /**
     * Capture the command input from the player amd cancel it if the password is correct.
     *
     * @param event the command event to check
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        qeHandler.handle(() -> {
            if (chatInput(true, event.getPlayer(), event.getMessage())) {
                event.setCancelled(true);
            }
        });
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private boolean chatInput(final boolean fromCommand, final Player player, final String message) throws QuestException {
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (!containsPlayer(onlineProfile)) {
            return false;
        }
        final String prefix;
        try {
            prefix = passwordPrefix == null
                    ? LegacyComponentSerializer.legacySection().serialize(BetonQuest.getInstance().getPluginMessage().getMessage(onlineProfile, "password"))
                    : passwordPrefix;
        } catch (final QuestException e) {
            throw new QuestException("Failed to get password prefix: " + e.getMessage(), e);
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
                try {
                    for (final EventID event : failEvents.getValue(onlineProfile)) {
                        BetonQuest.getInstance().getQuestTypeApi().event(onlineProfile, event);
                    }
                } catch (final QuestException e) {
                    throw new QuestException("Failed to resolve events: " + e.getMessage(), e);
                }
            }
        }
        return !prefix.isEmpty();
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
