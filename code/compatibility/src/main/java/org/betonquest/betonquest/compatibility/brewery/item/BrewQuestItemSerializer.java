package org.betonquest.betonquest.compatibility.brewery.item;

import com.dre.brewery.Brew;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes Brews to its name.
 */
public class BrewQuestItemSerializer implements QuestItemSerializer {

    /**
     * The empty default constructor.
     */
    public BrewQuestItemSerializer() {
    }

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        final Brew brew = Brew.get(itemStack);
        if (brew == null) {
            throw new QuestException("Item is not a Brew!");
        }
        return "\"" + brew.getCurrentRecipe().getRecipeName() + "\" " + brew.getQuality() + " mode:name";
    }
}
