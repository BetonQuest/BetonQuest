package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.jetbrains.annotations.Nullable;

/**
 * Creates an {@link ID} from a pack and string.
 *
 * @param <T> the type of the id
 */
@FunctionalInterface
public interface IDArgument<T extends ID> {
    /**
     * A variable interpreted as ID when resolved.
     *
     * @param argument the argument to parse the id
     * @param <I>      the ID type
     * @return the variables of the id
     */
    static <I extends ID> VariableArgument<Variable<I>> ofSingle(final IDArgument<I> argument) {
        return (variableProcessor, pack, string)
                -> new Variable<>(variableProcessor, pack, string, arg -> argument.convert(pack, arg));
    }

    /**
     * A variable interpreted as list of ID when resolved.
     *
     * @param argument the argument to parse the id
     * @param <I>      the ID type
     * @return the variables of the ids
     */
    static <I extends ID> VariableArgument<VariableList<I>> ofList(final IDArgument<I> argument) {
        return (variableProcessor, pack, string)
                -> new VariableList<>(variableProcessor, pack, string, arg -> argument.convert(pack, arg));
    }

    /**
     * Creates a new ID.
     *
     * @param pack       the source pack
     * @param identifier the id name, potentially prefixed with a quest path
     * @return the newly created id
     * @throws QuestException when there is no such {@link T} in the resolved quest package or
     *                        when the {@link T} cannot be created
     */
    T convert(@Nullable QuestPackage pack, String identifier) throws QuestException;
}
