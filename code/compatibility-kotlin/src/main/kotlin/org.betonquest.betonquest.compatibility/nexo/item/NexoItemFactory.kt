package org.betonquest.betonquest.compatibility.nexo.item

import org.betonquest.betonquest.api.instruction.Instruction
import org.betonquest.betonquest.api.instruction.argument.Argument
import org.betonquest.betonquest.api.kernel.TypeFactory
import org.betonquest.betonquest.api.quest.QuestException
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper
import org.betonquest.betonquest.item.QuestItemWrapper

object NexoItemFactory : TypeFactory<QuestItemWrapper> {

    @Throws(QuestException::class)
    override fun parseInstruction(instruction: Instruction): QuestItemWrapper {
        val wrapper = NexoItemWrapper(instruction[Argument.STRING])
        return if (instruction.hasArgument("quest-item")) {
            QuestItemTagAdapterWrapper(wrapper)
        } else {
            wrapper
        }
    }
}
