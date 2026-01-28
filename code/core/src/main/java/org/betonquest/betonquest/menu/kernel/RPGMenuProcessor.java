package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

/**
 * Does the load logic around {@link T} from a configuration section.
 *
 * @param <I> the {@link Identifier} identifying the type
 * @param <T> the type
 */
public abstract class RPGMenuProcessor<I extends Identifier, T> extends SectionProcessor<I, T> {

    /**
     * Logger Factory to create Menu Item Logger.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Text creator to parse text.
     */
    protected final ParsedSectionTextCreator textCreator;

    /**
     * The QuestTypeApi.
     */
    protected final QuestTypeApi questTypeApi;

    /**
     * The Item Parser instance.
     */
    protected final InstructionArgumentParser<ItemWrapper> itemParser;

    /**
     * Create a new Processor to create and store Menu Items.
     *
     * @param log               the custom logger for this class
     * @param packManager       the quest package manager to get quest packages from
     * @param readable          the type name used for logging, with the first letter in upper case
     * @param internal          the section name and/or bstats topic identifier
     * @param loggerFactory     the logger factory to class specific loggers with
     * @param textCreator       the text creator to parse text
     * @param parsers           the argument parsers
     * @param identifierFactory the identifier factory
     * @param questTypeApi      the QuestTypeApi
     */
    public RPGMenuProcessor(final BetonQuestLogger log, final QuestPackageManager packManager, final String readable,
                            final String internal, final BetonQuestLoggerFactory loggerFactory,
                            final ParsedSectionTextCreator textCreator, final ArgumentParsers parsers,
                            final IdentifierFactory<I> identifierFactory, final QuestTypeApi questTypeApi) {
        super(loggerFactory, log, questTypeApi.placeholders(), packManager, parsers, identifierFactory, readable, internal);
        this.loggerFactory = loggerFactory;
        this.textCreator = textCreator;
        this.questTypeApi = questTypeApi;
        this.itemParser = parsers.item();
    }
}
