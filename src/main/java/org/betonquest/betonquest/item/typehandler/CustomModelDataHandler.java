package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
public class CustomModelDataHandler implements ItemMetaHandler<ItemMeta> {
    private Existence existence = Existence.WHATEVER;

    private int modelData;

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
    public void set(final String key, final String data) throws QuestException {
        switch (key) {
            case "custom-model-data" -> {
                try {
                    this.existence = Existence.REQUIRED;
                    this.modelData = Integer.parseInt(data);
                } catch (final NumberFormatException e) {
                    throw new QuestException("Could not parse custom model data value: " + data, e);
                }
            }
            case "no-custom-model-data" -> this.existence = Existence.FORBIDDEN;
            default -> throw new QuestException("Unknown custom model data key: " + key);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        if (existence == Existence.REQUIRED) {
            meta.setCustomModelData(modelData);
        }
    }

    @Override
    public boolean check(final ItemMeta data) {
        return existence == Existence.WHATEVER
                || existence == Existence.FORBIDDEN && !data.hasCustomModelData()
                || existence == Existence.REQUIRED && data.hasCustomModelData() && modelData == data.getCustomModelData();
    }

    @Override
    public String toString() {
        return existence == Existence.REQUIRED ? "custom-model-data:" + modelData : "";
    }
}
