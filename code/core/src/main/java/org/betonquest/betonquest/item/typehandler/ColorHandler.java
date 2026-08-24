package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of Leather Armor Color.
 */
public class ColorHandler implements ItemMetaHandler<LeatherArmorMeta> {

    /**
     * The server leather color, default as "empty".
     */
    private final Color defaultColor = Bukkit.getServer().getItemFactory().getDefaultLeatherColor();

    /**
     * The empty default Constructor.
     */
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
    @Nullable
    public Attribute<LeatherArmorMeta> parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<Color> color = ExistenceArgument.applyOrNull("color", instruction.parse(HandlerUtil::getColor));
        if (color == null) {
            return null;
        }
        return new NonResolved(this.defaultColor, color);
    }

    /**
     * The attribute with placeholders.
     *
     * @param color the leather color
     */
    private record NonResolved(Color defaultColor, ExistenceArgument<Color> color)
            implements Attribute<LeatherArmorMeta> {

        @Override
        public ResolvedAttribute<LeatherArmorMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, Color> color = this.color.getValue(profile);
            return new Resolved(defaultColor, color);
        }
    }

    /**
     * The resolved attribute.
     */
    private record Resolved(Color defaultColor, Pair<Existence, Color> color)
            implements ResolvedAttribute<LeatherArmorMeta> {

        @Override
        public Class<LeatherArmorMeta> metaClass() {
            return LeatherArmorMeta.class;
        }

        @Override
        public void populate(final LeatherArmorMeta armorMeta) {
            armorMeta.setColor(color.getRight());
        }

        @Override
        public boolean check(final LeatherArmorMeta armorMeta) {
            return color.getLeft() == Existence.WHATEVER || armorMeta.getColor().equals(color.getRight());
            // if it's forbidden, this.color is default leather color (undyed)
        }
    }
}
