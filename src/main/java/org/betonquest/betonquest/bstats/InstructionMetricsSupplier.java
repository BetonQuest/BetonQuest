package org.betonquest.betonquest.bstats;

import org.betonquest.betonquest.api.identifier.InstructionIdentifier;

import java.util.Set;

/**
 * A supplier for providing raw data about instructions to calculate different metrics.
 *
 * @param <T> kind of {@link InstructionIdentifier} that is supplied
 */
public interface InstructionMetricsSupplier<T extends InstructionIdentifier> {

    /**
     * Fetch a set of all current {@link InstructionIdentifier}s.
     * If possible this method should do late-evaluation to provide updated data with every consecutive call.
     *
     * @return set of {@link InstructionIdentifier}s
     */
    Set<T> getIdentifiers();

    /**
     * Fetch a set of all currently valid types. If possible this method should do late-evaluation to provide updated
     * data with every consecutive call.
     *
     * @return set of type strings
     */
    Set<String> getTypes();
}
