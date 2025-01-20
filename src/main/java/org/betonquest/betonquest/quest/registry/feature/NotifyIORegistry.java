package org.betonquest.betonquest.quest.registry.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.quest.registry.FromClassFactoryRegistry;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Stores the Notify IOs that can be used in BetonQuest.
 */
public class NotifyIORegistry extends FromClassFactoryRegistry<NotifyIO, NotifyIORegistry.NotifyIOFactory> {

    /**
     * Create a new NotifyIO registry.
     *
     * @param log the logger that will be used for logging
     */
    public NotifyIORegistry(final BetonQuestLogger log) {
        super(log, "Notify IO");
    }

    @Override
    protected NotifyIOFactory createFactory(final Class<? extends NotifyIO> clazz) throws NoSuchMethodException {
        return new FactoryImpl(clazz.getConstructor(QuestPackage.class, Map.class));
    }

    /**
     * Creates Notify IOs from QuestPackage and configuration data.
     */
    public interface NotifyIOFactory {
        /**
         * Create the Notify IO.
         *
         * @param pack         the source pack
         * @param categoryData the configuration data
         * @return the created notify io
         * @throws QuestException when the creation fails
         */
        NotifyIO parse(QuestPackage pack, Map<String, String> categoryData) throws QuestException;
    }

    /**
     * Class Constructor based implementation.
     *
     * @param constructor the used constructor
     */
    private record FactoryImpl(Constructor<? extends NotifyIO> constructor) implements NotifyIOFactory {

        @Override
        public NotifyIO parse(final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
            return catchConstructionException("Notify IO", constructor, pack, categoryData);
        }
    }
}
