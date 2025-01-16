package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.Brew;
import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@SuppressWarnings("PMD.CommentRequired")
public class TakeBrewEvent extends QuestEvent {
    private final Integer count;

    private final BRecipe brew;

    public TakeBrewEvent(final Instruction instruction) throws QuestException {
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
    @SuppressWarnings("PMD.CognitiveComplexity")
    protected Void execute(final Profile profile) throws QuestException {
        final Player player = profile.getOnlineProfile().get().getPlayer();

        int remaining = count;

        final PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);
            if (item != null) {
                final Brew brewItem = Brew.get(item);
                if (brewItem != null && brewItem.getCurrentRecipe().equals(brew)) {
                    if (item.getAmount() - remaining <= 0) {
                        remaining -= item.getAmount();
                        inventory.setItem(i, null);
                    } else {
                        item.setAmount(item.getAmount() - remaining);
                        remaining = 0;
                    }
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        player.updateInventory();
        return null;
    }
}
