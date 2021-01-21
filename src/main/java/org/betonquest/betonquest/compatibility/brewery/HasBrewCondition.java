package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.Brew;
import com.dre.brewery.recipe.BRecipe;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("PMD.CommentRequired")
public class HasBrewCondition extends Condition {

    private final Integer count;
    private final BRecipe brew;

    public HasBrewCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        count = instruction.getInt();
        if (count <= 0) {
            throw new InstructionParseException("Can't give less than one brew!");
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
            throw new InstructionParseException("There is no brewing recipe with the name " + name + "!");
        } else {
            this.brew = recipe;
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);

        int remaining = count;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack item = player.getInventory().getItem(i);
            if (item != null && Brew.get(item) != null && Brew.get(item).getCurrentRecipe().equals(brew)) {

                remaining -= item.getAmount();
                if (remaining <= 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
