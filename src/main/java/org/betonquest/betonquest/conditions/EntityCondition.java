package org.betonquest.betonquest.conditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;

import java.util.Collection;
import java.util.List;
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
    private final String name;
    private String marked;

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
                        amounts[i] = new VariableNumber(1);
                    } else {
                        types[i] = EntityType.valueOf(typeParts[0].toUpperCase(Locale.ROOT));
                        amounts[i] = getAmount(typeParts[1]);
                    }
                } else {
                    types[i] = EntityType.valueOf(rawTypes[i].toUpperCase(Locale.ROOT));
                    amounts[i] = new VariableNumber(1);
                }
            } catch (IllegalArgumentException e) {
                throw new InstructionParseException("Unknown entity type: " + rawTypes[i], e);
            }
        }
        loc = instruction.getLocation();
        range = instruction.getVarNum();
        name = instruction.getOptional("name");
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
    }

    private VariableNumber getAmount(final String typePart) throws InstructionParseException {
        try {
            return new VariableNumber(instruction.getPackage().getName(), typePart);
        } catch (InstructionParseException e) {
            throw new InstructionParseException("Could not parse amount", e);
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        final int[] neededAmounts = new int[types.length];
        for (int i = 0; i < neededAmounts.length; i++) {
            neededAmounts[i] = 0;
        }
        final Collection<Entity> entities = location.getWorld().getEntities();
        loop:
        for (final Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            if (name != null && (entity.getCustomName() == null || !entity.getCustomName().equals(name))) {
                continue;
            }
            if (marked != null) {
                if (!entity.hasMetadata("betonquest-marked")) {
                    continue;
                }
                final List<MetadataValue> meta = entity.getMetadata("betonquest-marked");
                for (final MetadataValue m : meta) {
                    if (!m.asString().equals(marked)) {
                        continue loop;
                    }
                }
            }
            final double pRange = range.getDouble(playerID);
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
            if (neededAmounts[i] < amounts[i].getInt(playerID)) {
                return false;
            }
        }
        return true;
    }

}
