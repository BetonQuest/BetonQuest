package org.betonquest.betonquest.compatibility.tbp.item;

import dev.jsinco.brewery.api.brew.BrewQuality;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The brewing project item serializer.
 */
public class TbpItemSerializer implements QuestItemSerializer {

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        final String recipeName = container.get(new NamespacedKey("brewery", "tag"), PersistentDataType.STRING);
        if (recipeName == null) {
            throw new QuestException("Item not completed brew");
        }
        final Double score = container.get(new NamespacedKey("brewery", "score"), PersistentDataType.DOUBLE);
        final BrewQuality quality;
        if (score == null) {
            quality = BrewQuality.EXCELLENT;
        } else {
            quality = quality(score);
        }
        if (quality == null) {
            throw new QuestException("Brew is failed");
        }
        return String.format("\"%s\" %s", recipeName, quality.name().toLowerCase(Locale.ROOT));
    }

    private @Nullable BrewQuality quality(final double score) {
        if (score >= 0.8) {
            return BrewQuality.EXCELLENT;
        }
        if (score >= 0.6) {
            return BrewQuality.GOOD;
        }
        if (score > 0) {
            return BrewQuality.BAD;
        }
        return null;
    }
}
