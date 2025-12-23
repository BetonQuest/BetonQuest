package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.type.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.id.ItemID;

/**
 * Parses a string to an item.
 */
public class ItemParser implements InstructionIdentifierArgument<QuestItemWrapper> {

    /**
     * The singleton instance of the parser.
     */
    public static final ItemParser INSTANCE = new ItemParser(BetonQuest.getInstance().getFeatureApi());

    /**
     * The feature API to use for parsing.
     */
    private final FeatureApi featureApi;

    /**
     * Creates a new parser for items.
     *
     * @param featureApi the feature API to use for parsing
     */
    public ItemParser(final FeatureApi featureApi) {
        this.featureApi = featureApi;
    }

    @Override
    public QuestItemWrapper apply(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final ItemID item;
        final Variable<Number> number;
        if (string.contains(":")) {
            final String[] parts = string.split(":", 2);
            item = new ItemID(variables, packManager, pack, parts[0]);
            number = new DefaultVariable<>(NumberParser.DEFAULT.apply(parts[1]));
        } else {
            item = new ItemID(variables, packManager, pack, string);
            number = new DefaultVariable<>(1);
        }
        return new Item(featureApi, item, number);
    }
}
