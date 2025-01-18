package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.Brew;
import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("PMD.CommentRequired")
public class HasBrewCondition extends Condition {
    private final Integer count;

    private final BRecipe brew;

    public HasBrewCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);

        count = instruction.getInt();
        if (count <= 0) {
            throw new QuestException("Can't give less than one brew!");
        }

        final String name = instruction.next().replace("_", " ");

        BRecipe recipe = null;
        for (final BRecipe r : BRecipe.getAllRecipes()) {
            if (r.hasName(name)) {
                recipe = r;
                break;
            }
        }

        if (recipe == null) {
            throw new QuestException("There is no brewing recipe with the name " + name + "!");
        } else {
            this.brew = recipe;
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final Player player = profile.getOnlineProfile().get().getPlayer();

        int remaining = count;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack item = player.getInventory().getItem(i);
            if (item != null) {
                final Brew brewItem = Brew.get(item);
                if (brewItem != null && brewItem.getCurrentRecipe().equals(brew)) {
                    remaining -= item.getAmount();
                    if (remaining <= 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
