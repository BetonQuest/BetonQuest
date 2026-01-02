package org.betonquest.betonquest.api.instruction.section.subsection;

import org.betonquest.betonquest.api.instruction.section.path.DecoratableSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.path.ListSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.path.SectionParser;

import java.util.List;
import java.util.Map;

public interface SubSectionRedirector {

    /**
     * Preparses the entire section into a string to be parsed with the regular chain afterwards.
     *
     * @param preparser the section parser converting the section into a single string
     * @return a new {@link SectionParser} that continues with the section as a single string
     */
    SectionParser preparse(SubSectionArgumentParser<String> preparser);

    /**
     * Preparses the value as a configuration list of strings into a String containing a list of those values
     * just to be parsed with the regular chain afterwards.
     * This step is required for configuration lists to be parsed correctly.
     *
     * @return a new {@link SectionParser} that continues with the section as a list of strings
     */
    SectionParser prelist();

    /**
     * Parses the section into a value of type T using the given parser.
     *
     * @param parser the parser to use
     * @param <T>    the type of the parsed value
     * @return a new {@link DecoratableSectionRetriever} for the parsed value
     */
    <T> DecoratableSectionRetriever<T> parse(SubSectionArgumentParser<T> parser);

    /**
     * Parses the section by list all children elements and their values into a map.
     * The map keys are the names of the children elements.
     * Uses {@link org.bukkit.configuration.ConfigurationSection#getValues(boolean)} with the given deep parameter.
     *
     * @param deep if set to true, also parses nested sections
     * @return a new {@link DecoratableSectionRetriever} for the parsed map
     */
    DecoratableSectionRetriever<Map<String, Object>> map(boolean deep);

    /**
     * Parses the section as a configuration list of values into a list of values of type T using the given parser.
     *
     * @param parser the parser to use
     * @param <T>    the type of the parsed list's elements
     * @return a new {@link ListSectionRetriever} for the parsed list
     */
    <T> ListSectionRetriever<T> list(SubSectionArgumentParser<List<T>> parser);

    /**
     * Parses the section containing named subsections into a map of section names and their values.
     *
     * @param parser the parser to use
     * @param <T>    the type of the parsed values
     * @return a new {@link DecoratableSectionRetriever} for the parsed map
     */
    <T> DecoratableSectionRetriever<Map<String, T>> namedSectionList(SubSectionArgumentParser<T> parser);
}
