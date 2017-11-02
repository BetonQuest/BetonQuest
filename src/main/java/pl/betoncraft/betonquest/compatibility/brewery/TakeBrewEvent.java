package pl.betoncraft.betonquest.compatibility.brewery;

import com.dre.brewery.BIngredients;
import com.dre.brewery.BRecipe;
import com.dre.brewery.Brew;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class TakeBrewEvent extends QuestEvent{

    private Integer count;
    private BRecipe brew;

    public TakeBrewEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);

        count = instruction.getInt();
        if(count < 1){
            throw new InstructionParseException("Can't give less than one brew!");
        }

        String name = instruction.next().replace("_", " ");

        BRecipe recipe = null;
        for(BRecipe r : BIngredients.recipes){
            if(r.hasName(name)){
                recipe = r;
                break;
            }
        }

        if(recipe == null){
            throw new InstructionParseException("There is no brewing recipe with the name " + name + "!");
        }else{
            this.brew = recipe;
        }
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {

        Player p = PlayerConverter.getPlayer(playerID);

        int remaining = count;

        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (item != null && Brew.get(item) != null && Brew.get(item).getCurrentRecipe().equals(brew)) {
                if (item.getAmount() - remaining <= 0) {
                    remaining -= item.getAmount();
                    p.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - remaining);
                    remaining = 0;
                }
                if (remaining <= 0) {
                    break;
                }
            }
        }

        p.updateInventory();
    }
}
