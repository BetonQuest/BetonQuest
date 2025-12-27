package org.betonquest.betonquest.quest.placeholder.tag;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.IdentifierParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.config.PluginMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * An abstract class for creating Tag placeholders.
 *
 * @param <T> the data holder type
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractTagPlaceholder<T> {

    /**
     * The data holder.
     */
    protected final T data;

    /**
     * The tag to check for.
     */
    protected final String tagName;

    /**
     * The quest package to check for the tag.
     */
    protected final QuestPackage questPackage;

    /**
     * Whether to return true/false or the configured messages.
     */
    protected final FlagArgument<Boolean> papiMode;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Constructs a new GlobalTagPlaceholder.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param data          the data holder
     * @param tagName       the tag to check for
     * @param questPackage  the quest package to check for the tag
     * @param papiMode      whether to return true/false or the configured messages
     */
    public AbstractTagPlaceholder(final PluginMessage pluginMessage, final T data, final String tagName,
                               final QuestPackage questPackage, final FlagArgument<Boolean> papiMode) {
        this.pluginMessage = pluginMessage;
        this.data = data;
        this.tagName = tagName;
        this.questPackage = questPackage;
        this.papiMode = papiMode;
    }

    /**
     * Returns the value of the placeholder.
     *
     * @param profile the profile to check
     * @param tags    the tags to check
     * @return the value of the placeholder
     * @throws QuestException if the papiMode is enabled and the message could not be resolved
     */
    public String getValueFor(@Nullable final Profile profile, final Set<String> tags) throws QuestException {
        final boolean papiMode = this.papiMode.getValue(profile).orElse(false);
        if (tags.contains(IdentifierParser.INSTANCE.apply(questPackage, tagName))) {
            return papiMode ? LegacyComponentSerializer.legacySection().serialize(pluginMessage.getMessage(profile, "condition_placeholder_met")) : "true";
        }
        return papiMode ? LegacyComponentSerializer.legacySection().serialize(pluginMessage.getMessage(profile, "condition_placeholder_not_met")) : "false";
    }
}
