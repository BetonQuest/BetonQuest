package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;

import java.util.Collection;

/**
 * Interface DataLoader manages the loading of data in all processors.
 */
public interface ProcessorDataLoader {

    /**
     * Adds a processor for additional features to the data loader.
     *
     * @param processor the processor to add
     */
    void addProcessor(QuestProcessor<?, ?> processor);

    /**
     * Triggers the loading of the data in all processors.
     * Causes all previously loaded data to be discarded.
     *
     * @param packages the packages to load
     */
    void loadData(Collection<QuestPackage> packages);
}
