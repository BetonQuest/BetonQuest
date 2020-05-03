package pl.betoncraft.betonquest.commands.quest;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.config.ConfigAccessor;
import pl.betoncraft.betonquest.config.InternalMessagekeys;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.logging.Level;

@CommandAlias("quest")
public class ItemsCommand extends BaseCommand {
    @Subcommand("items|item|i")
    private void handleItems(Player player, @Flags("new") ItemID itemID) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, item must not be air");
            throw new InvalidCommandArgument(InternalMessagekeys.NO_ITEM, false);
        }

        if (itemID.getPackage() == null) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, package does not exist");
            throw new InvalidCommandArgument(InternalMessagekeys.SPECIFY_PACKAGE, false);
        }

        ConfigAccessor config = itemID.getPackage().getItems();
        String instructions = QuestItem.itemToString(item);

        // save it in items.yml
        LogUtils.getLogger().log(Level.FINE, "Saving item to configuration as " + itemID.getFullID());
        config.getConfig().set(itemID.getBaseID(), instructions.trim());
        config.saveConfig();

        getCurrentCommandIssuer().sendInfo(InternalMessagekeys.ITEM_CREATED, "{item}", itemID.getFullID());
    }

    @Subcommand("give|g")
    private void giveItem(Player player, ItemID itemID) {
        try {
            QuestItem item = new QuestItem(itemID);
            player.getInventory().addItem(item.generate(1));
        } catch (InstructionParseException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while creating an item: " + e.getMessage());
            LogUtils.logThrowable(e);
            throw new InvalidCommandArgument(InternalMessagekeys.ERROR, "{message}", e.getMessage());
        }
    }
}
