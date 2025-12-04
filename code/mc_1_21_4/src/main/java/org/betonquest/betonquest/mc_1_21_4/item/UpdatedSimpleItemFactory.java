package org.betonquest.betonquest.mc_1_21_4.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.SimpleQuestItem;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
import org.betonquest.betonquest.item.typehandler.DurabilityHandler;
import org.betonquest.betonquest.item.typehandler.EnchantmentsHandler;
import org.betonquest.betonquest.item.typehandler.FireworkHandler;
import org.betonquest.betonquest.item.typehandler.FlagHandler;
import org.betonquest.betonquest.item.typehandler.HeadHandler;
import org.betonquest.betonquest.item.typehandler.ItemMetaHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedNameHandler;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedPotionHandler;
import org.betonquest.betonquest.util.BlockSelector;

import java.util.List;
import java.util.function.Supplier;

/**
 * Creates {@link SimpleQuestItem}s from {@link Instruction}s.
 */
public class UpdatedSimpleItemFactory extends SimpleQuestItemFactory {

    /**
     * Creates a new simple Quest Item Factory.
     *
     * @param variables             the variable processor to create and resolve variables
     * @param packManager           the quest package manager to get quest packages from
     * @param textParser            the text parser used to parse text
     * @param bookPageWrapper       the book page wrapper used to split pages
     * @param questItemLoreSupplier supplies the plugin message instance if the "quest item" lore line should be added
     */
    public UpdatedSimpleItemFactory(final Variables variables, final QuestPackageManager packManager, final TextParser textParser,
                                    final BookPageWrapper bookPageWrapper, final Supplier<PluginMessage> questItemLoreSupplier) {
        super(variables, packManager, textParser, bookPageWrapper, questItemLoreSupplier);
    }

    @Override
    protected QuestItem parseInstruction(final String material, final List<String> arguments) throws QuestException {
        final BlockSelector selector = new BlockSelector(material);

        final NameHandler name = new UpdatedNameHandler(textParser);
        final LoreHandler lore = new LoreHandler(textParser);

        final PluginMessage pluginMessage = questItemLoreSupplier.get();
        final List<ItemMetaHandler<?>> handlers = List.of(
                new QuestHandler(pluginMessage == null ? QuestHandler.LoreConsumer.EMPTY : new QuestHandler.Lore(pluginMessage)),
                new DurabilityHandler(),
                new UpdatedCustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                name,
                lore,
                new EnchantmentsHandler(),
                new UpdatedPotionHandler(),
                new BookHandler(textParser, bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );

        if (!arguments.isEmpty()) {
            fillHandler(handlers, arguments);
        }
        return new SimpleQuestItem(selector, handlers, name, lore);
    }
}
