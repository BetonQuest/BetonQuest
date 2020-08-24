/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.betoncraft.betonquest.compatibility.brewery;

import com.dre.brewery.Brew;
import com.dre.brewery.recipe.BRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class TakeBrewEvent extends QuestEvent {

    private Integer count;
    private BRecipe brew;

    public TakeBrewEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        count = instruction.getInt();
        if (count < 1) {
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
    protected Void execute(final String playerID) throws QuestRuntimeException {

        final Player player = PlayerConverter.getPlayer(playerID);

        int remaining = count;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack item = player.getInventory().getItem(i);
            if (item != null && Brew.get(item) != null && Brew.get(item).getCurrentRecipe().equals(brew)) {
                if (item.getAmount() - remaining <= 0) {
                    remaining -= item.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - remaining);
                    remaining = 0;
                }
                if (remaining <= 0) {
                    break;
                }
            }
        }
        player.updateInventory();
        return null;
    }
}
