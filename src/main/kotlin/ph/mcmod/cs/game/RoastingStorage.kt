package ph.mcmod.cs.game

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator.nbt
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.nbt.NbtCompound
import ph.mcmod.kum.asStorage
import ph.mcmod.kum.containsInt

class RoastingStorage(var blockEntity: SyncedTileEntity?) : SingleVariantStorage<ItemVariant>() {
    val canBeExtracted
        get() = (resource.nbt?.getInt("roastingCountdown") ?: 0) <= 0
    
    override fun getCapacity(variant: ItemVariant?): Long {
        return 1
    }
    
    override fun getBlankVariant(): ItemVariant {
        return ItemVariant.blank()
    }
    
    override fun insert(insertedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext?): Long {
        return super.insert(insertedVariant, maxAmount, transaction).also {
            if (it > 0) {
                val nbt = insertedVariant.nbt?: NbtCompound()
                nbt.putInt("roastingCountdown",100)
                variant= ItemVariant.of(insertedVariant.item,nbt)
            }
        }
    }
    
    override fun supportsExtraction(): Boolean {
        return (resource.nbt?.getInt("roastingCountdown") ?: 0) <= 0
    }
    
    override fun onFinalCommit() {
        super.onFinalCommit()
        blockEntity?.notifyUpdate()
    }
}