@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "OVERRIDE_DEPRECATION")

package ph.mcmod.cs.api

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.minecraft.inventory.SimpleInventory
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.kum.asStorage

@ApiStatus.Experimental
infix fun Double.modAndDiv(divisor: Number): Double = this % divisor.toDouble() / divisor.toDouble()
@ApiStatus.Experimental
infix fun Float.modAndDiv(divisor: Number): Float = this % divisor.toFloat() / divisor.toFloat()
@ApiStatus.Experimental
fun simpleSlot(): SingleSlotStorage<ItemVariant> = SimpleInventory(1).asStorage().getSlot(0)
@ApiStatus.Experimental
class FixedSlotsFluidStorage(val size: Int, val capacityPerSlot: Long) : CombinedStorage<FluidVariant, SingleVariantStorage<FluidVariant>>(Array(size) { SingleFluidVariantStorage(capacityPerSlot) }.asList())
@ApiStatus.Experimental
class SingleFluidVariantStorage(val capacityPerSlot: Long) : SingleVariantStorage<FluidVariant>() {
    override fun getCapacity(variant: FluidVariant): Long {
        return capacityPerSlot
    }
    
    override fun getBlankVariant(): FluidVariant {
        return FluidVariant.blank()
    }
}
@ApiStatus.Experimental
fun <T : Any?> T.printS(): T {
    print("$this ")
    return this
}







