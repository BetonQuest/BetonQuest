package org.betonquest.betonquest.compatibility.citizens;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Location;

import java.util.Locale;

/**
 * Provides information about a citizen npc.
 * <p>
 * Format:
 * {@code %citizen.<id>.<type>%}
 * <p>
 * Types:
 * * name - Return citizen name
 * * full_name - Full Citizen name
 * * location  - Return citizen location. x;y;z;world;yaw;pitch
 */
@SuppressWarnings("PMD.CommentRequired")
public class CitizensVariable extends Variable {

    private final int npcId;
    private final TYPE key;

    public CitizensVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        npcId = instruction.getInt();
        try {
            key = TYPE.valueOf(instruction.next().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Invalid Type: " + instruction.current(), e);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public String getValue(final String playerID) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            return "";
        }

        switch (key) {
            case NAME:
                return npc.getName();
            case FULL_NAME:
                return npc.getFullName();
            case LOCATION:
                if (npc.getEntity() != null) {
                    final Location loc = npc.getEntity().getLocation();
                    return String.format("%.2f;%.2f;%.2f;%s;%.2f;%.2f",
                            loc.getX(),
                            loc.getY(),
                            loc.getZ(),
                            loc.getWorld().getName(),
                            loc.getYaw(),
                            loc.getPitch());
                }
                break;
        }
        return "";
    }

    private enum TYPE {
        NAME,
        FULL_NAME,
        LOCATION
    }

}
