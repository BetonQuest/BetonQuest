package org.betonquest.betonquest.compatibility.thebrewingproject.item;

import dev.jsinco.brewery.api.brew.BrewQuality;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.compatibility.thebrewingproject.BrewUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

/**
 * The brewing project item serializer.
 */
public record BrewItemSerializer() implements QuestItemSerializer {

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        final String recipeName = container.get(BrewUtil.TBP_TAG, PersistentDataType.STRING);
        if (recipeName == null) {
            throw new QuestException("Item not completed brew");
        }
        final BrewQuality quality = BrewUtil.quality(container)
                .orElse(null);
        if (quality == null) {
            throw new QuestException("Brew is failed");
        }
        return "\"%s\" %s".formatted(recipeName, quality.name().toLowerCase(Locale.ROOT));
    }
}
