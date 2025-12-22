package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.ItemParser;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.compatibility.holograms.lines.ItemLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TextLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopXObject;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hides and shows holograms to players, based on conditions.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public abstract class HologramLoop extends SectionProcessor<HologramLoop.HologramID, HologramWrapper> {

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
    private final ItemParser itemParser;

    /**
     * Default refresh Interval for Holograms.
     */
    private int defaultInterval = 10 * 20;

    /**
     * Creates a new instance of the loop.
     *
     * @param loggerFactory    logger factory to use
     * @param log              the logger that will be used for logging
     * @param packManager      the quest package manager to get quest packages from
     * @param variables        the variable processor to create and resolve variables
     * @param hologramProvider the hologram provider to create new holograms
     * @param readable         the type name used for logging, with the first letter in upper case
     * @param internal         the section name and/or bstats topic identifier
     * @param textParser       the text parser used to parse text and colors
     */
    public HologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                        final Variables variables, final QuestPackageManager packManager,
                        final HologramProvider hologramProvider, final String readable, final String internal, final TextParser textParser) {
        super(log, variables, packManager, readable, internal);
        this.loggerFactory = loggerFactory;
        this.hologramProvider = hologramProvider;
        this.textParser = textParser;
        this.itemParser = new ItemParser(BetonQuest.getInstance().getFeatureApi());
    }

    @Override
    public void clear() {
        super.clear();
        defaultInterval = BetonQuest.getInstance().getPluginConfig().getInt("hologram.update_interval", 10 * 20);
    }

    @Override
    protected HologramWrapper loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final String checkIntervalString = section.getString("check_interval", String.valueOf(defaultInterval));
        final Variable<Number> checkInterval = new DefaultVariable<>(variables, pack, checkIntervalString, NumberParser.DEFAULT);
        final Variable<Number> maxRange = new DefaultVariable<>(variables, pack, section.getString("max_range", "0"), NumberParser.DEFAULT);

        final List<String> lines = section.getStringList("lines");
        final List<ConditionID> conditions = new VariableList<>(variables, pack, section.getString("conditions", ""),
                value -> new ConditionID(variables, packManager, pack, value)).getValue(null);

        final List<AbstractLine> cleanedLines = new ArrayList<>();
        for (final String line : lines) {
            if (line.startsWith("item:")) {
                cleanedLines.add(parseItemLine(pack, line.substring("item:".length())));
            } else if (line.startsWith("top:")) {
                cleanedLines.add(parseTopLine(pack, line.substring("top:".length()), section.getName()));
            } else {
                cleanedLines.add(parseTextLine(pack, line));
            }
        }
        final List<BetonHologram> holograms = getHologramsFor(pack, section);
        for (final BetonHologram hologram : holograms) {
            hologram.hideAll();
        }
        final HologramWrapper hologramWrapper = new HologramWrapper(
                loggerFactory.create(HologramWrapper.class),
                BetonQuest.getInstance().getQuestTypeApi(),
                BetonQuest.getInstance().getProfileProvider(),
                checkInterval.getValue(null).intValue(),
                holograms,
                isStaticHologram(cleanedLines),
                conditions,
                cleanedLines,
                pack,
                maxRange);
        HologramRunner.addHologram(hologramWrapper);
        return hologramWrapper;
    }

    /**
     * Creates and returns a list of holograms for the given section.
     *
     * @param pack    the package of the holograms
     * @param section the section of the holograms
     * @return a list of holograms
     * @throws QuestException if there is an error while parsing the holograms
     */
    protected abstract List<BetonHologram> getHologramsFor(QuestPackage pack, ConfigurationSection section) throws QuestException;

    private boolean isStaticHologram(final List<AbstractLine> lines) {
        return lines.stream().noneMatch(AbstractLine::isNotStaticText);
    }

    private ItemLine parseItemLine(final QuestPackage pack, final String line) throws QuestException {
        try {
            return new ItemLine(itemParser.apply(variables, packManager, pack, line).generate(null));
        } catch (final QuestException e) {
            throw new QuestException("Error while loading item: " + e.getMessage(), e);
        }
    }

    private TopLine parseTopLine(final QuestPackage pack, final String line, final String name) throws QuestException {
        final Matcher validator = TOP_LINE_VALIDATOR.matcher(line);
        if (!validator.matches()) {
            throw new QuestException("Malformed top line in hologram! Expected format: 'top:<point>;<order>;<limit>;<formattingString>'.");
        }

        final String pointName = PackageArgument.IDENTIFIER.apply(pack, validator.group(1));
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
        } else if (ORDER_ASC.equalsIgnoreCase(type)) {
            return TopXObject.OrderType.ASCENDING;
        } else {
            throw new QuestException("Top list order type '" + type + "' unknown! Expected 'asc' or 'desc'.");
        }
    }

    private TextLine parseTextLine(final QuestPackage pack, final String line) throws QuestException {
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(line);
        final String text = matcher.find()
                ? hologramProvider.parseVariable(pack, line)
                : line;
        return new TextLine(textParser.parse(text));
    }

    @Override
    protected HologramID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new HologramID(packManager, pack, identifier);
    }

    /**
     * Internal identifier/key for a Hologram.
     */
    protected static class HologramID extends DefaultIdentifier {

        /**
         * Creates a new ID.
         *
         * @param packManager the quest package manager to get quest packages from
         * @param pack        the package the ID is in
         * @param identifier  the id instruction string
         * @throws QuestException if the ID could not be parsed
         */
        protected HologramID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
            super(packManager, pack, identifier);
        }
    }
}
