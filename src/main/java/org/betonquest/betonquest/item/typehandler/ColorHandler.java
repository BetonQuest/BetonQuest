package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
public class ColorHandler implements ItemMetaHandler<LeatherArmorMeta> {
    private Color color = Bukkit.getServer().getItemFactory().getDefaultLeatherColor();

    private Existence colorE = Existence.WHATEVER;

    public ColorHandler() {
    }

    @Override
    public Class<LeatherArmorMeta> metaClass() {
        return LeatherArmorMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("color");
    }

    @Override
    @Nullable
    public String serializeToString(final LeatherArmorMeta armorMeta) {
        if (armorMeta.getColor().equals(Bukkit.getServer().getItemFactory().getDefaultLeatherColor())) {
            return null;
        }
        final DyeColor dyeColor = DyeColor.getByColor(armorMeta.getColor());
        return "color:" + (dyeColor == null ? '#' + Integer.toHexString(armorMeta.getColor().asRGB()) : dyeColor.toString());
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        if (!"color".equals(key)) {
            throw new QuestException("Invalid color key: " + key);
        }
        if (Existence.NONE_KEY.equalsIgnoreCase(data)) {
            colorE = Existence.FORBIDDEN;
            return;
        }
        color = Utils.getColor(data);
        colorE = Existence.REQUIRED;
    }

    @Override
    public void populate(final LeatherArmorMeta armorMeta) {
        armorMeta.setColor(color);
    }

    @Override
    public boolean check(final LeatherArmorMeta armorMeta) {
        return colorE == Existence.WHATEVER || armorMeta.getColor().equals(this.color);
        // if it's forbidden, this.color is default leather color (undyed)
    }
}
