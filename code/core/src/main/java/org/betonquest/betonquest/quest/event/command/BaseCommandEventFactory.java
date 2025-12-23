package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for command related event factories.
 * <p>
 * Contains common methods for parsing commands from instructions and holds the server, scheduler and plugin.
 */
public abstract class BaseCommandEventFactory implements PlayerEventFactory {

    /**
     * Regex used to detect a conditions statement at the end of the instruction.
     */
    private static final Pattern CONDITIONS_REGEX = Pattern.compile("conditions?:\\S*\\s*$");

    /**
     * The server to execute commands on.
     */
    protected final Server server;

    /**
     * Logger factory to create a logger for the events.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the sudo event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param server        the server to execute commands on
     */
    public BaseCommandEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        this.loggerFactory = loggerFactory;
        this.server = server;
    }

    /**
     * Parses the commands from the instruction.
     * <p>
     * The commands are separated by | but one can use \| to represent a pipe character.
     * If the instruction contains a conditions: section, it will be trimmed from the command string.
     * Spaces at the beginning and end of each command will be trimmed.
     *
     * @param instruction instruction to parse
     * @return list of commands
     * @throws QuestException if the instruction is invalid
     */
    public List<Variable<String>> parseCommands(final Instruction instruction) throws QuestException {
        final List<Variable<String>> commands = new ArrayList<>();
        final String string = String.join(" ", instruction.getValueParts());
        final Matcher conditionsMatcher = CONDITIONS_REGEX.matcher(string);
        final int end = conditionsMatcher.find() ? conditionsMatcher.start() : string.length();
        final String command = (String) string.subSequence(0, end);
        final List<String> rawCommands = Arrays.stream(command.split("(?<!\\\\)\\|"))
                .map(s -> s.replace("\\|", "|"))
                .map(String::trim)
                .toList();
        for (final String rawCommand : rawCommands) {
            commands.add(instruction.get(rawCommand, instruction.getParsers().string()));
        }
        return commands;
    }
}
