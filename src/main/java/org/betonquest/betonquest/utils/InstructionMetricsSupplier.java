package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.id.ID;

import java.util.Set;

public interface InstructionMetricsSupplier<T extends ID> {

    Set<T> getIdentifiers();

    Set<String> getTypes();
}
