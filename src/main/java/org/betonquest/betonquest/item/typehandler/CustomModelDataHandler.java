package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
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

    public void parse(final String data) throws InstructionParseException {
        try {
            require(Integer.parseInt(data));
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse custom model data value: " + data, e);
        }
    }

    public void require(final int customModelDataId) {
        this.existence = Existence.REQUIRED;
        this.modelData = customModelDataId;
    }

    public void forbid() {
        this.existence = Existence.FORBIDDEN;
    }

    public Existence getExistence() {
        return existence;
    }

    public boolean has() {
        return existence == Existence.REQUIRED;
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
        if (getExistence() == Existence.REQUIRED) {
            meta.setCustomModelData(get());
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
