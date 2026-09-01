package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.LoreMetaHandler;
import org.betonquest.betonquest.item.handler.NameMetaHandler;
import org.betonquest.betonquest.item.typehandler.BannerHandler;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
import org.betonquest.betonquest.item.typehandler.CustomModelDataHandler;
import org.betonquest.betonquest.item.typehandler.DurabilityHandler;
import org.betonquest.betonquest.item.typehandler.EnchantmentsHandler;
import org.betonquest.betonquest.item.typehandler.FireworkHandler;
import org.betonquest.betonquest.item.typehandler.FlagHandler;
import org.betonquest.betonquest.item.typehandler.HeadHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Holds the handlers for de-/serialization of the simple quest item and allows to modify them.
 */
public class SimpleQuestItemHandlerRegistry {

    /**
     * All other meta handler to parse attributes of the simple item.
     */
    private final Map<String, ItemMetaHandler<?>> handlers = new LinkedHashMap<>();

    /**
     * Name meta handler for {@link QuestItem#getName()}.
     */
    private NameMetaHandler nameMetaHandler;

    /**
     * Lore meta handler for {@link QuestItem#getLore()}.
     */
    private LoreMetaHandler loreMetaHandler;

    /**
     * Cached return value.
     */
    @Nullable
    private Baked baked;

    /**
     * Creates a new HandlerRegistry with default Handlers.
     *
     * @param bookPageWrapper the book page wrapper used to split pages
     * @param questItemLore   the consumer to use when the item to generate is a quest item
     */
    public SimpleQuestItemHandlerRegistry(final BookPageWrapper bookPageWrapper, final Argument<LoreConsumer> questItemLore) {
        this.nameMetaHandler = new NameHandler();

        final QuestHandler questHandler = new QuestHandler(questItemLore);
        this.loreMetaHandler = new LoreHandler(questHandler);
        List.of(
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                new EnchantmentsHandler(),
                new PotionHandler(),
                new BannerHandler(),
                new BookHandler(bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        ).forEach(handler -> handler.keys().forEach(key -> handlers.put(key, handler)));
    }

    /**
     * Registers a new handler.
     * <p>
     * {@link NameMetaHandler} and {@link LoreMetaHandler} must be distinct.
     * If the handler has a key a different already claims, the old handler is unregistered for all its keys.
     *
     * @param handler the handler to register
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void register(final ItemMetaHandler<?> handler) {
        if (handler instanceof NameMetaHandler) {
            nameMetaHandler = (NameMetaHandler) handler;
        } else if (handler instanceof LoreMetaHandler) {
            loreMetaHandler = (LoreMetaHandler) handler;
        } else {
            for (final String key : handler.keys()) {
                final ItemMetaHandler<?> old = handlers.put(key, handler);
                handlers.values().removeIf(value -> value == old);
            }
        }
        baked = null;
    }

    /**
     * Gets the handlers for de-/serialization of the simple quest item.
     *
     * @return the stored handlers
     */
    public Baked get() {
        if (baked != null) {
            return baked;
        }
        baked = new Baked(nameMetaHandler, loreMetaHandler, List.copyOf(new LinkedHashSet<>(handlers.values())));
        return baked;
    }

    /**
     * Snapshot of the registered handlers.
     *
     * @param name     Name meta handler for {@link QuestItem#getName()}.
     * @param lore     Lore meta handler for {@link QuestItem#getLore()}.
     * @param handlers All other meta handler to parse attributes of the simple item.
     */
    public record Baked(NameMetaHandler name, LoreMetaHandler lore, List<ItemMetaHandler<?>> handlers) {

    }
}
