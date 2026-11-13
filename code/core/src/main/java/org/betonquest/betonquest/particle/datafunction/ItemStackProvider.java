package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An item stack provider usable as an argument.
 */
public class ItemStackProvider implements ParticleDataFunctionProvider {

    /**
     * The item argument.
     */
    @Nullable
    private Argument<ItemWrapper> item;

    /**
     * Creates a new item stack provider.
     */
    public ItemStackProvider() {
    }

    @Override
    public ParticleDataFunction<ItemStack> getValue(@Nullable final Profile profile) throws QuestException {
        if (item == null) {
            throw new QuestException("ItemStack particle requires 'item' argument");
        }
        final ItemWrapper itemWrapper = item.getValue(profile);
        final ItemStack stack = itemWrapper.generate(profile);
        return (delta1, delta2) -> stack;
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(ItemStack.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.item = instruction.read().value("item").item().get();
    }
}
