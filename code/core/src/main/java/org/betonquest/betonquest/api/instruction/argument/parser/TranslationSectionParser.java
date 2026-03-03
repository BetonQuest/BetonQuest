package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SubSectionArgumentParser;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

/**
 * Parses a section containing translations into a specific text.
 */
public class TranslationSectionParser implements SubSectionArgumentParser<Text> {

    /**
     * The parsed section text creator.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Creates a new parser for translation sections.
     *
     * @param textCreator the text creator
     */
    public TranslationSectionParser(final ParsedSectionTextCreator textCreator) {
        this.textCreator = textCreator;
    }

    @Override
    public Text parse(final SectionInstruction instruction) throws QuestException {
        return textCreator.parseFromSection(instruction.getPackage(), instruction.getSection(), null);
    }
}
