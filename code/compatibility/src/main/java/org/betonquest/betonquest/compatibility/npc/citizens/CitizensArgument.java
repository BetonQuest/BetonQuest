package org.betonquest.betonquest.compatibility.npc.citizens;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;

/**
 * Parses a string to a Citizens Npc ID.
 */
public class CitizensArgument implements InstructionArgumentParser<NpcIdentifier> {

    /**
     * The identifier factory to use.
     */
    private final IdentifierFactory<NpcIdentifier> identifierFactory;

    /**
     * The instruction api to use.
     */
    private final Instructions instructionApi;

    /**
     * Creates a new parser for Citizens Npc Ids.
     *
     * @param instructionApi    the instruction api to use
     * @param identifierFactory the identifier factory to use
     */
    public CitizensArgument(final Instructions instructionApi, final IdentifierFactory<NpcIdentifier> identifierFactory) {
        this.instructionApi = instructionApi;
        this.identifierFactory = identifierFactory;
    }

    @Override
    public NpcIdentifier apply(final PlaceholderManager placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final NpcIdentifier npcId = identifierFactory.parseIdentifier(pack, string);
        final Instruction npcInstruction = instructionApi.create(npcId, npcId.readRawInstruction());
        if (!"citizens".equals(npcInstruction.getPart(0))) {
            throw new QuestException("Cannot use non-Citizens NPC ID");
        }
        return npcId;
    }
}
