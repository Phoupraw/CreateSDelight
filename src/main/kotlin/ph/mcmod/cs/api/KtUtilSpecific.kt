@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "OVERRIDE_DEPRECATION")

package ph.mcmod.cs.api

import com.google.common.collect.Table
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.BlankVariantView
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.Direction
import java.math.BigInteger
import java.util.function.Predicate
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

//-----













interface KEvent<T> {
    val callbacks: Iterable<T>
    operator fun plusAssign(callback: T)
    operator fun minusAssign(callback: T)
}

open class KEventImpl<T> : KEvent<T> {
    private val _callbacks: MutableCollection<T> = mutableListOf()
    override val callbacks: Iterable<T> get() = _callbacks
    
    override operator fun plusAssign(callback: T) {
        _callbacks += callback
    }
    
    override operator fun minusAssign(callback: T) {
        _callbacks -= callback
    }
}

class KEvent0 : KEventImpl<() -> Unit>(), () -> Unit {
    override operator fun invoke() {
        for (callback in callbacks) callback()
    }
}

class KEvent1<T> : KEventImpl<(T) -> Unit>(),(T) -> Unit {
    override operator fun invoke(t: T) {
        for (callback in callbacks) callback(t)
    }
}

class KEvent2<T, U> : KEventImpl<(T, U) -> Unit>(),(T, U) -> Unit {
    override operator fun invoke(t: T, u: U) {
        for (callback in callbacks) callback(t, u)
    }
}

class KEvent3<T, U, V> : KEventImpl<(T, U, V) -> Unit>() ,(T, U, V) -> Unit{
    override operator fun invoke(t: T, u: U, v: V) {
        for (callback in callbacks) callback(t, u, v)
    }
}