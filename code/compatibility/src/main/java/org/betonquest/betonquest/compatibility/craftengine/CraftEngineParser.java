package org.betonquest.betonquest.compatibility.craftengine;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.inventory.ItemStack;

/**
 * Parses a string to a {@link CustomItem}.
 */
public class CraftEngineParser implements SimpleArgumentParser<CustomItem<ItemStack>> {

    /**
     * The default instance of {@link CraftEngineParser}.
     */
    public static final CraftEngineParser CRAFT_ENGINE_PARSER = new CraftEngineParser();

    /**
 * The empty default constructor.
 */
    public CraftEngineParser() { }

    @Override
    public CustomItem<ItemStack> apply(final String string) throws QuestException {
        return Utils.getNN(CraftEngineItems.byId(Key.of(string)), "Invalid CraftEngine Item: " + string);
    }
}
