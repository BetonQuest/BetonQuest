package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.section.DefaultSectionInstruction;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Does the load logic around {@link T} from a configuration section.
 *
 * @param <I> the {@link Identifier} identifying the type
 * @param <T> the type
 */
public abstract class SectionProcessor<I extends Identifier, T> extends QuestProcessor<I, T> {

    /**
     * The argument parsers to use for parsing arguments.
     */
    private final ArgumentParsers parsers;

    /**
     * The logger factory to create new class-specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     * @param log           the custom logger for this class
     * @param placeholders  the {@link Placeholders} to create and resolve placeholders
     * @param packManager   the quest package manager to get quest packages from
     * @param parsers       the {@link ArgumentParsers} to use for parsing arguments
     * @param readable      the type name used for logging, with the first letter in upper case
     * @param internal      the section name and/or bstats topic identifier
     */
    public SectionProcessor(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log, final Placeholders placeholders, final QuestPackageManager packManager,
                            final ArgumentParsers parsers, final String readable, final String internal) {
        super(log, placeholders, packManager, readable, internal);
        this.loggerFactory = loggerFactory;
        this.parsers = parsers;
    }

    @Override
    public void load(final QuestPackage pack) {
        final DefaultSectionInstruction instruction = new DefaultSectionInstruction(parsers, placeholders, packManager, pack, pack.getConfig(), loggerFactory);
        try {
            final Argument<List<Map.Entry<I, T>>> sections = instruction.read().list(internal).namedSections(this::loadSection)
                    .withoutEarlyValidation().getOptional(Collections.emptyList());
            final List<Map.Entry<I, T>> entries = sections.getValue(null);
            entries.forEach(entry -> {
                values.put(entry.getKey(), entry.getValue());
                log.debug(pack, "%s '%s' loaded".formatted(readable, entry.getKey()));
            });
        } catch (final QuestException e) {
            log.error(pack, "Could not load %ss: %s".formatted(readable, e.getMessage()), e);
        }
    }

    /**
     * Load all {@link T} from the SectionInstruction.
     * <p>
     * Any errors will be logged.
     *
     * @param sectionName the section name to use for identifiers
     * @param instruction the section instruction to parse the values
     * @return the loaded {@link T}
     * @throws QuestException if the loading fails
     */
    protected abstract Map.Entry<I, T> loadSection(String sectionName, SectionInstruction instruction) throws QuestException;

    /**
     * Get the loaded {@link T} by their ID.
     *
     * @return loaded values map, reflecting changes
     */
    public Map<I, T> getValues() {
        return values;
    }
}
