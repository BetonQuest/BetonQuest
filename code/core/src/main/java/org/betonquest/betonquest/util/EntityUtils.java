package org.betonquest.betonquest.util;

import net.kyori.adventure.text.Component;
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

    /**
     * Key for the {@link PersistentDataType#STRING} marker of entities.
     */
    public static final NamespacedKey MARKED_KEY = new NamespacedKey("betonquest", "betonquest-marked");

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
    public static List<Entity> getSelectedEntity(final Location location, @Nullable final Component name,
                                                 @Nullable final String marked, final double range) {
        final Collection<Entity> entities = location.getNearbyEntities(range, range, range);
        return entities.stream().filter(entity -> isSelectedEntity(entity, name, marked)).toList();
    }

    private static boolean isSelectedEntity(final Entity entity, @Nullable final Component name, @Nullable final String mark) {
        if (name != null && !entity.name().equals(name)) {
            return false;
        }
        if (mark != null) {
            final String dataContainerValue = entity.getPersistentDataContainer().get(MARKED_KEY, PersistentDataType.STRING);
            return dataContainerValue != null && dataContainerValue.equals(mark);
        }
        return true;
    }
}
