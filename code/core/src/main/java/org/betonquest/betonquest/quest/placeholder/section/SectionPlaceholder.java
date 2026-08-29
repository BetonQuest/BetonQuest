package org.betonquest.betonquest.quest.placeholder.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.PackageIdentifierParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder that transforms a section into a comma-separated list of identifiers of elements in the section.
 */
public class SectionPlaceholder implements NullablePlaceholder {

    /**
     * The quest package to be relative to.
     */
    private final QuestPackage questPackage;

    /**
     * The parent section to select from, e.g., actions, conditions, etc.
     */
    private final Argument<String> section;

    /**
     * The identifier in the section to select subsection from.
     */
    private final Argument<String> identifier;

    /**
     * How the elements in the section should be selected.
     */
    private final Argument<SectionSelectionMode> selectionMode;

    /**
     * The limit for how many elements in the section should be selected at max.
     */
    private final Argument<Number> limit;

    /**
     * Whether the elements should be shuffled after selection.
     */
    private final FlagArgument<Boolean> shuffle;

    /**
     * Whether the number of elements in the section should be counted instead of listed.
     */
    private final FlagArgument<Boolean> count;

    /**
     * Create a new section placeholder.
     *
     * @param questPackage  the quest package to be relative to
     * @param section       the parent section to select from, e.g., actions, conditions, etc
     * @param identifier    the identifier in the section to select subsections from
     * @param selectionMode how the elements in the section should be selected
     * @param limit         the limit for how many elements in the section should be selected at max
     * @param shuffle       whether the elements should be shuffled after selection
     * @param count         whether the number of elements in the section should be counted instead of listed
     */
    public SectionPlaceholder(final QuestPackage questPackage, final Argument<String> section, final Argument<String> identifier,
                              final Argument<SectionSelectionMode> selectionMode, final Argument<Number> limit,
                              final FlagArgument<Boolean> shuffle, final FlagArgument<Boolean> count) {
        this.questPackage = questPackage;
        this.section = section;
        this.identifier = identifier;
        this.selectionMode = selectionMode;
        this.limit = limit;
        this.shuffle = shuffle;
        this.count = count;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final String section = this.section.getValue(profile);
        final String identifier = this.identifier.getValue(profile);
        final SectionSelectionMode mode = selectionMode.getValue(profile);
        final int limit = this.limit.getValue(profile).intValue();
        final boolean shuffle = this.shuffle.getValue(profile).orElse(false);

        final char pathSeparator = questPackage.getConfig().options().pathSeparator();
        final String path = section + pathSeparator + identifier;
        final ConfigurationSection configSection = questPackage.getConfig().getConfigurationSection(path);
        if (configSection == null) {
            throw new QuestException("Section '%s' does not exist in package '%s'!".formatted(path, questPackage));
        }
        final List<String> keys = new ArrayList<>(configSection.getKeys(true));
        keys.removeIf(configSection::isConfigurationSection);

        if (count.getValue(profile).orElse(false)) {
            return String.valueOf(Math.min(keys.size(), limit));
        }

        if (mode == SectionSelectionMode.LAST) {
            Collections.reverse(keys);
        } else if (mode == SectionSelectionMode.RANDOM) {
            Collections.shuffle(keys);
        }

        List<String> result = keys.stream().limit(limit)
                .map(key -> identifier + pathSeparator + key)
                .map(key -> PackageIdentifierParser.INSTANCE.apply(questPackage, key))
                .toList();
        if (shuffle) {
            result = new ArrayList<>(result);
            Collections.shuffle(result);
        }
        return String.join(",", result);
    }
}
