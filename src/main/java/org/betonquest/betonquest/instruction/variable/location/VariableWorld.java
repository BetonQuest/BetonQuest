package org.betonquest.betonquest.instruction.variable.location;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Represents a world that can be referenced by a variable.
 */
public class VariableWorld extends Variable<World> {

    /**
     * Resolves a string that may contain variables to a variable of a world.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    public VariableWorld(final VariableProcessor variableProcessor, final QuestPackage pack, final String input) throws InstructionParseException {
        super(variableProcessor, pack, input, VariableWorld::parse);
    }

    /**
     * Parses the given value to a world.
     *
     * @param value the value to parse
     * @return the parsed world
     * @throws QuestRuntimeException if the value could not be parsed to a world
     */
    protected static World parse(final String value) throws QuestRuntimeException {
        final World world = Bukkit.getWorld(value);
        if (world == null) {
            throw new QuestRuntimeException("World " + value + " does not exists.");
        }
        return world;
    }
}
