package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.kernel.processor.PostLoadTask;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The default implementation of the {@link ProcessorDataLoader}.
 */
public class DefaultProcessorDataLoader implements ProcessorDataLoader {

    /**
     * The logger instance.
     */
    private final BetonQuestLogger log;

    /**
     * Contains all additional processors.
     */
    private final List<QuestProcessor<?, ?>> processors;

    /**
     * Create a new DefaultAdditionalDataLoader.
     *
     * @param log the logger instance
     */
    public DefaultProcessorDataLoader(final BetonQuestLogger log) {
        this.log = log;
        this.processors = new ArrayList<>();
    }

    @Override
    public void addProcessor(final QuestProcessor<?, ?> processor) {
        this.processors.add(processor);
    }

    @Override
    public void loadData(final Collection<QuestPackage> packages) {
        new LoadDataEvent(LoadDataEvent.State.PRE_LOAD).callEvent();
        log.debug("Loading processor data, clearing processors");
        this.processors.forEach(QuestProcessor::clear);
        packages.forEach(pack -> {
            log.debug(pack, "Loading package '%s' for processors.".formatted(pack.getQuestPath()));
            this.processors.forEach(processor -> processor.load(pack));
            log.debug(pack, "Package '%s' for processors loaded.".formatted(pack.getQuestPath()));
        });
        log.debug("Running post-load tasks");
        processors.stream().filter(processor -> processor instanceof PostLoadTask)
                .map(processor -> (PostLoadTask) processor)
                .forEach(PostLoadTask::startAll);
        log.debug("Finished running post-load tasks");
        final String readableSizes = processors.stream()
                .sorted(Comparator.comparing(QuestProcessor::getReadableName))
                .map(QuestProcessor::readableSize)
                .collect(Collectors.joining(", "));
        log.info("There are [%s] loaded from %s packages.".formatted(readableSizes, packages.size()));
        log.debug("Finished loading processor data");
        new LoadDataEvent(LoadDataEvent.State.POST_LOAD).callEvent();
    }
}
