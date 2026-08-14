package org.betonquest.betonquest.lib.instruction.reader;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The default implementation of the {@link InstructionReaderStrategy}.
 */
public class SingleValueReader implements InstructionReaderStrategy<String> {

    /**
     * The parts of the instruction.
     */
    private final QuestSupplier<String> value;

    /**
     * Constructs a new instruction reader.
     *
     * @param value the value of the instruction
     */
    public SingleValueReader(final QuestSupplier<String> value) {
        this.value = value;
    }

    @Override
    public String getNext() throws QuestException {
        return value.get();
    }

    @Override
    @Nullable
    public String getOptional(final String prefix) throws QuestException {
        return value.get();
    }

    @Override
    public Map.Entry<FlagState, String> getFlag(final String prefix) throws QuestException {
        return Map.entry(FlagState.UNDEFINED, value.get());
    }

    @Override
    public Map<String, String> getNamed(final Predicate<String> keyFilter) {
        return Collections.emptyMap();
    }
}
