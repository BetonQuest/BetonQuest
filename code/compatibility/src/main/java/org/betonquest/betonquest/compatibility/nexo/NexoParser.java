package org.betonquest.betonquest.compatibility.nexo;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.util.Utils;

/**
 * Parses strings into {@link ItemBuilder} instances.
 */
public class NexoParser implements SimpleArgumentParser<ItemBuilder> {

    /**
     * Default constructor for NexoParser.
     */
    private NexoParser() {
    }

    /**
     * The default instance of {@link NexoParser}.
     */
    public static final NexoParser NEXO_PARSER = new NexoParser();

    /**
     * Converts a string ID to a {@link ItemBuilder}.
     *
     * @param string the item ID to parse
     * @return the corresponding item builder
     * @throws QuestException if the item ID is invalid
     */
    @Override
    public ItemBuilder apply(final String string) throws QuestException {
        return Utils.getNN(NexoItems.itemFromId(string), "Invalid Nexo Item: " + string);
    }

}
