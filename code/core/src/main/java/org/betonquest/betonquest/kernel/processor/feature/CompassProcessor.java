package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.feature.DefaultQuestCompass;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Location;

import java.util.Map;
import java.util.Optional;

/**
 * Loads and stores {@link DefaultQuestCompass}es.
 */
public class CompassProcessor extends SectionProcessor<CompassIdentifier, DefaultQuestCompass> {

    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Create a new QuestProcessor to store {@link DefaultQuestCompass}es.
     *
     * @param log               the custom logger for this class
     * @param instructionApi    the instruction api to use
     * @param textCreator       the text creator to parse text
     * @param identifierFactory the identifier factory to create {@link CompassIdentifier}s for this type
     */
    public CompassProcessor(final BetonQuestLogger log, final Instructions instructionApi,
                            final ParsedSectionTextCreator textCreator, final IdentifierFactory<CompassIdentifier> identifierFactory) {
        super(log, instructionApi, identifierFactory, "Compass", "compass");
        this.textCreator = textCreator;
    }

    @Override
    protected Map.Entry<CompassIdentifier, DefaultQuestCompass> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final Text name = textCreator.parseFromSection(pack, instruction.getSection(), "name");
        final Argument<Location> location = instruction.read().value("location").location().get();
        final Optional<Argument<ItemIdentifier>> item = instruction.read().value("item").identifier(ItemIdentifier.class).getOptional();
        final DefaultQuestCompass compass = new DefaultQuestCompass(name, location, item.isEmpty() ? null : item.get().getValue(null));
        return Map.entry(getIdentifier(pack, sectionName), compass);
    }
}
