package org.betonquest.betonquest.api.quest.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.instruction.PlaceholderInstruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents placeholder-related identifiers.
 */
public class PlaceholderID extends InstructionIdentifier {

    /**
     * The prefix and suffix used to identify placeholders.
     */
    public static final String PLACEHOLDER_IDENTIFIER = "%";

    /**
     * Constructs a new {@link PlaceholderID} with the given logger factory, quest package, and identifier.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         The quest package that this identifier belongs to.
     * @param identifier   The identifier string. It should start and end with '%' character.
     * @throws QuestException if the instruction could not be created or
     *                        if the identifier string does not start and end with '%' character.
     */
    public PlaceholderID(final Placeholders placeholders, final QuestPackageManager packManager, @Nullable final QuestPackage pack,
                         final String identifier) throws QuestException {
        super(packManager, pack, identifier.substring(1, identifier.length() - 1), id -> {
            if (!identifier.startsWith(PLACEHOLDER_IDENTIFIER) || !identifier.endsWith(PLACEHOLDER_IDENTIFIER)) {
                throw new QuestException("Placeholder instruction has to start and end with '%' characters");
            }
            return new PlaceholderInstruction(placeholders, packManager, id.getPackage(), id, DefaultArgumentParsers.INSTANCE, id.get());
        });
    }

    @Override
    public String get() {
        return PLACEHOLDER_IDENTIFIER + super.get() + PLACEHOLDER_IDENTIFIER;
    }

    @Override
    public String getFull() {
        return PLACEHOLDER_IDENTIFIER + getPackage().getQuestPath() + SEPARATOR + super.get() + PLACEHOLDER_IDENTIFIER;
    }
}
