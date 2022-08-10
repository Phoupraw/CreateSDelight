package ph.mcmod.cs.game

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class ItemSetBlock(settings: Settings) : ItemStorageBlock(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ItemSetBlockEntity(pos, state)
    }
}