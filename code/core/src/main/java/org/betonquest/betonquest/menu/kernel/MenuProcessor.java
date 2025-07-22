package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.MenuItemID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.menu.Slots;
import org.betonquest.betonquest.menu.command.MenuBoundCommand;
import org.betonquest.betonquest.menu.command.SimpleCommand;
import org.betonquest.betonquest.message.ParsedSectionMessageCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Processor to create and store {@link Menu}s.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class MenuProcessor extends RPGMenuProcessor<MenuID, Menu> {
    /**
     * RPG Menu instance.
     */
    private final RPGMenu rpgMenu;

    /**
     * Profile Provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Commands to open specific menus.
     */
    private final Set<MenuBoundCommand> boundCommands;

    /**
     * Create a new Processor to create and store Menu Items.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to class specific loggers with
     * @param messageCreator    the message creator to parse messages
     * @param questTypeAPI      the QuestTypeAPI
     * @param variableProcessor the variable resolver
     * @param featureAPI        the Feature API
     * @param rpgMenu           the RPG Menu instance
     * @param profileProvider   the Profile Provider
     */
    public MenuProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                         final ParsedSectionMessageCreator messageCreator, final QuestTypeAPI questTypeAPI,
                         final VariableProcessor variableProcessor, final FeatureAPI featureAPI, final RPGMenu rpgMenu,
                         final ProfileProvider profileProvider) {
        super(log, "Menu", "menus", loggerFactory, messageCreator, variableProcessor, questTypeAPI, featureAPI);
        this.rpgMenu = rpgMenu;
        this.profileProvider = profileProvider;
        this.boundCommands = new HashSet<>();
    }

    @Override
    public void clear() {
        boundCommands.forEach(SimpleCommand::unregister);
        boundCommands.clear();
        super.clear();
    }

    @Override
    public Menu loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final MenuCreationHelper helper = new MenuCreationHelper(pack, section);
        final Menu.MenuData menuData = helper.getMenuData();
        final MenuID menuID = getIdentifier(pack, section.getName());
        final Variable<Item> boundItem = section.isSet("bind")
                ? new Variable<>(variableProcessor, pack, helper.getRequired("bind"), value -> itemParser.apply(pack, value))
                : null;
        final BetonQuestLogger log = loggerFactory.create(MenuID.class);
        final Menu menu = new Menu(log, menuID, questTypeAPI, menuData, boundItem);
        if (section.isSet("command")) {
            final String string = new Variable<>(variableProcessor, pack, helper.getRequired("command"),
                    Argument.STRING).getValue(null).trim();
            createBoundCommand(menu, string);
        }
        return menu;
    }

    private void createBoundCommand(final Menu menu, final String command)
            throws QuestException {
        if (!command.matches("/*[0-9A-Za-z\\-]+")) {
            throw new QuestException("command is invalid!");
        }
        final String shortened = command.startsWith("/") ? command.substring(1) : command;
        final MenuBoundCommand boundCommand = new MenuBoundCommand(loggerFactory.create(MenuBoundCommand.class),
                rpgMenu, profileProvider, menu, shortened);
        this.boundCommands.add(boundCommand);
        boundCommand.register();
    }

    @Override
    protected MenuID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new MenuID(pack, identifier);
    }

    /**
     * Class to bundle objects required to create a Menu.
     */
    private class MenuCreationHelper extends CreationHelper {

        /**
         * Creates a new Creation Helper.
         *
         * @param pack    the pack to create from
         * @param section the section to create from
         */
        protected MenuCreationHelper(final QuestPackage pack, final ConfigurationSection section) {
            super(pack, section);
        }

        private Menu.MenuData getMenuData() throws QuestException {
            final int height = new Variable<>(variableProcessor, pack, getRequired("height"), Argument.NUMBER)
                    .getValue(null).intValue();
            if (height < 1 || height > 6) {
                throw new QuestException("height is invalid!");
            }
            final Message title = messageCreator.parseFromSection(pack, section, "title");
            final Variable<List<ConditionID>> openConditions = getID("open_conditions", ConditionID::new);
            final Variable<List<EventID>> openEvents = getID("open_events", EventID::new);
            final Variable<List<EventID>> closeEvents = getID("close_events", EventID::new);

            final List<Slots> slots = loadSlots(height);
            return new Menu.MenuData(title, height, slots, openConditions, openEvents, closeEvents);
        }

        private List<Slots> loadSlots(final int height) throws QuestException {
            final ConfigurationSection slotsSection = section.getConfigurationSection("slots");
            if (slotsSection == null) {
                throw new QuestException("slots are missing!");
            }
            final List<Slots> slots = new ArrayList<>();
            for (final String key : slotsSection.getKeys(false)) {
                final Variable<List<MenuItemID>> itemsList = new VariableList<>(variableProcessor, pack,
                        slotsSection.getString(key, ""), value -> new MenuItemID(pack, value));
                try {
                    slots.add(new Slots(rpgMenu, key, itemsList));
                } catch (final IllegalArgumentException e) {
                    throw new QuestException("slots." + key + " is invalid: " + e.getMessage(), e);
                }
            }
            Slots.checkSlots(slots, height * 9);
            return slots;
        }
    }
}
