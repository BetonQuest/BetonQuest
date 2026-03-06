package org.betonquest.betonquest.compatibility.nexo;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

/**
 * Parses strings into {@link ItemBuilder} instances.
 */
public class NexoParser implements SimpleArgumentParser<ItemBuilder> {

    /**
     * The default instance of {@link NexoParser}.
     */
    public static final NexoParser NEXO_PARSER = new NexoParser();

    /**
     * The empty default constructor.
     */
    public NexoParser() {
        // Empty
    }

    @Override
    public ItemBuilder apply(final String string) throws QuestException {
        final ItemBuilder item = NexoItems.itemFromId(string);
        if (item == null) {
            throw new QuestException("Invalid Nexo Item '%s'!".formatted(string));
        }
        return item;
    }
}
