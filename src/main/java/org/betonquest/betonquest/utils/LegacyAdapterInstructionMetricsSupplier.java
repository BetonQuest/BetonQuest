package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.id.ID;

import java.util.Set;
import java.util.function.Supplier;

public class LegacyAdapterInstructionMetricsSupplier<T extends ID> implements InstructionMetricsSupplier<T> {

    private final Supplier<Set<T>> identifierSupplier;

    private final Supplier<Set<String>> typeSupplier;

    public LegacyAdapterInstructionMetricsSupplier(final Supplier<Set<T>> identifierSupplier, final Supplier<Set<String>> typeSupplier) {
        this.identifierSupplier = identifierSupplier;
        this.typeSupplier = typeSupplier;
    }

    @Override
    public Set<T> getIdentifiers() {
        return identifierSupplier.get();
    }

    @Override
    public Set<String> getTypes() {
        return typeSupplier.get();
    }
}
