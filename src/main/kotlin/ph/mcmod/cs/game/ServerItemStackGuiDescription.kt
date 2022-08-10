package ph.mcmod.cs.game

import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import ph.mcmod.cs.api.*
import ph.mcmod.kum.always
import ph.mcmod.kum.asStorage
import ph.mcmod.kum.cursorStorage
import kotlin.math.min

@Suppress("LeakingThis")
open class ServerItemStackGuiDescription(syncId: Int, playerInventory: PlayerInventory, val storage: QueueItemStorage) : ClientItemStackGuiDescription(syncId, playerInventory), Runnable {
    
    init {
        run()
        storage.runnable = this
    }
    
    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) {
        if (slotIndex in 0..1 && actionType == SlotActionType.PICKUP) {
            StorageUtil.move(cursorStorage, storage, always(), Long.MAX_VALUE, null)
            if (slotIndex == 0) {
                val temp = getSlot(0).stack
                getSlot(0).stack = cursorStack
                cursorStack = temp
            } else {
                StorageUtil.move(storage, cursorStorage, always(), storage.peek.amount, null)
            }
        } else
            super.onSlotClick(slotIndex, button, actionType, player)
    }
    
    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        return when (index) {
            1 -> {
                StorageUtil.move(storage, player.inventory.asStorage(), always(), getSlot(1).stack.count.toLong(), null)
                ItemStack.EMPTY
            }
            in 2..37 -> {
                StorageUtil.move(getSlot(index).asStorage(), storage, always(), Long.MAX_VALUE, null)
                ItemStack.EMPTY
            }
            else -> {
                super.transferSlot(player, index)
            }
        }
    }
    
    override fun run() {
        getSlot(1).stack = storage.peek.run { resource.toStack(min(resource.item.maxCount, amount.toInt())) }
    }
}