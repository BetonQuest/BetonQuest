package org.betonquest.betonquest.compatibility.nexo.item

import com.nexomc.nexo.api.NexoItems
import com.nexomc.nexo.items.ItemBuilder
import net.kyori.adventure.text.Component
import org.betonquest.betonquest.api.instruction.variable.Variable
import org.betonquest.betonquest.api.profile.Profile
import org.betonquest.betonquest.api.quest.QuestException
import org.betonquest.betonquest.item.QuestItem
import org.betonquest.betonquest.item.QuestItemWrapper
import org.bukkit.inventory.ItemStack

data class NexoItemWrapper(
    private val itemName: Variable<String>
) : QuestItemWrapper {

    @Throws(QuestException::class)
    override fun getItem(profile: Profile?): QuestItem {
        return NexoItem(itemName.getValue(profile))
    }

    class NexoItem(
        private val itemId: String
    ) : QuestItem {

        private val itemBuilder: ItemBuilder? = NexoItems.itemFromId(itemId)

        override fun getName(): Component? =
            itemBuilder?.itemName

        override fun getLore(): List<Component>? =
            itemBuilder?.lore

        override fun generate(stackSize: Int, profile: Profile?): ItemStack? =
            itemBuilder?.setAmount(stackSize)?.build()?.clone()

        override fun matches(item: ItemStack?): Boolean =
            itemId == NexoItems.idFromItem(item)
    }
}