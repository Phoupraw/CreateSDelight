@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
package ph.mcmod.cs.game

import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import ph.mcmod.kum.ItemStorable

abstract class ItemStorageBlock(settings: Settings) : BlockWithEntity(settings) {
    abstract override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL
    
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        ItemStorable.scatterOnStateReplaced(this, state, world, pos, newState, moved)
        super.onStateReplaced(state, world, pos, newState, moved)
    }
}