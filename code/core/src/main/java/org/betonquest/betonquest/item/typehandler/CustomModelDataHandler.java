package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.betonquest.betonquest.lib.instruction.argument.DefaultFlagArgument;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of CustomModelData.
 */
public class CustomModelDataHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * If 'item_model' is forbidden.
     */
    private FlagArgument<Boolean> noModelData = new DefaultFlagArgument<>(false, FlagState.UNDEFINED);

    /**
     * The CustomModelData with existence.
     */
    private ExistenceArgument<Integer> modelData = ExistenceArgument.whateverNullValue();

    /**
     * The empty default Constructor.
     */
    public CustomModelDataHandler() {
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
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
    public void parse(final Instruction instruction) throws QuestException {
        this.modelData = ExistenceArgument.apply("custom-model-data", instruction.parse(resolvedString -> {
            try {
                return Integer.parseInt(resolvedString);
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse custom model data value: " + resolvedString, e);
            }
        }));
        this.noModelData = instruction.bool().getFlag("no-custom-model-data", true);
    }

    @Override
    public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
        final Pair<Existence, Integer> modelData = this.modelData.getValue(profile);
        final boolean noModelData = this.noModelData.getValue(profile).orElse(false);
        return new ResolvedAttribute.ResolvedItemMeta() {
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
        };
    }
}
