package org.betonquest.betonquest.compatibility.brewery.item;

import com.dre.brewery.Brew;
import com.dre.brewery.recipe.BRecipe;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Action to give a player a certain amount of brews with a specific quality.
 */
public class BrewWrapper implements QuestItemWrapper {

    /**
     * The quality of the brews.
     */
    private final Argument<Number> quality;

    /**
     * The name of the brew to give.
     */
    private final Argument<String> name;

    /**
     * Interpretation mode for brews.
     */
    private final Argument<IdentifierType> mode;

    /**
     * Create a new Give Brew Action.
     *
     * @param quality the quality of the brews.
     * @param name    the name of the brew to give.
     * @param mode    the interpretation mode for brews.
     */
    public BrewWrapper(final Argument<Number> quality, final Argument<String> name, final Argument<IdentifierType> mode) {
        this.quality = quality;
        this.name = name;
        this.mode = mode;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final int quality = this.quality.getValue(profile).intValue();
        BreweryUtils.validateQualityOrThrow(quality);
        final String name = this.name.getValue(profile);
        final BRecipe recipe = this.mode.getValue(profile).getRecipeOrThrow(name);
        return new BrewItem(recipe, quality);
    }

    /**
     * Implementation of {@link QuestItem} for Brews.
     *
     * @param recipe  the underlying recipe
     * @param quality the brew quality
     */
    public record BrewItem(BRecipe recipe, int quality) implements QuestItem {

        @Override
        public Component getName() {
            return Component.text(recipe.getName(quality));
        }

        @Override
        public List<Component> getLore() {
            final List<String> qualityLore = recipe.getLoreForQuality(quality);
            if (qualityLore == null) {
                return List.of();
            }
            return qualityLore.stream().map(Component::text).collect(Collectors.toUnmodifiableList());
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
            return recipe.create(quality).asQuantity(stackSize);
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            if (item == null) {
                return false;
            }
            final Brew brewItem = Brew.get(item);
            return brewItem != null && recipe.equals(brewItem.getCurrentRecipe());
        }
    }
}
