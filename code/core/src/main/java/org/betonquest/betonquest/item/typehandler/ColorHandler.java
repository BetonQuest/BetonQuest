package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
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
     * The leather color, defaults to server default as "empty".
     */
    private ExistenceArgument<Color> color = ExistenceArgument.whateverValue(
            Bukkit.getServer().getItemFactory().getDefaultLeatherColor());

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
    public void parse(final Instruction instruction) throws QuestException {
        this.color = ExistenceArgument.apply("color", instruction.parse(HandlerUtil::getColor));
    }

    @Override
    public ResolvedAttribute<LeatherArmorMeta> resolve(@Nullable final Profile profile) throws QuestException {
        final Pair<Existence, Color> color = this.color.getValue(profile);
        return new ResolvedAttribute<>() {

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
        };
    }
}
