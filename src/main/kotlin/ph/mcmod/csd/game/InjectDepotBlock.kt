package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour
import com.simibubi.create.content.logistics.block.depot.DepotBlock
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.jetbrains.annotations.ApiStatus
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import ph.mcmod.csd.game.InjectDepotBehaviour.Companion.isFlipping
import ph.mcmod.csd.game.InjectDepotTileEntity.Companion.needingFlipping

interface InjectDepotBlock {
    @ApiStatus.Internal
    companion object {
        @JvmStatic
        fun flip(block: DepotBlock, state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hitResult: BlockHitResult, cir: CallbackInfoReturnable<ActionResult>) {
            if (world.isClient) {
                cir.returnValue = ActionResult.CONSUME
                return
            }
            val fire = world.getBlockState(pos.down())
            if (!fire.isOf(AllBlocks.LIT_BLAZE_BURNER.get())) {
                return
            }
            val stackInHand = player.getStackInHand(hand)
            if (!stackInHand.isIn(ConventionalItemTags.SHOVELS)) {
                return
            }
            var r = ActionResult.FAIL
            (world.getBlockEntity(pos) as? DepotTileEntity)?.also { te ->
                te as InjectDepotTileEntity
                val behaviour = te.getBehaviour(DepotBehaviour.TYPE)
                behaviour as InjectDepotBehaviour
                //debug
                val debug = false
                if (!behaviour.isFlipping || debug) {
                    val heldItemStack = behaviour.heldItemStack
                    if (heldItemStack.needingFlipping || debug) {
                        behaviour.isFlipping = true
                        r = ActionResult.SUCCESS
                        te.notifyUpdate()
                    }
                }
            }
            cir.returnValue = r
        }
    }
    
}