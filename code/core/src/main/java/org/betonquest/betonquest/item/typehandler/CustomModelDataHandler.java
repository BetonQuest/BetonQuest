package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of CustomModelData.
 */
public class CustomModelDataHandler implements ItemMetaHandler.Standard {

    /**
     * The empty default Constructor.
     */
    public CustomModelDataHandler() {
    }

    @Override
    public Set<String> keys() {
        return Set.of("custom-model-data", "no-custom-model-data");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.hasCustomModelData()) {
            return "custom-model-data:" + meta.getCustomModelData();
        }
        return null;
    }

    @Override
    public Attribute.Standard parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<Integer> modelData = ExistenceArgument.apply("custom-model-data", instruction.parse(resolvedString -> {
            try {
                return Integer.parseInt(resolvedString);
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse custom model data value: " + resolvedString, e);
            }
        }));
        final FlagArgument<Boolean> noModelData = instruction.bool().getFlag("no-custom-model-data", true);
        // TODO null check
        return new NonResolved(modelData, noModelData);
    }

    /**
     * The attribute with placeholders.
     *
     * @param modelData   The CustomModelData with existence.
     * @param noModelData If 'custom model data' is forbidden.
     */
    private record NonResolved(ExistenceArgument<Integer> modelData, FlagArgument<Boolean> noModelData)
            implements Attribute.Standard {

        @Override
        public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, Integer> modelData = this.modelData.getValue(profile);
            final boolean noModelData = this.noModelData.getValue(profile).orElse(false);
            return new Resolved(modelData, noModelData);
        }
    }

    /**
     * The resolved attribute.
     */
    private record Resolved(Pair<Existence, Integer> modelData, boolean noModelData)
            implements ResolvedAttribute.Standard {

        @Override
        public void populate(final ItemMeta meta) {
            final Integer cmd = modelData.getRight();
            if (cmd != null) {
                meta.setCustomModelData(cmd);
            }
        }

        @Override
        public boolean check(final ItemMeta meta) {
            if (noModelData && meta.hasCustomModelData()) {
                return false;
            }
            final Existence existence = modelData.getLeft();
            return existence == Existence.WHATEVER
                    || existence == Existence.FORBIDDEN && !meta.hasCustomModelData()
                    || existence == Existence.REQUIRED && meta.hasCustomModelData() && modelData.getRight() == meta.getCustomModelData();
        }
    }
}
