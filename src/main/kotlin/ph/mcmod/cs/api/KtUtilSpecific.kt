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

infix fun Double.modAndDiv(divisor:Number): Double = this % divisor.toDouble() / divisor.toDouble()
infix fun Float.modAndDiv(divisor:Number): Float = this % divisor.toFloat() / divisor.toFloat()













