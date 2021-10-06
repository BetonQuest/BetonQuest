package org.betonquest.betonquest.objectives;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
        template = ObjectiveData.class;
        command = instruction.next().replace('_', ' ');
        commandVariables = loadVariables(command);
        ignoreCase = instruction.hasArgument("ignoreCase");
        exact = instruction.hasArgument("exact");
        cancel = instruction.hasArgument("cancel");
        failEvents = instruction.getList(instruction.getOptional("failEvents"), instruction::getEvent).toArray(new EventID[0]);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (event.isCancelled() && event.getMessage().startsWith("/")) {
            // A different plugin intercepted this chat "command" already, handle unregistered command
            checkForRequiredCommand(event.getPlayer(), event.getMessage(), event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        checkForRequiredCommand(event.getPlayer(), event.getMessage(), event);
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

    // TODO: This is mainly a functional copy-paste from NotifyEvent. Variable handling should eventually be standardized.
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

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    private void checkForRequiredCommand(final Player player, final String commandExecuted, final Cancellable event) {
        final String playerID = PlayerConverter.getID(player);
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            final String replaceCommand = getCommandWithVariablesReplaced(playerID);
            if (foundMatch(commandExecuted, replaceCommand)) {
                if (cancel) {
                    event.setCancelled(true);
                }
                Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> completeObjective(playerID));
            } else {
                for (final EventID failEvent : failEvents) {
                    BetonQuest.event(playerID, failEvent);
                }
            }
        }
    }

    private String getCommandWithVariablesReplaced(final String playerID) {
        String replaceCommand = command;
        for (final String variable : commandVariables) {
            replaceCommand = replaceCommand.replace(
                    variable,
                    BetonQuest.getInstance().getVariableValue(instruction.getPackage().getName(), variable, playerID)
            );
        }
        return replaceCommand;
    }

    private boolean foundMatch(final String commandExecuted, final String commandRequired) {
        final boolean match;
        if (exact) {
            match = ignoreCase ? StringUtils.equalsIgnoreCase(commandExecuted, commandRequired)
                    : StringUtils.equals(commandExecuted, commandRequired);
        } else {
            match = ignoreCase ? StringUtils.startsWithIgnoreCase(commandExecuted, commandRequired)
                    : StringUtils.startsWith(commandExecuted, commandRequired);
        }
        return match;
    }

}
