package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
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
    private ExistenceArgument<Integer> modelData = ExistenceArgument.whateverValue(0);

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
    public void set(final Instruction instruction) throws QuestException {
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
    public void populate(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
        final Pair<Existence, Integer> pair = modelData.getValue(profile);
        final Integer cmd = pair.getRight();
        if (cmd != null) {
            meta.setCustomModelData(cmd);
        }
    }

    @Override
    public boolean check(final ItemMeta data, @Nullable final Profile profile) throws QuestException {
        if (noModelData.getValue(profile).orElse(false) && data.hasCustomModelData()) {
            return false;
        }
        final Pair<Existence, Integer> pair = modelData.getValue(profile);
        final Existence existence = pair.getLeft();
        final Integer cmd = pair.getRight();
        return existence == Existence.WHATEVER
                || existence == Existence.FORBIDDEN && !data.hasCustomModelData()
                || existence == Existence.REQUIRED && data.hasCustomModelData() && cmd == data.getCustomModelData();
    }
}
