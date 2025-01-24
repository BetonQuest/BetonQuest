package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;

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
public abstract class BaseCommandEventFactory implements EventFactory {

    /**
     * Regex used to detect a conditions statement at the end of the instruction.
     */
    private static final Pattern CONDITIONS_REGEX = Pattern.compile("conditions?:\\S*\\s*$");

    /**
     * Data for primary server thread access.
     */
    protected final PrimaryServerThreadData data;

    /**
     * Logger factory to create a logger for events.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the sudo event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public BaseCommandEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
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
    public List<VariableString> parseCommands(final Instruction instruction) throws QuestException {
        final List<VariableString> commands = new ArrayList<>();
        final String string = String.join(" ", instruction.getValueParts());
        final Matcher conditionsMatcher = CONDITIONS_REGEX.matcher(string);
        final int end = conditionsMatcher.find() ? conditionsMatcher.start() : string.length();
        final String command = (String) string.subSequence(0, end);
        final List<String> rawCommands = Arrays.stream(command.split("(?<!\\\\)\\|"))
                .map(s -> s.replace("\\|", "|"))
                .map(String::trim)
                .toList();
        for (final String rawCommand : rawCommands) {
            commands.add(instruction.get(rawCommand, VariableString::new));
        }
        return commands;
    }
}
