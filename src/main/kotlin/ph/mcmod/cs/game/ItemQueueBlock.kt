package ph.mcmod.cs.game

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class ItemQueueBlock(settings: Settings) : ItemStorageGuiBlock(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ItemQueueBlockEntity(pos, state)
    }
}