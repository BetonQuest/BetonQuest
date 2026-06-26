package org.betonquest.betonquest.util;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for metrics.
 */
public final class MetricsUtils {

    /**
     * Private constructor.
     */
    private MetricsUtils() {
    }

    /**
     * Counts the number of identifiers of a certain type and represents the sum in a map.
     *
     * @param identifiers    the identifiers to categorize and count
     * @param validTypes     the valid types to count - any other type will be ignored
     * @param instructionApi the instruction api to create instructions from identifiers
     * @return a map with all counts
     */
    public static Map<String, Integer> typeCountMetrics(final Collection<? extends ReadableIdentifier> identifiers,
                                                        final Collection<String> validTypes, final Instructions instructionApi) {
        final List<String> types = new ArrayList<>(validTypes);
        return new ArrayList<>(identifiers).stream()
                .map(identifier -> typeFromId(identifier, instructionApi))
                .filter(types::contains)
                .collect(Collectors.toConcurrentMap(Function.identity(), key -> 1, Integer::sum));
    }

    @Nullable
    private static String typeFromId(final ReadableIdentifier identifier, final Instructions instructionApi) {
        try {
            final Instruction instruction = identifier instanceof final PlaceholderIdentifier placeholderIdentifier
                    ? instructionApi.createPlaceholder(placeholderIdentifier, placeholderIdentifier.readRawInstruction())
                    : instructionApi.create(identifier, identifier.readRawInstruction());
            return instruction.getPart(0);
        } catch (final QuestException ignored) {
            return null;
        }
    }
}
