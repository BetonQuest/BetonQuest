package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.menu.MenuItem;
import org.betonquest.betonquest.menu.MenuItemID;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
     * @param parsers       the argument parsers
     */
    public MenuItemProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                             final QuestPackageManager packManager, final ParsedSectionTextCreator textCreator,
                             final QuestTypeApi questTypeApi, final ConfigAccessor config, final ArgumentParsers parsers) {
        super(log, packManager, "Menu Item", "menu_items", loggerFactory, textCreator, parsers, questTypeApi);
        this.packManager = packManager;
        this.config = config;
    }

    @Override
    protected Map.Entry<MenuItemID, MenuItem> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final ConfigurationSection section = instruction.getSection();
        final QuestPackage pack = instruction.getPackage();

        final String itemString = section.getString("item");
        if (itemString == null) {
            throw new QuestException("Item not specified for menu item '%s'".formatted(sectionName));
        }
        final String rawItemValue = itemString + ":" + section.getString("amount", "1");
        final Argument<ItemWrapper> item = instruction.chainForArgument(rawItemValue).item().get();
        final Text descriptions = section.contains(CONFIG_TEXT) ? textCreator.parseFromSection(pack, section, CONFIG_TEXT) : null;
        final MenuItem.ClickActions clickActions = getActions(instruction);
        final Argument<List<ConditionID>> conditions = instruction.read().value("conditions").parse(ConditionID::new).list().getOptional(Collections.emptyList());
        final String rawClose = section.getString("close", config.getString("menu.default_close", "false"));
        final Argument<Boolean> close = instruction.chainForArgument(rawClose).bool().get();

        final BetonQuestLogger log = loggerFactory.create(MenuItem.class);
        final MenuItemID menuItemID = getIdentifier(pack, sectionName);
        final MenuItem menuItem = new MenuItem(log, questTypeApi, item, menuItemID, descriptions, clickActions, conditions, close);
        return Map.entry(menuItemID, menuItem);
    }

    private Argument<List<ActionID>> getActionList(final SectionInstruction instruction, final String... path) throws QuestException {
        return instruction.read().value(path).parse(ActionID::new).list().getOptional(List.of());
    }

    private MenuItem.ClickActions getActions(final SectionInstruction instruction) throws QuestException {
        if (instruction.getSection().isConfigurationSection("click")) {
            return new MenuItem.ClickActions(
                    getActionList(instruction, "click.left"),
                    getActionList(instruction, "click.shiftLeft"),
                    getActionList(instruction, "click.right"),
                    getActionList(instruction, "click.shiftRight"),
                    getActionList(instruction, "click.middleMouse"));
        }
        return new MenuItem.ClickActions(getActionList(instruction, "click"));
    }

    @Override
    protected MenuItemID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new MenuItemID(packManager, pack, identifier);
    }
}
