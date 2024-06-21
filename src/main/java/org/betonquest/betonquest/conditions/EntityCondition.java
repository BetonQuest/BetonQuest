package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;

/**
 * Checks if there are specified entities in the area
 */
@SuppressWarnings("PMD.CommentRequired")
public class EntityCondition extends Condition {
    private final EntityType[] types;

    private final VariableNumber[] amounts;

    private final CompoundLocation loc;

    private final VariableNumber range;

    @Nullable
    private final String name;

    @Nullable
    private final VariableString marked;

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition"})
    public EntityCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        final String[] rawTypes = instruction.getArray();
        types = new EntityType[rawTypes.length];
        amounts = new VariableNumber[rawTypes.length];
        for (int i = 0; i < rawTypes.length; i++) {
            try {
                if (rawTypes[i].contains(":")) {
                    final String[] typeParts = rawTypes[i].split(":");
                    if (typeParts.length == 0) {
                        throw new InstructionParseException("Type not defined");
                    } else if (typeParts.length < 2) {
                        types[i] = EntityType.valueOf(typeParts[0].toUpperCase(Locale.ROOT));
                        amounts[i] = new VariableNumber(instruction.getPackage(), "1");
                    } else {
                        types[i] = EntityType.valueOf(typeParts[0].toUpperCase(Locale.ROOT));
                        amounts[i] = getAmount(typeParts[1]);
                    }
                } else {
                    types[i] = EntityType.valueOf(rawTypes[i].toUpperCase(Locale.ROOT));
                    amounts[i] = new VariableNumber(instruction.getPackage(), "1");
                }
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Unknown entity type: " + rawTypes[i], e);
            }
        }
        loc = instruction.getLocation();
        range = instruction.getVarNum();
        name = instruction.getOptional("name");
        final String markedString = instruction.getOptional("marked");
        marked = markedString == null ? null : new VariableString(
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
    }

    private VariableNumber getAmount(final String typePart) throws InstructionParseException {
        try {
            return new VariableNumber(instruction.getPackage(), typePart);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Could not parse amount", e);
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Location location = loc.getLocation(profile);
        final int[] neededAmounts = new int[types.length];
        for (int i = 0; i < neededAmounts.length; i++) {
            neededAmounts[i] = 0;
        }
        final Collection<Entity> entities = location.getWorld().getEntities();
        for (final Entity entity : entities) {
            if (name != null && (entity.getCustomName() == null || !entity.getCustomName().equals(name))) {
                continue;
            }
            if (marked != null) {
                final String value = marked.getString(profile);
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                final String dataContainerValue = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (dataContainerValue == null || !dataContainerValue.equals(value)) {
                    continue;
                }
            }
            final double pRange = range.getDouble(profile);
            if (entity.getLocation().distanceSquared(location) < pRange * pRange) {
                final EntityType theType = entity.getType();
                for (int i = 0; i < types.length; i++) {
                    if (theType == types[i]) {
                        neededAmounts[i]++;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < amounts.length; i++) {
            if (neededAmounts[i] < amounts[i].getInt(profile)) {
                return false;
            }
        }
        return true;
    }
}
