package org.betonquest.betonquest.quest.objective.password;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Requires the player to type a password in chat.
 */
public class PasswordObjective extends DefaultObjective {

    /**
     * Regex pattern to match the password.
     */
    private final FlagArgument<Pattern> regex;

    /**
     * Prefix to be shown to the player before the password.
     */
    @Nullable
    private final String passwordPrefix;

    /**
     * Actions to be triggered on failure.
     */
    private final Argument<List<ActionID>> failActions;

    /**
     * Constructor for the PasswordObjective.
     *
     * @param service        the objective factory service
     * @param regex          the regex pattern to match the password
     * @param passwordPrefix the prefix to be shown to the player
     * @param failActions    the actions to be triggered on failure
     * @throws QuestException if there is an error in the instruction
     */
    public PasswordObjective(final ObjectiveFactoryService service, final FlagArgument<Pattern> regex,
                             @Nullable final String passwordPrefix, final Argument<List<ActionID>> failActions) throws QuestException {
        super(service);
        this.regex = regex;
        this.passwordPrefix = passwordPrefix;
        this.failActions = failActions;
    }

    /**
     * Check if the password is correct.
     *
     * @param event         the chat event to check
     * @param onlineProfile the profile of the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onChat(final AsyncPlayerChatEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (chatInput(false, onlineProfile, event.getMessage())) {
            event.setCancelled(true);
        }
    }

    /**
     * Capture the command input from the player amd cancel it if the password is correct.
     *
     * @param event         the command event to check
     * @param onlineProfile the profile of the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onCommand(final PlayerCommandPreprocessEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (chatInput(true, onlineProfile, event.getMessage())) {
            event.setCancelled(true);
        }
    }

    private boolean chatInput(final boolean fromCommand, final OnlineProfile onlineProfile, final String message) throws QuestException {
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

        if (regex.getValue(onlineProfile).map(pattern -> pattern.matcher(password).matches()).orElse(false)) {
            Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> getService().complete(onlineProfile));
            return !fromCommand || !prefix.isEmpty();
        }
        try {
            BetonQuest.getInstance().getQuestTypeApi().actions(onlineProfile, failActions.getValue(onlineProfile));
        } catch (final QuestException e) {
            throw new QuestException("Failed to resolve fail actions: " + e.getMessage(), e);
        }
        return !prefix.isEmpty();
    }
}
