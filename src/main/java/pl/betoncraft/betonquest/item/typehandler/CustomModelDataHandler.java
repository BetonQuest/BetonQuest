package pl.betoncraft.betonquest.item.typehandler;

import org.bukkit.inventory.meta.ItemMeta;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;

public class CustomModelDataHandler {

    private QuestItem.Existence existence = QuestItem.Existence.WHATEVER;
    private int modelData = 0;

    public CustomModelDataHandler() {
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
