package org.betonquest.betonquest.compatibility.nexo;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.util.Utils;

public class NexoParser implements SimpleArgumentParser<ItemBuilder> {

    public static final NexoParser NEXO_PARSER = new NexoParser();

    @Override
    public ItemBuilder apply(final String string) throws QuestException {
        return Utils.getNN(NexoItems.itemFromId(string), "Invalid Nexo Item: " + string);
    }

}
