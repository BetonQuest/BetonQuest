package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.menu.MenuItem;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Processor to create and store {@link MenuItem}s.
 */
public class MenuItemProcessor extends RPGMenuProcessor<MenuItemIdentifier, MenuItem> {

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
     * @param instructionApi    the instruction api to use
     * @param textCreator       the text creator to parse text
     * @param identifierFactory the identifier factory to create {@link MenuItemIdentifier}s for this type
     * @param questTypeApi      the QuestTypeApi
     * @param config            the config to load menu item options from
     * @param parsers           the argument parsers
     */
    public MenuItemProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                             final InstructionApi instructionApi, final ParsedSectionTextCreator textCreator,
                             final IdentifierFactory<MenuItemIdentifier> identifierFactory,
                             final QuestTypeApi questTypeApi, final ConfigAccessor config, final ArgumentParsers parsers) {
        super(log, instructionApi, "Menu Item", "menu_items", loggerFactory, textCreator, parsers, identifierFactory, questTypeApi);
        this.config = config;
    }

    @Override
    protected Map.Entry<MenuItemIdentifier, MenuItem> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
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
        final Argument<List<ConditionIdentifier>> conditions = instruction.read().value("conditions")
                .identifier(ConditionIdentifier.class).list().getOptional(Collections.emptyList());
        final String rawClose = section.getString("close", config.getString("menu.default_close", "false"));
        final Argument<Boolean> close = instruction.chainForArgument(rawClose).bool().get();

        final BetonQuestLogger log = loggerFactory.create(MenuItem.class);
        final MenuItemIdentifier menuItemID = getIdentifier(pack, sectionName);
        final MenuItem menuItem = new MenuItem(log, questTypeApi, item, menuItemID, descriptions, clickActions, conditions, close);
        return Map.entry(menuItemID, menuItem);
    }

    private Argument<List<ActionIdentifier>> getActionList(final SectionInstruction instruction, final String... path) throws QuestException {
        return instruction.read().value(path).identifier(ActionIdentifier.class).list().getOptional(Collections.emptyList());
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
}
