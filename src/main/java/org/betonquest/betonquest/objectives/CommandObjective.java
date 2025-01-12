package org.betonquest.betonquest.objectives;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Requires the player to execute a specific command.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CommandObjective extends Objective implements Listener {
    private final VariableString command;

    private final boolean ignoreCase;

    private final boolean exact;

    private final boolean cancel;

    private final EventID[] failEvents;

    public CommandObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        command = new VariableString(instruction.getPackage(), instruction.next(), true);
        ignoreCase = instruction.hasArgument("ignoreCase");
        exact = instruction.hasArgument("exact");
        cancel = instruction.hasArgument("cancel");
        failEvents = instruction.getList(instruction.getOptional("failEvents"), instruction::getEvent).toArray(new EventID[0]);
    }

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            final String replaceCommand = command.getString(onlineProfile);
            if (foundMatch(event.getMessage(), replaceCommand)) {
                if (cancel) {
                    event.setCancelled(true);
                }
                completeObjective(onlineProfile);
            } else {
                for (final EventID failEvent : failEvents) {
                    BetonQuest.event(onlineProfile, failEvent);
                }
            }
        }
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

    private boolean foundMatch(final String commandExecuted, final String commandRequired) {
        if (exact) {
            return ignoreCase ? StringUtils.equalsIgnoreCase(commandExecuted, commandRequired)
                    : StringUtils.equals(commandExecuted, commandRequired);
        } else {
            return ignoreCase ? StringUtils.startsWithIgnoreCase(commandExecuted, commandRequired)
                    : StringUtils.startsWith(commandExecuted, commandRequired);
        }
    }

}
