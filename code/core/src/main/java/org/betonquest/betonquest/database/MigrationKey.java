package org.betonquest.betonquest.database;

/**
 * Key for the migration with the information about the name and version.
 *
 * @param namespace name of the plugin
 * @param version   version of the migration
 */
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
