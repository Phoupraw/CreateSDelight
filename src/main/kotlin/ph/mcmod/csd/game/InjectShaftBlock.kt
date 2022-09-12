package ph.mcmod.csd.game

import com.simibubi.create.content.contraptions.base.RotatedPillarKineticBlock
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock
import com.simibubi.create.content.contraptions.relays.elementary.SimpleKineticTileEntity
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import ph.mcmod.csd.MyRegistries
import ph.mcmod.kum.always
import ph.mcmod.kum.asStorage
import ph.mcmod.kum.isEmpty

interface InjectShaftBlock {
    companion object {
        @JvmStatic
        fun putRoastOnUse(block: ShaftBlock, state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, ray: BlockHitResult, cir: CallbackInfoReturnable<ActionResult>) {
            (world.getBlockEntity(pos) as? InjectBracketedKineticTileEntity)?.also { te ->
                val handStorage = player.inventory.asStorage().getHandSlot(hand)
                if (handStorage.isEmpty) {
                    StorageUtil.move(te.roastingStorage, handStorage, always(), Long.MAX_VALUE, null)
                } else {
                    StorageUtil.move(handStorage, te.roastingStorage, always(), Long.MAX_VALUE, null)
                }.also { amount ->
                    if (amount > 0) {
                        cir.returnValue = ActionResult.SUCCESS
                    }
                }
            }
        }
    
        @JvmStatic
        fun onWrenched(block: ShaftBlock, state: BlockState, context: ItemUsageContext): ActionResult {
            val axis = state.get(RotatedPillarKineticBlock.AXIS)
            if (axis==Direction.Axis.Y)return ActionResult.PASS
            val world = context.world
            val side = context.side
            if (side.axis!=axis) return ActionResult.PASS
            val pos = context.blockPos
            val te = world.getBlockEntity(pos)as? SimpleKineticTileEntity ?:return ActionResult.PASS
            if (te.speed!=0f) return ActionResult.PASS
            world.setBlockState(pos,MyRegistries.MyBlocks.DRYING_RACK.defaultState.with(DryingRackBlock.AXIS_X,axis==Direction.Axis.X))
            return ActionResult.SUCCESS
        }
    }
}