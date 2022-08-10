package ph.mcmod.cs.game

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class ItemStackBlock(settings: Settings) : ItemStorageGuiBlock(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ItemStackBlockEntity(pos, state)
    }
}