package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;
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
    public void set(final String key, final String data) throws InstructionParseException {
        if (!"color".equals(key)) {
            throw new InstructionParseException("Invalid color key: " + key);
        }
        set(data);
    }

    @Override
    public void populate(final LeatherArmorMeta armorMeta) {
        armorMeta.setColor(get());
    }

    @Override
    public boolean check(final LeatherArmorMeta armorMeta) {
        return check(armorMeta.getColor());
    }

    public void set(final String string) throws InstructionParseException {
        if (Existence.NONE_KEY.equalsIgnoreCase(string)) {
            colorE = Existence.FORBIDDEN;
            return;
        }
        color = Utils.getColor(string);
        colorE = Existence.REQUIRED;
    }

    public Color get() {
        return color;
    }

    @SuppressWarnings("PMD.TooFewBranchesForSwitch")
    public boolean check(final Color color) {
        return switch (colorE) {
            case WHATEVER -> true;
            case REQUIRED, FORBIDDEN -> // if it's forbidden, this.color is default leather color (undyed)
                    color.equals(this.color);
        };
    }
}
