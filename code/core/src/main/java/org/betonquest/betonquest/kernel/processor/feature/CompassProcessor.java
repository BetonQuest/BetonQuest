package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Location;

import java.util.Map;
import java.util.Optional;

/**
 * Loads and stores {@link QuestCompass}es.
 */
public class CompassProcessor extends SectionProcessor<CompassIdentifier, QuestCompass> {

    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Create a new QuestProcessor to store {@link QuestCompass}es.
     *
     * @param loggerFactory     the logger factory to create new class-specific loggers
     * @param log               the custom logger for this class
     * @param packManager       the quest package manager to get quest packages from
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param textCreator       the text creator to parse text
     * @param identifierFactory the identifier factory to create {@link CompassIdentifier}s for this type
     * @param parsers           the {@link ArgumentParsers} to use for parsing arguments
     */
    public CompassProcessor(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log, final Placeholders placeholders, final QuestPackageManager packManager,
                            final ParsedSectionTextCreator textCreator, final IdentifierFactory<CompassIdentifier> identifierFactory, final ArgumentParsers parsers) {
        super(loggerFactory, log, placeholders, packManager, parsers, identifierFactory, "Compass", "compass");
        this.textCreator = textCreator;
    }

    @Override
    protected Map.Entry<CompassIdentifier, QuestCompass> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final Text name = textCreator.parseFromSection(pack, instruction.getSection(), "name");
        final Argument<Location> location = instruction.read().value("location").location().get();
        final Optional<Argument<ItemIdentifier>> item = instruction.read().value("item").identifier(ItemIdentifier.class).getOptional();
        final QuestCompass compass = new QuestCompass(name, location, item.isEmpty() ? null : item.get().getValue(null));
        return Map.entry(getIdentifier(pack, sectionName), compass);
    }
}
