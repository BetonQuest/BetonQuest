package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Spawns mobs at given location
 */
@SuppressWarnings("PMD.CommentRequired")
public class SpawnMobEvent extends QuestEvent {
    private final CompoundLocation loc;

    private final EntityType type;

    private final VariableNumber amount;

    @Nullable
    private final QuestItem helmet;

    @Nullable
    private final QuestItem chestplate;

    @Nullable
    private final QuestItem leggings;

    @Nullable
    private final QuestItem boots;

    @Nullable
    private final QuestItem mainHand;

    @Nullable
    private final QuestItem offHand;

    private final Item[] drops;

    @Nullable
    private final String name;

    @Nullable
    private final VariableString marked;

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public SpawnMobEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        final String entity = instruction.next();
        try {
            type = EntityType.valueOf(entity.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new InstructionParseException("Entity type '" + entity + "' does not exist", e);
        }
        amount = instruction.getVarNum();
        final String nameString = instruction.getOptional("name");
        name = nameString == null ? null : Utils.format(nameString, true, false).replace('_', ' ');
        final String markedString = instruction.getOptional("marked");
        marked = markedString == null ? null : new VariableString(
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
        ItemID item;
        item = instruction.getItem(instruction.getOptional("h"));
        helmet = item == null ? null : new QuestItem(item);
        item = instruction.getItem(instruction.getOptional("c"));
        chestplate = item == null ? null : new QuestItem(item);
        item = instruction.getItem(instruction.getOptional("l"));
        leggings = item == null ? null : new QuestItem(item);
        item = instruction.getItem(instruction.getOptional("b"));
        boots = item == null ? null : new QuestItem(item);
        item = instruction.getItem(instruction.getOptional("m"));
        mainHand = item == null ? null : new QuestItem(item);
        item = instruction.getItem(instruction.getOptional("o"));
        offHand = item == null ? null : new QuestItem(item);
        drops = instruction.getItemList(instruction.getOptional("drops"));
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final Location location = loc.getLocation(profile);
        final int pAmount = amount.getInt(profile);
        for (int i = 0; i < pAmount; i++) {
            final Entity entity = location.getWorld().spawnEntity(location, type);
            if (entity instanceof final LivingEntity living) {
                final EntityEquipment equipment = living.getEquipment();
                if (equipment != null) {
                    equipment.setHelmet(helmet == null ? null : helmet.generate(1));
                    equipment.setHelmetDropChance(0);
                    equipment.setChestplate(chestplate == null ? null : chestplate.generate(1));
                    equipment.setChestplateDropChance(0);
                    equipment.setLeggings(leggings == null ? null : leggings.generate(1));
                    equipment.setLeggingsDropChance(0);
                    equipment.setBoots(boots == null ? null : boots.generate(1));
                    equipment.setBootsDropChance(0);
                    equipment.setItemInMainHand(mainHand == null ? null : mainHand.generate(1));
                    equipment.setItemInMainHandDropChance(0);
                    equipment.setItemInOffHand(offHand == null ? null : offHand.generate(1));
                    equipment.setItemInOffHandDropChance(0);
                }
            }
            int dropIndex = 0;
            for (final Item item : drops) {
                entity.setMetadata("betonquest-drops-" + dropIndex,
                        new FixedMetadataValue(BetonQuest.getInstance(), item.getID().getFullID() + ":"
                                + item.getAmount().getInt(profile)));
                dropIndex++;
            }
            if (name != null && entity instanceof final LivingEntity livingEntity) {
                livingEntity.setCustomName(name);
            }
            if (marked != null) {
                entity.setMetadata("betonquest-marked", new FixedMetadataValue(BetonQuest.getInstance(), marked.getString(profile)));
            }
        }
        return null;
    }
}
