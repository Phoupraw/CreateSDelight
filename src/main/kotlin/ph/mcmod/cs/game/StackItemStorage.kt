package ph.mcmod.cs.game

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.minecraft.block.entity.BlockEntity
import ph.mcmod.cs.game.QueueItemStorage
import java.util.*

open class StackItemStorage(blockEntity: BlockEntity) : QueueItemStorage(blockEntity) {
    val stack: Deque<SingleSlotStorage<ItemVariant>> = ArrayDeque()
    override val queue: Queue<SingleSlotStorage<ItemVariant>> = stack
    override fun addSlot(slot: Slot) {
        stack.push(slot)
    }
}