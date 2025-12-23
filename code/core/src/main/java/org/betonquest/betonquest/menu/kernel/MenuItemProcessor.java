package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.argument.parser.BooleanParser;
import org.betonquest.betonquest.api.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.menu.MenuItem;
import org.betonquest.betonquest.menu.MenuItemID;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
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
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Config to load menu options from.
     */
    private final ConfigAccessor config;

    /**
     * Create a new Processor to create and store Menu Items.
     *
     * @param log           the custom logger for this class
     * @param loggerFactory the logger factory to class specific loggers with
     * @param packManager   the quest package manager to get quest packages from
     * @param textCreator   the text creator to parse text
     * @param questTypeApi  the QuestTypeApi
     * @param config        the config to load menu item options from
     * @param featureApi    the Feature API
     */
    public MenuItemProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                             final QuestPackageManager packManager, final ParsedSectionTextCreator textCreator,
                             final QuestTypeApi questTypeApi, final ConfigAccessor config, final FeatureApi featureApi) {
        super(log, packManager, "Menu Item", "menu_items", loggerFactory, textCreator, questTypeApi, featureApi);
        this.packManager = packManager;
        this.config = config;
    }

    @Override
    protected MenuItem loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final MenuItemCreationHelper helper = new MenuItemCreationHelper(pack, section);
        final String itemString = helper.getRequired("item") + ":" + section.getString("amount", "1");
        final Variable<QuestItemWrapper> item = new DefaultVariable<>(variables, pack, itemString,
                value -> itemParser.apply(variables, packManager, pack, value));
        final Text descriptions;
        if (section.contains(CONFIG_TEXT)) {
            descriptions = textCreator.parseFromSection(pack, section, CONFIG_TEXT);
        } else {
            descriptions = null;
        }
        final MenuItem.ClickEvents clickEvents = helper.getClickEvents();
        final Variable<List<ConditionID>> conditions = helper.getID("conditions", ConditionID::new);
        final String rawClose = section.getString("close", config.getString("menu.default_close", "false"));
        final Variable<Boolean> close = new DefaultVariable<>(variables, pack, rawClose, new BooleanParser());
        final BetonQuestLogger log = loggerFactory.create(MenuItem.class);
        return new MenuItem(log, questTypeApi, item, getIdentifier(pack, section.getName()), descriptions, clickEvents, conditions, close);
    }

    @Override
    protected MenuItemID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new MenuItemID(packManager, pack, identifier);
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
