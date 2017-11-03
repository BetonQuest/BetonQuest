package pl.betoncraft.betonquest.compatibility.brewery;

import com.dre.brewery.BIngredients;
import com.dre.brewery.BRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Collection;

public class GiveBrewEvent extends QuestEvent{

    private Integer amount;
    private Integer quality;
    private BRecipe recipe;

    public GiveBrewEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);

        amount = instruction.getInt();

        quality = instruction.getInt();

        if(quality < 0 || quality > 10){
            throw new InstructionParseException("Brew quality must be between 0 and 10!");
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
            this.recipe = recipe;
        }
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player p = PlayerConverter.getPlayer(playerID);

        ItemStack[] brews = new ItemStack[amount];
        for(int i = 0; i < amount; i++){
            brews[i] = recipe.create(quality);
        }

        Collection<ItemStack> remaining = p.getInventory().addItem(brews).values();

        for(ItemStack item : remaining) {
            p.getWorld().dropItem(p.getLocation(), item);
        }
    }
}
