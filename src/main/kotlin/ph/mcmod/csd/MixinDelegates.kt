@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package ph.mcmod.csd

import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.BlockState
import net.minecraft.block.FluidBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.tag.FluidTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

internal object MixinDelegates {
    
    @JvmStatic
    fun useBowlOnWater(fluidBlock: FluidBlock, state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (fluidBlock.fluid.isIn(FluidTags.WATER)) {
            val stackInHand = player.getStackInHand(hand)
            if (stackInHand.isOf(Items.BOWL)) {
                if (player is ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(player, pos, stackInHand)
                }
                if (!player.isCreative) {
                    stackInHand.decrement(1)
                    player.inventory.offerOrDrop(MyRegistries.MyItems.WATER_BOWL.defaultStack)
                }
                return ActionResult.SUCCESS
            }
        }
        return ActionResult.PASS
    }
    
    
}