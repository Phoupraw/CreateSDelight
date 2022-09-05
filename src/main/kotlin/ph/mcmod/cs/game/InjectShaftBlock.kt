package ph.mcmod.cs.game

import com.simibubi.create.AllTags.AllBlockTags
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import ph.mcmod.cs.api.HandStackStorage
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
//                        Items.ACACIA_BOAT.registryEntry.isIn()
//                        AllBlockTags.PASSIVE_BOILER_HEATERS.tag
                        cir.returnValue = ActionResult.SUCCESS
                    }
                }
            }
        }
    }
}