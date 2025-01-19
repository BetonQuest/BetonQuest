package org.betonquest.betonquest.util;

import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Utility class for entities.
 */
public final class EntityUtils {

    private EntityUtils() {
    }

    /**
     * Gets all entities in the range around the location with the given name and mark.
     *
     * @param location the location
     * @param name     the name of the entity
     * @param marked   the mark of the entity
     * @param range    the range around the location
     * @return the selected entity
     */
    public static List<Entity> getSelectedEntity(final Location location, @Nullable final String name,
                                                 @Nullable final String marked, final double range) {
        final Collection<Entity> entities = location.getNearbyEntities(range, range, range);
        return entities.stream().filter(entity -> isSelectedEntity(entity, name, marked)).toList();
    }

    private static boolean isSelectedEntity(final Entity entity, @Nullable final String name, @Nullable final String mark) {
        if (name != null && !entity.getName().equals(name)) {
            return false;
        }
        if (mark != null) {
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
            final String dataContainerValue = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            return dataContainerValue != null && dataContainerValue.equals(mark);
        }
        return true;
    }
}
