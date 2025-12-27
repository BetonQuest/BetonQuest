package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.api.quest.Placeholders;

/**
 * The {@link PlaceholderInstruction}. Primary object for placeholder input parsing.
 */
public class PlaceholderInstruction extends DefaultInstruction {

    /**
     * Regular expression that can be used to split placeholders correctly.
     */
    private static final Tokenizer DOT_TOKENIZER = (instruction) -> instruction.split("\\.");

    /**
     * Constructs a new {@link PlaceholderInstruction} with the given quest package, placeholder identifier, and instruction.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         The quest package that this instruction belongs to.
     * @param identifier   The identifier of the placeholder.
     * @param parsers      The parsers to use for parsing the instruction's arguments.
     * @param instruction  The instruction string. It should start and end with '%' character.
     * @throws QuestException if the instruction could not be tokenized,
     *                        or if the instruction does not start and end with '%' character.
     */
    public PlaceholderInstruction(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack,
                                  final Identifier identifier, final ArgumentParsers parsers, final String instruction) throws QuestException {
        super(placeholders, packManager, DOT_TOKENIZER, pack, identifier, parsers, cleanInstruction(instruction));
    }

    /**
     * Constructs a new PlaceholderInstruction with the given quest package, placeholder identifier, and instruction.
     *
     * @param instruction The raw instruction string for this placeholder.
     * @param identifier  The identifier for this placeholder.
     */
    public PlaceholderInstruction(final PlaceholderInstruction instruction, final Identifier identifier) {
        super(instruction, identifier);
    }

    private static String cleanInstruction(final String instruction) throws QuestException {
        if (!instruction.isEmpty() && instruction.charAt(0) != '%' && !instruction.endsWith("%")) {
            throw new QuestException("Placeholder instruction does not start and end with '%' character");
        }
        return instruction.substring(1, instruction.length() - 1);
    }

    @Override
    public PlaceholderInstruction copy() {
        return copy(getID());
    }

    @Override
    public PlaceholderInstruction copy(final Identifier newID) {
        return new PlaceholderInstruction(this, newID);
    }
}
