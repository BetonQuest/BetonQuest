package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.menu.Slots;
import org.betonquest.betonquest.menu.command.MenuBoundCommand;
import org.betonquest.betonquest.menu.command.SimpleCommand;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Processor to create and store {@link Menu}s.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class MenuProcessor extends RPGMenuProcessor<MenuIdentifier, Menu> {

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
     * @param instructionApi    the instruction api to use
     * @param textCreator       the text creator to parse text
     * @param actionManager     the ActionManager
     * @param conditionManager  the ConditionManager
     * @param parsers           the argument parsers
     * @param rpgMenu           the RPG Menu instance
     * @param identifierFactory the identifier factory to create {@link MenuIdentifier}s for this type
     * @param profileProvider   the Profile Provider
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public MenuProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                         final Instructions instructionApi, final ParsedSectionTextCreator textCreator,
                         final ActionManager actionManager, final ConditionManager conditionManager, final ArgumentParsers parsers, final RPGMenu rpgMenu,
                         final IdentifierFactory<MenuIdentifier> identifierFactory, final ProfileProvider profileProvider) {
        super(log, instructionApi, "Menu", "menus", loggerFactory, textCreator, parsers, identifierFactory, actionManager, conditionManager);
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
    protected Map.Entry<MenuIdentifier, Menu> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final MenuIdentifier identifier = getIdentifier(pack, sectionName);
        final ConfigurationSection section = instruction.getSection();
        final BetonQuestLogger log = loggerFactory.create(Menu.class);
        final Text title = textCreator.parseFromSection(pack, section, "title");

        final Argument<Number> height = instruction.read().value("height").number().atLeast(1).atMost(6).get();
        final int heightValue = height.getValue(null).intValue();
        final Argument<List<ConditionIdentifier>> openConditions = instruction.read().value("open_conditions")
                .identifier(ConditionIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<List<ActionIdentifier>> openActions = instruction.read().value("open_actions")
                .identifier(ActionIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<List<ActionIdentifier>> closeActions = instruction.read().value("close_actions")
                .identifier(ActionIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<List<Slots>> slots = instruction.read().list("slots").namedStrings()
                .map(list -> loadSlots(instruction::chainForArgument, list))
                .validate(list -> Slots.checkSlots(list, heightValue * 9))
                .get();
        final Menu.MenuData menuData = new Menu.MenuData(title, heightValue, slots, openConditions, openActions, closeActions);
        final Menu menu = new Menu(log, identifier, actionManager, conditionManager, menuData, getItems(instruction));
        final Argument<String> command = instruction.read().value("command").string().getOptional("");
        final String commandValue = command.getValue(null);
        if (!commandValue.isEmpty()) {
            createBoundCommand(menu, commandValue);
        }
        return Map.entry(identifier, menu);
    }

    private List<Slots> loadSlots(final Function<String, InstructionChainParser> parserFunction,
                                  final List<Map.Entry<String, String>> slots) throws QuestException {
        final List<Slots> loadedSlots = new ArrayList<>();
        for (final Map.Entry<String, String> slot : slots) {
            final Argument<List<MenuItemIdentifier>> items = parserFunction.apply(slot.getValue())
                    .identifier(MenuItemIdentifier.class).list().get();
            loadedSlots.add(new Slots(rpgMenu, slot.getKey(), items));
        }
        return loadedSlots;
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

    @Nullable
    private Argument<ItemWrapper> getItem(final SectionInstruction instruction, final String path) throws QuestException {
        if (instruction.getSection().isSet(path)) {
            return instruction.read().value(path).item().get();
        }
        return null;
    }

    @Nullable
    private Menu.BoundItems getItems(final SectionInstruction instruction) throws QuestException {
        if (!instruction.getSection().isSet("bind")) {
            return null;
        }
        if (instruction.getSection().isConfigurationSection("bind")) {
            return new Menu.BoundItems(
                    getItem(instruction, "bind.left"),
                    getItem(instruction, "bind.sneakLeft"),
                    getItem(instruction, "bind.right"),
                    getItem(instruction, "bind.sneakRight"));
        }
        return new Menu.BoundItems(getItem(instruction, "bind"));
    }
}
