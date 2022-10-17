package org.betonquest.betonquest.objectives;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Requires the player to execute a specific command.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CommandObjective extends Objective implements Listener {

    private final String command;
    private final List<String> commandVariables;
    private final boolean ignoreCase;
    private final boolean exact;
    private final boolean cancel;
    private final EventID[] failEvents;

    public CommandObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        command = parseCommand(instruction.next());
        commandVariables = loadVariables(command);
        ignoreCase = instruction.hasArgument("ignoreCase");
        exact = instruction.hasArgument("exact");
        cancel = instruction.hasArgument("cancel");
        failEvents = instruction.getList(instruction.getOptional("failEvents"), instruction::getEvent).toArray(new EventID[0]);
    }

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final OnlineProfile profile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(profile) && checkConditions(profile)) {
            final String replaceCommand = getCommandWithVariablesReplaced(profile);
            if (foundMatch(event.getMessage(), replaceCommand)) {
                if (cancel) {
                    event.setCancelled(true);
                }
                completeObjective(profile);
            } else {
                for (final EventID failEvent : failEvents) {
                    BetonQuest.event(profile, failEvent);
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

    private String parseCommand(final String rawCommand) {
        return rawCommand
                .replaceAll("(?<!\\\\)_", " ")
                .replaceAll("\\\\_", "_");
    }

    private List<String> loadVariables(final String message) throws InstructionParseException {
        final List<String> variables = new ArrayList<>();
        for (final String variable : BetonQuest.resolveVariables(message)) {
            try {
                BetonQuest.createVariable(instruction.getPackage(), variable);
            } catch (final InstructionParseException exception) {
                throw new InstructionParseException("Could not create '" + variable + "' variable: "
                        + exception.getMessage(), exception);
            }
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
        return variables;
    }

    private String getCommandWithVariablesReplaced(final Profile profile) {
        String replaceCommand = command;
        for (final String variable : commandVariables) {
            replaceCommand = replaceCommand.replace(
                    variable,
                    BetonQuest.getInstance().getVariableValue(instruction.getPackage().getPackagePath(), variable, profile)
            );
        }
        return replaceCommand;
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
