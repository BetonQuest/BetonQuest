package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class for {@link DefaultIdentifier}s that also provides an instruction.
 */
public abstract class InstructionIdentifier extends DefaultIdentifier {

    /**
     * The created instruction of the object.
     */
    private final Instruction instruction;

    /**
     * Constructor of an identifier that creates an instruction from the given function.
     *
     * @param packManager         the quest package manager to get quest packages from
     * @param pack                the package the instruction is in
     * @param identifier          the identifier string leading to the instruction
     * @param instructionFunction the instruction provided by this identifier
     * @throws QuestException if the identifier could not be parsed
     */
    protected InstructionIdentifier(final QuestPackageManager packManager, @Nullable final QuestPackage pack,
                                    final String identifier,
                                    final QuestFunction<Identifier, Instruction> instructionFunction) throws QuestException {
        super(packManager, pack, identifier);
        this.instruction = instructionFunction.apply(this);
    }

    /**
     * Constructor of an identifier that creates an instruction from the given section.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the package the instruction is in
     * @param identifier  the identifier string leading to the instruction
     * @param section     the section of the config file
     * @param readable    the readable name of the object type
     * @throws QuestException if the identifier or instruction could not be parsed
     */
    protected InstructionIdentifier(final Variables variables, final QuestPackageManager packManager, @Nullable final QuestPackage pack,
                                    final String identifier, final String section, final String readable) throws QuestException {
        this(packManager, pack, identifier, id -> {
            final MultiConfiguration config = id.getPackage().getConfig();
            final String rawInstruction = config.getString(section + config.options().pathSeparator() + id.get());
            if (rawInstruction == null) {
                throw new QuestException(readable + " '" + id.getFull() + "' is not defined");
            }
            return new Instruction(variables, packManager, id.getPackage(), id, rawInstruction);
        });
    }

    /**
     * Returns the Instruction referenced and created by this ID.
     *
     * @return the instruction of this ID
     */
    public Instruction getInstruction() {
        return instruction;
    }
}
