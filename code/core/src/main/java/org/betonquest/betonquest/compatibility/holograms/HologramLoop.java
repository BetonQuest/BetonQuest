package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.PackageIdentifierParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.compatibility.holograms.lines.ItemLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TextLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopXObject;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hides and shows holograms to players, based on conditions.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public abstract class HologramLoop extends SectionProcessor<HologramIdentifier, HologramWrapper> {

    /**
     * Pattern to match the correct syntax for the top line content.
     */
    private static final Pattern TOP_LINE_VALIDATOR = Pattern.compile("^([\\w>]+);(\\w+);(\\d+);(.+)$",
            Pattern.CASE_INSENSITIVE);

    /**
     * The string to match the descending order.
     */
    private static final String ORDER_DESC = "desc";

    /**
     * The string to match the ascending order.
     */
    private static final String ORDER_ASC = "asc";

    /**
     * Hologram provider to create new Holograms.
     */
    protected final HologramProvider hologramProvider;

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The text parser used to parse text and colors.
     */
    private final TextParser textParser;

    /**
     * Parser for the item line.
     */
    private final InstructionArgumentParser<ItemWrapper> itemParser;

    /**
     * Default refresh Interval for Holograms.
     */
    private int defaultInterval = 10 * 20;

    /**
     * Creates a new instance of the loop.
     *
     * @param loggerFactory     logger factory to use
     * @param log               the logger that will be used for logging
     * @param packManager       the quest package manager to get quest packages from
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param hologramProvider  the hologram provider to create new holograms
     * @param readable          the type name used for logging, with the first letter in upper case
     * @param internal          the section name and/or bstats topic identifier
     * @param textParser        the text parser used to parse text and colors
     * @param parsers           the argument parsers
     * @param identifierFactory the identifier factory to create {@link HologramIdentifier}s for this type
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public HologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                        final Placeholders placeholders, final QuestPackageManager packManager,
                        final HologramProvider hologramProvider, final String readable, final String internal,
                        final TextParser textParser, final ArgumentParsers parsers, final IdentifierFactory<HologramIdentifier> identifierFactory) {
        super(loggerFactory, log, placeholders, packManager, parsers, identifierFactory, readable, internal);
        this.loggerFactory = loggerFactory;
        this.hologramProvider = hologramProvider;
        this.textParser = textParser;
        this.itemParser = parsers.item();
    }

    @Override
    public void clear() {
        super.clear();
        defaultInterval = BetonQuest.getInstance().getPluginConfig().getInt("hologram.update_interval", 10 * 20);
    }

    @Override
    protected Map.Entry<HologramIdentifier, HologramWrapper> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final ConfigurationSection section = instruction.getSection();

        final Argument<Number> checkInterval = instruction.read().value("check_interval").number().getOptional(defaultInterval);
        final Argument<Number> maxRange = instruction.read().value("max_range").number().getOptional(0);
        final Argument<List<ConditionIdentifier>> conditions = instruction.read().value("conditions")
                .identifier(ConditionIdentifier.class).list().getOptional(Collections.emptyList());

        final List<BetonHologram> holograms = getHologramsFor(instruction);
        for (final BetonHologram hologram : holograms) {
            hologram.hideAll();
        }
        final List<String> lines = section.getStringList("lines");
        final List<AbstractLine> cleanedLines = new ArrayList<>();
        for (final String line : lines) {
            final AbstractLine abstractLine = parseLine(pack, sectionName, line);
            cleanedLines.add(abstractLine);
        }
        final HologramIdentifier identifier = getIdentifier(pack, sectionName);
        final QuestExceptionHandler handler = new QuestExceptionHandler(pack, loggerFactory.create(HologramWrapper.class), identifier.getFull());
        final HologramWrapper hologramWrapper = new HologramWrapper(
                handler,
                BetonQuest.getInstance().getQuestTypeApi(),
                BetonQuest.getInstance().getProfileProvider(),
                checkInterval.getValue(null).intValue(),
                holograms,
                isStaticHologram(cleanedLines),
                conditions.getValue(null),
                cleanedLines,
                pack,
                maxRange);
        HologramRunner.addHologram(hologramWrapper);
        return Map.entry(identifier, hologramWrapper);
    }

    private AbstractLine parseLine(final QuestPackage pack, final String sectionName, final String line) throws QuestException {
        if (line.startsWith("item:")) {
            return parseItemLine(pack, line.substring("item:".length()));
        }
        if (line.startsWith("top:")) {
            return parseTopLine(pack, line.substring("top:".length()), sectionName);
        }
        return parseTextLine(pack, line);
    }

    /**
     * Creates and returns a list of holograms for the given section.
     *
     * @param instruction the section instruction
     * @return a list of holograms
     * @throws QuestException if there is an error while parsing the holograms
     */
    protected abstract List<BetonHologram> getHologramsFor(SectionInstruction instruction) throws QuestException;

    private boolean isStaticHologram(final List<AbstractLine> lines) {
        return lines.stream().noneMatch(AbstractLine::isNotStaticText);
    }

    private ItemLine parseItemLine(final QuestPackage pack, final String line) throws QuestException {
        try {
            return new ItemLine(itemParser.apply(placeholders, packManager, pack, line).generate(null));
        } catch (final QuestException e) {
            throw new QuestException("Error while loading item: " + e.getMessage(), e);
        }
    }

    private TopLine parseTopLine(final QuestPackage pack, final String line, final String name) throws QuestException {
        final Matcher validator = TOP_LINE_VALIDATOR.matcher(line);
        if (!validator.matches()) {
            throw new QuestException("Malformed top line in hologram! Expected format: 'top:<point>;<order>;<limit>;<formattingString>'.");
        }

        final String pointName = PackageIdentifierParser.INSTANCE.apply(pack, validator.group(1));
        final TopXObject.OrderType orderType = orderType(validator.group(2));

        final int limit;
        try {
            limit = Integer.parseInt(validator.group(3));
        } catch (final NumberFormatException e) {
            throw new QuestException("Top list limit must be numeric! Expected format: 'top:<point>;<order>;<limit>;<formattingString>'.", e);
        }

        final String formattingGroup = validator.group(4);
        for (final String placeholder : List.of("{place}", "{name}", "{score}")) {
            if (!formattingGroup.contains(placeholder)) {
                log.debug(pack, "Hologram '" + name + "' in pack '" + pack + "' does not contain placeholder '" + placeholder + "'.");
            }
        }
        return new TopLine(loggerFactory, pointName, orderType, limit, new VariableComponent(textParser.parse(formattingGroup)));
    }

    private TopXObject.OrderType orderType(final String type) throws QuestException {
        if (ORDER_DESC.equalsIgnoreCase(type)) {
            return TopXObject.OrderType.DESCENDING;
        }
        if (ORDER_ASC.equalsIgnoreCase(type)) {
            return TopXObject.OrderType.ASCENDING;
        }
        throw new QuestException("Top list order type '" + type + "' unknown! Expected 'asc' or 'desc'.");
    }

    private TextLine parseTextLine(final QuestPackage pack, final String line) throws QuestException {
        final Matcher matcher = HologramProvider.PLACEHOLDER_VALIDATOR.matcher(line);
        final String text = matcher.find()
                ? hologramProvider.parsePlaceholder(pack, line)
                : line;
        return new TextLine(textParser.parse(text));
    }
}
