package org.betonquest.betonquest.compatibility.craftengine;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

/**
 * Parses a string to a {@link BukkitItemDefinition}.
 */
public class CraftEngineParser implements SimpleArgumentParser<BukkitItemDefinition> {

    /**
     * The default instance of {@link CraftEngineParser}.
     */
    public static final CraftEngineParser CRAFT_ENGINE_PARSER = new CraftEngineParser();

    /**
     * The empty default constructor.
     */
    public CraftEngineParser() {
        // Empty
    }

    @Override
    public BukkitItemDefinition apply(final String string) throws QuestException {
        final BukkitItemDefinition bukkitItemDefinition = CraftEngineItems.byId(string);
        if (bukkitItemDefinition == null) {
            throw new QuestException("Invalid CraftEngine Item '%s'!".formatted(string));
        }
        return bukkitItemDefinition;
    }
}
