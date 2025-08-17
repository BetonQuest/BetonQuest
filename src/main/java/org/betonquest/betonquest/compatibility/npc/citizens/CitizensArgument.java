package org.betonquest.betonquest.compatibility.npc.citizens;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.NpcID;

/**
 * Parses a string to a Citizens Npc ID.
 */
public class CitizensArgument implements PackageArgument<NpcID> {
    /**
     * The default instance of {@link CitizensArgument}.
     */
    public static final CitizensArgument CITIZENS_ID = new CitizensArgument();

    /**
     * Creates a new parser for Citizens Npc Ids.
     */
    public CitizensArgument() {
    }

    @Override
    public NpcID apply(final QuestPackageManager questPackageManager, final QuestPackage pack, final String string) throws QuestException {
        final NpcID npcId = new NpcID(questPackageManager, pack, string);
        final Instruction npcInstruction = npcId.getInstruction();
        if (!"citizens".equals(npcInstruction.getPart(0))) {
            throw new QuestException("Cannot use non-Citizens NPC ID!");
        }
        return npcId;
    }
}
