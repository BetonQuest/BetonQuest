package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
public class CustomModelDataHandler implements ItemMetaHandler<ItemMeta> {
    private QuestItem.Existence existence = QuestItem.Existence.WHATEVER;

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

    public void parse(final String data) throws InstructionParseException {
        try {
            require(Integer.parseInt(data));
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse custom model data value: " + data, e);
        }
    }

    public void require(final int customModelDataId) {
        this.existence = QuestItem.Existence.REQUIRED;
        this.modelData = customModelDataId;
    }

    public void forbid() {
        this.existence = QuestItem.Existence.FORBIDDEN;
    }

    public QuestItem.Existence getExistence() {
        return existence;
    }

    public boolean has() {
        return existence == QuestItem.Existence.REQUIRED;
    }

    public int get() {
        return modelData;
    }

    @Override
    public void set(final String key, final String data) throws InstructionParseException {
        switch (key) {
            case "custom-model-data" -> parse(data);
            case "no-custom-model-data" -> forbid();
            default -> throw new InstructionParseException("Unknown custom model data key: " + key);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        if (getExistence() == QuestItem.Existence.REQUIRED) {
            meta.setCustomModelData(get());
        }
    }

    @Override
    public boolean check(final ItemMeta data) {
        return existence == QuestItem.Existence.WHATEVER
                || existence == QuestItem.Existence.FORBIDDEN && !data.hasCustomModelData()
                || existence == QuestItem.Existence.REQUIRED && data.hasCustomModelData() && modelData == data.getCustomModelData();
    }

    @Override
    public String toString() {
        return existence == QuestItem.Existence.REQUIRED ? "custom-model-data:" + modelData : "";
    }
}
