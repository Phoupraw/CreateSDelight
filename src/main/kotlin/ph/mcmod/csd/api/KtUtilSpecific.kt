@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "OVERRIDE_DEPRECATION")

package ph.mcmod.csd.api

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand
import net.minecraft.world.World
import ph.mcmod.kum.*
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

var ItemStack.customModelData: Int?
    get() = orCreateNbt.getOrNull("CustomModelData", NbtCompound::getInt)
    set(value) {
        orCreateNbt.putOrRemove("CustomModelData", value, NbtCompound::putInt)
        removeNbtIfEmpty()
    }

fun <T> Optional<T>.getOrNull(): T? = orElse(null)

fun ItemStack.modelHasDepth(world: World? = null) = MinecraftClient.getInstance().itemRenderer.getModel(this, world, null, 0).hasDepth()

class HandStackStorage(val livingEntity: LivingEntity, val hand: Hand) : SingleStackStorage() {
    override fun getStack(): ItemStack {
        return livingEntity.getStackInHand(hand)
    }
    
    override fun setStack(stack: ItemStack?) {
        livingEntity.setStackInHand(hand, stack)
    }
}

//class DoubleInItemStack(val itemStack: ItemStack, val key: String) : ReadWriteProperty<Any?, Double?> {
//    override fun getValue(thisRef: Any?, property: KProperty<*>): Double? {
//        return itemStack.nbt?.getOrNull(key, NbtCompound::getDouble)
//    }
//
//    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double?) {
//        itemStack.nbt?.putOrRemove(key, value, NbtCompound::putDouble)
//        itemStack.removeNbtIfEmpty()
//    }
//}