package org.betonquest.betonquest.bstats;

import org.betonquest.betonquest.id.ID;

import java.util.Set;

/**
 * A supplier for providing raw data about instructions to calculate different metrics.
 *
 * @param <T> kind of {@link ID} that is supplied
 */
public interface InstructionMetricsSupplier<T extends ID> {

    /**
     * Fetch a set of all current {@link ID}s. If possible this method should do late-evaluation to provide updated data with
     * every consecutive call.
     *
     * @return set of {@link ID}s
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
