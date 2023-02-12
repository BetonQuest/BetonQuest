package org.betonquest.betonquest.database;

public record MigrationKey(
        String namespace,
        int version
) implements Comparable<MigrationKey> {

    @Override
    public int compareTo(final MigrationKey key) {
        if (this.namespace.equals(key.namespace)) {
            return Integer.compare(this.version, key.version);
        }
        return this.namespace.compareTo(key.namespace);
    }
}
