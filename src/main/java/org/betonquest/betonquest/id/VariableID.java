package org.betonquest.betonquest.id;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings("PMD.CommentRequired")
public class VariableID extends ID {
    public VariableID(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier.replaceAll("%", ""));
        if (!super.identifier.isEmpty() && identifier.charAt(0) != '%' && !identifier.endsWith("%")) {
            throw new ObjectNotFoundException("Variable instruction has to start and end with '%' characters");
        }
        super.rawInstruction = identifier;
        super.identifier = "%" + super.identifier + "%";
    }

    @Override
    public Instruction generateInstruction() {
        return new VariableInstruction(super.pack, this, super.identifier);
    }

    @Override
    public String getBaseID() {
        return rawInstruction;
    }

    @Override
    public String getFullID() {
        return pack.getQuestPath() + "-" + getBaseID();
    }

}
