package org.betonquest.betonquest.compatibility.thebrewingproject.item;

import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewQuality;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Serializer for TheBrewingProject brews.
 *
 * @param brewManager the brew manager provided by TheBrewingProject
 */
public record BrewItemSerializer(BrewManager<ItemStack> brewManager) implements QuestItemSerializer {

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final String recipeName = brewManager.brewName(itemStack)
                .orElseThrow(() -> new QuestException("Item not completed brew"));
        final BrewQuality quality = brewManager.brewQuality(itemStack)
                .orElseThrow(() -> new QuestException("Brew is failed"));
        return "\"%s\" %s".formatted(recipeName, quality.name().toLowerCase(Locale.ROOT));
    }
}
