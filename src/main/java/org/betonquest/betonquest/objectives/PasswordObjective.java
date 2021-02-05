package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

/**
 * Requires the player to type a password in chat.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PasswordObjective extends Objective implements Listener {

    private final String regex;
    private final boolean ignoreCase;
    private final String passwordPrefix;
    private final EventID[] failEvents;

    public PasswordObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        regex = instruction.next().replace('_', ' ');
        ignoreCase = instruction.hasArgument("ignoreCase");
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

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private boolean chatInput(final boolean fromCommand, final Player player, final String message) {
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID)) {
            return false;
        }
        final String prefix = passwordPrefix == null ?
                Config.getMessage(BetonQuest.getInstance().getPlayerData(playerID).getLanguage(), "password") : passwordPrefix;
        if (!prefix.isEmpty() && !message.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
            return false;
        }
        final String password = message.substring(prefix.length());
        if (checkConditions(playerID)) {
            if ((ignoreCase ? password.toLowerCase(Locale.ROOT) : password).matches(regex)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        completeObjective(playerID);
                    }
                }.runTask(BetonQuest.getInstance());

                if (fromCommand) {
                    return !prefix.isEmpty();
                } else {
                    return true;
                }
            } else {
                for (final EventID event : failEvents) {
                    BetonQuest.event(playerID, event);
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
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
