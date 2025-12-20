package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.argument.types.ItemParser;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;

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
    protected final ItemParser itemParser;

    /**
     * Create a new Processor to create and store Menu Items.
     *
     * @param log           the custom logger for this class
     * @param packManager   the quest package manager to get quest packages from
     * @param readable      the type name used for logging, with the first letter in upper case
     * @param internal      the section name and/or bstats topic identifier
     * @param loggerFactory the logger factory to class specific loggers with
     * @param textCreator   the text creator to parse text
     * @param questTypeApi  the QuestTypeApi
     * @param featureApi    the Feature API
     */
    public RPGMenuProcessor(final BetonQuestLogger log, final QuestPackageManager packManager, final String readable,
                            final String internal, final BetonQuestLoggerFactory loggerFactory,
                            final ParsedSectionTextCreator textCreator,
                            final QuestTypeApi questTypeApi, final FeatureApi featureApi) {
        super(log, questTypeApi.variables(), packManager, readable, internal);
        this.loggerFactory = loggerFactory;
        this.textCreator = textCreator;
        this.questTypeApi = questTypeApi;
        this.itemParser = new ItemParser(featureApi);
    }

    /**
     * Class to bundle objects required for creation.
     */
    protected class CreationHelper {

        /**
         * Source Pack.
         */
        protected final QuestPackage pack;

        /**
         * Source Section.
         */
        protected final ConfigurationSection section;

        /**
         * Creates a new Creation Helper.
         *
         * @param pack    the pack to create from
         * @param section the section to create from
         */
        protected CreationHelper(final QuestPackage pack, final ConfigurationSection section) {
            this.pack = pack;
            this.section = section;
        }

        /**
         * Unresolved string from config file.
         *
         * @param path where to search
         * @return requested String
         * @throws QuestException if string is not given
         */
        protected String getRequired(final String path) throws QuestException {
            final String string = section.getString(path);
            if (string == null) {
                throw new QuestException(path + " is missing!");
            }
            return string;
        }

        /**
         * Parse a list of ids from config file.
         *
         * @param <U>      the id type
         * @param path     where to search
         * @param argument the argument converter
         * @return requested ids or empty list when not present
         * @throws QuestException if one of the ids can't be found
         */
        protected <U extends Identifier> VariableList<U> getID(final String path, final InstructionIdentifierArgument<U> argument)
                throws QuestException {
            return new VariableList<>(variables, pack, section.getString(path, ""),
                    value -> argument.apply(variables, packManager, pack, value));
        }
    }
}
