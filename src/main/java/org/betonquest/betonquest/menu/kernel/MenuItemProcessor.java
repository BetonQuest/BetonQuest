package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.menu.MenuItem;
import org.betonquest.betonquest.menu.MenuItemID;
import org.betonquest.betonquest.message.ParsedSectionMessageCreator;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Processor to create and store {@link MenuItem}s.
 */
public class MenuItemProcessor extends RPGMenuProcessor<MenuItemID, MenuItem> {
    /**
     * Text config property for Item lore.
     */
    private static final String CONFIG_TEXT = "text";

    /**
     * Config to load menu options from.
     */
    private final ConfigAccessor config;

    /**
     * Create a new Processor to create and store Menu Items.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to class specific loggers with
     * @param messageCreator    the message creator to parse messages
     * @param questTypeAPI      the QuestTypeAPI
     * @param config            the config to load menu item options from
     * @param variableProcessor the variable resolver
     * @param featureAPI        the Feature API
     */
    public MenuItemProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                             final ParsedSectionMessageCreator messageCreator, final QuestTypeAPI questTypeAPI,
                             final ConfigAccessor config, final VariableProcessor variableProcessor, final FeatureAPI featureAPI) {
        super(log, "Menu Item", "menu_items", loggerFactory, messageCreator, variableProcessor, questTypeAPI, featureAPI);
        this.config = config;
    }

    @Override
    protected MenuItem loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final MenuItemCreationHelper helper = new MenuItemCreationHelper(pack, section);
        final String itemString = helper.getRequired("item");
        final String amountString = GlobalVariableResolver.resolve(pack, section.getString("amount"));
        final Variable<Number> amount = amountString == null ? new Variable<>(1) : new Variable<>(variableProcessor, pack, amountString, Argument.NUMBER);
        final Item item = new Item(featureAPI, new ItemID(pack, itemString), amount);
        final Message descriptions;
        if (section.contains(CONFIG_TEXT)) {
            descriptions = messageCreator.parseFromSection(pack, section, CONFIG_TEXT);
        } else {
            log.debug(pack, "No description for menu item '" + pack.getQuestPath() + "." + section.getName() + "'");
            descriptions = null;
        }
        final MenuItem.ClickEvents clickEvents = helper.getClickEvents();
        final Variable<List<ConditionID>> conditions = helper.getID("conditions", ConditionID::new);
        final String rawClose = section.getString("close", config.getString("default_close", ""));
        final boolean close = Argument.BOOLEAN.apply(GlobalVariableResolver.resolve(pack, rawClose));
        final BetonQuestLogger log = loggerFactory.create(MenuItem.class);
        return new MenuItem(log, questTypeAPI, item, getIdentifier(pack, section.getName()), descriptions, clickEvents, conditions, close);
    }

    @Override
    protected MenuItemID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new MenuItemID(pack, identifier);
    }

    /**
     * Class to bundle objects required to create a MenuItem.
     */
    private class MenuItemCreationHelper extends CreationHelper {

        /**
         * Creates a new Creation Helper.
         *
         * @param pack    the pack to create from
         * @param section the section to create from
         */
        protected MenuItemCreationHelper(final QuestPackage pack, final ConfigurationSection section) {
            super(pack, section);
        }

        private MenuItem.ClickEvents getClickEvents() throws QuestException {
            if (section.isConfigurationSection("click")) {
                return new MenuItem.ClickEvents(
                        getEvents("click.left"),
                        getEvents("click.shiftLeft"),
                        getEvents("click.right"),
                        getEvents("click.shiftRight"),
                        getEvents("click.middleMouse")
                );
            } else {
                return new MenuItem.ClickEvents(getEvents("click"));
            }
        }

        private Variable<List<EventID>> getEvents(final String key) throws QuestException {
            return getID(key, EventID::new);
        }
    }
}
