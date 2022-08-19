package ph.mcmod.cs.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import ph.mcmod.cs.MyRegistries

class BowlItem(settings: Settings?) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack>? {
        val stackInHand = user.getStackInHand(hand)
        val hitResult: HitResult = raycast(world, user, RaycastContext.FluidHandling.WATER)
        if (hitResult.type != HitResult.Type.MISS && hitResult is BlockHitResult) {
            val blockPos = hitResult.blockPos
            val fluid = world.getFluidState(blockPos).fluid
            if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) {
                world.playSound(user, user.x, user.y, user.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f)
                world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos)
                user.incrementStat(Stats.USED.getOrCreateStat(this))
                return TypedActionResult.success(ItemUsage.exchangeStack(stackInHand, user, MyRegistries.MyItems.WATER_BOWL.defaultStack))
            }
        }
        return TypedActionResult.pass(stackInHand)
    }
}