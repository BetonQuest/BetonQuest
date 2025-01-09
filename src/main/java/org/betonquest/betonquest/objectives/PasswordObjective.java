package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Requires the player to type a password in chat.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PasswordObjective extends Objective implements Listener {

    private final Pattern regex;

    @Nullable
    private final String passwordPrefix;

    private final EventID[] failEvents;

    public PasswordObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        final String pattern = instruction.next().replace('_', ' ');
        final int regexFlags = instruction.hasArgument("ignoreCase") ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
        regex = Pattern.compile(pattern, regexFlags);
        final String prefix = instruction.getOptional("prefix");
        passwordPrefix = prefix == null || prefix.isEmpty() ? prefix : prefix + ": ";
        failEvents = instruction.getList(instruction.getOptional("fail"), instruction::getEvent).toArray(new EventID[0]);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (chatInput(false, event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (chatInput(true, event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private boolean chatInput(final boolean fromCommand, final Player player, final String message) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
        if (!containsPlayer(onlineProfile)) {
            return false;
        }
        final String prefix = passwordPrefix == null
                ? Config.getMessage(BetonQuest.getInstance().getPlayerDataStorage()
                .get(onlineProfile).getLanguage(), "password") : passwordPrefix;
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
                    BetonQuest.event(onlineProfile, event);
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
