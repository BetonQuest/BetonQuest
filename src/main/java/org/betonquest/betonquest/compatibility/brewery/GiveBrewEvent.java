package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@SuppressWarnings("PMD.CommentRequired")
public class GiveBrewEvent extends QuestEvent {
    private final Integer amount;

    private final Integer quality;

    private final BRecipe recipe;

    public GiveBrewEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);

        amount = instruction.getInt();

        quality = instruction.getInt();

        if (quality < 0 || quality > 10) {
            throw new QuestException("Brew quality must be between 0 and 10!");
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
            this.recipe = recipe;
        }
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final Player player = profile.getOnlineProfile().get().getPlayer();

        final ItemStack[] brews = new ItemStack[amount];
        for (int i = 0; i < amount; i++) {
            brews[i] = recipe.create(quality);
        }

        final Collection<ItemStack> remaining = player.getInventory().addItem(brews).values();

        for (final ItemStack item : remaining) {
            player.getWorld().dropItem(player.getLocation(), item);
        }
        return null;
    }
}
