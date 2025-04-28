package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedSectionMessageCreator;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Does the load logic around {@link T} from a configuration section.
 *
 * @param <I> the {@link ID} identifying the type
 * @param <T> the type
 */
public abstract class RPGMenuProcessor<I extends ID, T> extends SectionProcessor<I, T> {

    /**
     * Logger Factory to create Menu Item Logger.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Message creator to parse messages.
     */
    protected final ParsedSectionMessageCreator messageCreator;

    /**
     * The QuestTypeAPI.
     */
    protected final QuestTypeAPI questTypeAPI;

    /**
     * The Variable Processor.
     */
    protected final VariableProcessor variableProcessor;

    /**
     * The Feature API.
     */
    protected final FeatureAPI featureAPI;

    /**
     * Create a new Processor to create and store Menu Items.
     *
     * @param log               the custom logger for this class
     * @param readable          the type name used for logging, with the first letter in upper case
     * @param internal          the section name and/or bstats topic identifier
     * @param loggerFactory     the logger factory to class specific loggers with
     * @param messageCreator    the message creator to parse messages
     * @param variableProcessor the variable resolver
     * @param questTypeAPI      the QuestTypeAPI
     * @param featureAPI        the Feature API
     */
    public RPGMenuProcessor(final BetonQuestLogger log, final String readable, final String internal,
                            final BetonQuestLoggerFactory loggerFactory, final ParsedSectionMessageCreator messageCreator,
                            final VariableProcessor variableProcessor, final QuestTypeAPI questTypeAPI, final FeatureAPI featureAPI) {
        super(log, readable, internal);
        this.loggerFactory = loggerFactory;
        this.messageCreator = messageCreator;
        this.questTypeAPI = questTypeAPI;
        this.variableProcessor = variableProcessor;
        this.featureAPI = featureAPI;
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
         * Parse string from config file.
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
            return GlobalVariableResolver.resolve(pack, string);
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
        protected <U extends ID> VariableList<U> getID(final String path, final PackageArgument<U> argument)
                throws QuestException {
            final String rawString = GlobalVariableResolver.resolve(pack, section.getString(path));
            if (rawString == null) {
                return new VariableList<>(List.of());
            }
            return new VariableList<>(variableProcessor, pack, rawString, value -> argument.apply(pack, value));
        }
    }
}
