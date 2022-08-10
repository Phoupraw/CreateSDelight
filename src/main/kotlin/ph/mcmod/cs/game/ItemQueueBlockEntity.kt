package ph.mcmod.cs.game

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import ph.mcmod.cs.MyRegistries

class ItemQueueBlockEntity(pos: BlockPos, state: BlockState) : ItemPortsBlockEntity(MyRegistries.MyBlockEntityTypes.ITEM_QUEUE, pos, state) {
    override val itemStorage: QueueItemStorage = QueueItemStorage(this)
    
    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return ServerItemQueueGuiDescription(syncId, inv, itemStorage)
    }
    
    override fun getDisplayName(): Text {
        return cachedState.block.name
    }
}