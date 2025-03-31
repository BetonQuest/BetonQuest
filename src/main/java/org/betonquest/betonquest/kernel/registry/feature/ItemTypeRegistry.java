package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Registry for {@link QuestItem} types.
 * <p>
 * The {@link #PROVIDER_PATTERN} indicates custom registry while abstinence falls back to the provided
 * {@link QuestItem Standard Quest Item}.
 */
public class ItemTypeRegistry extends FactoryRegistry<TypeFactory<QuestItem>> {
    /**
     * The pattern that indicates custom quest item parsing.
     */
    private static final Pattern PROVIDER_PATTERN = Pattern.compile("^(@\\[(?<provider>.+)])$");

    /**
     * The factory to create Items without custom parsing.
     */
    @Nullable
    private TypeFactory<QuestItem> defaultItemFactory;

    /**
     * Create a new Item registry.
     *
     * @param log the logger that will be used for logging
     */
    public ItemTypeRegistry(final BetonQuestLogger log) {
        super(log, "items");
    }

    /**
     * Sets the factory to create Items without custom parsing/prefix.
     *
     * @param defaultItemFactory the factory to set as default
     */
    public void setDefaultItemFactory(final TypeFactory<QuestItem> defaultItemFactory) {
        this.defaultItemFactory = defaultItemFactory;
    }

    @Override
    @Nullable
    public TypeFactory<QuestItem> getFactory(final String name) {
        final Matcher matcher = PROVIDER_PATTERN.matcher(name);
        if (matcher.matches()) {
            return super.getFactory(matcher.group("provider"));
        }
        return defaultItemFactory;
    }
}
