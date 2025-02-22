package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Variable that resolves into a list of {@link T}.
 *
 * @param <T> the variable type
 */
public class VariableList<T> extends Variable<List<T>> {

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param resolver          the resolver to convert the resolved variable to the given type
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableList(final VariableProcessor variableProcessor, @Nullable final QuestPackage pack, final String input,
                        final QuestFunction<String, T> resolver) throws QuestException {
        super(variableProcessor, pack, input, value -> {
            final String[] array = value.split(",");
            final List<T> list = new ArrayList<>(array.length);
            for (final String part : array) {
                list.add(resolver.apply(part));
            }
            return list;
        });
    }
}
