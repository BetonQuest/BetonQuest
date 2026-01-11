package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.bukkit.configuration.ConfigurationSection;

/**
 * The api to create instructions.
 */
public interface InstructionApi {

    /**
     * Creates an instruction from an identifier and an instruction string.
     *
     * @param identifier  the identifier of the instruction.
     * @param instruction the instruction string.
     * @return a new instruction for the given identifier and instruction string.
     * @throws QuestException if the instruction cannot be created.
     */
    Instruction createInstruction(Identifier identifier, String instruction) throws QuestException;

    /**
     * Creates an instruction from a quest package and an instruction string.
     *
     * @param questPackage the quest package the instruction belongs to.
     * @param instruction  the instruction string.
     * @return a new instruction for the given quest package and instruction string.
     * @throws QuestException if the instruction cannot be created.
     */
    Instruction createInstruction(QuestPackage questPackage, String instruction) throws QuestException;

    /**
     * Creates a section instruction for the given quest package and section.
     *
     * @param questPackage the quest package the section belongs to.
     * @param section      the section to read from
     * @return a new section instruction for the given quest package and section.
     * @throws QuestException if the section instruction cannot be created.
     */
    SectionInstruction createSectionInstruction(QuestPackage questPackage, ConfigurationSection section) throws QuestException;
}
