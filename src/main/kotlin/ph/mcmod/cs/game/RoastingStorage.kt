package ph.mcmod.cs.game

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.block.DoubleBlockProperties
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import ph.mcmod.cs.api.simpleSlot
import ph.mcmod.kum.asStorage

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
                val nbt = insertedVariant.nbt ?: NbtCompound()
                nbt.putInt("roastingCountdown", 100)
                variant = ItemVariant.of(insertedVariant.item, nbt)
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

class RoastingStorage1(var blockEntity: SyncedTileEntity?) : SnapshotParticipant<Unit>(), Storage<ItemVariant> {
    val capacity = 5
    val size
        get() = storage.parts.size
    val storage = CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>>(mutableListOf())
    override fun createSnapshot() {
    }
    
    override fun readSnapshot(snapshot: Unit) {
    
    }
    
    override fun onFinalCommit() {
        blockEntity?.notifyUpdate()
    }
    
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        createSnapshot()
        var result = storage.insert(resource, maxAmount, transaction)
        if (result == 0L && size + getSize(resource) <= capacity) {
            storage.parts.add(simpleSlot())
            result = storage.insert(resource, maxAmount, transaction)
        }
        return result
    }
    
    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext?): Long {
        TODO("Not yet implemented")
    }
    
    override fun iterator(transaction: TransactionContext?): MutableIterator<StorageView<ItemVariant>> {
        TODO("Not yet implemented")
    }
    
    companion object {
        val MAP = mutableMapOf<Item, Int>()
        fun getSize(itemVariant: ItemVariant) = MAP.getOrDefault(itemVariant.item, 1)
    }
}