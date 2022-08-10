package ph.mcmod.cs.game

import net.devtech.arrp.json.blockstate.JState.variant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import ph.mcmod.cs.api.*
import ph.mcmod.cs.stackSpace
import ph.mcmod.cs.storage.CommitListenable
import ph.mcmod.kum.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.LinkedHashMap

class SetItemStorage(val blockEntity: BlockEntity) : SnapshotParticipant<Int>(), Storage<ItemVariant>, NbtSerializable {
    val map: MutableMap<ItemVariant, SingleSlotStorage<ItemVariant>> = LinkedHashMap()
    val pendings: MutableSet<ItemVariant> = hashSetOf()
    val capacity = 27 * 64
    var space = capacity
    val iterators = WeakHashSet<Iter>()
    override fun createSnapshot() = space
    
    override fun readSnapshot(snapshot: Int) {
        space = snapshot
    }
    
    override fun onFinalCommit() {
        pendings.clear()
        blockEntity.markDirty()
        for (iterator in iterators) {
            iterator.ite = map.values.iterator()
        }
    }
    
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long = map.getOrPut(resource) { Slot() }.insert(resource, maxAmount, transaction)
    
    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long = exactView(transaction, resource).extract(resource, maxAmount, transaction)
    
    override fun iterator(transaction: TransactionContext?): Iterator<StorageView<ItemVariant>> {
        return Iter()
    }
    
    override fun exactView(transaction: TransactionContext, resource: ItemVariant): StorageView<ItemVariant> = map.getOrDefault(resource, ITEM_BLANK_VIEW)
    
    override fun toNbt(root: NbtCompound): NbtCompound {
        root.put("items", map.values.mapTo(NbtList()) { it.resource.toNbt() })
        root.putInt("space", space)
        return root
    }
    
    override fun fromNbt(root: NbtCompound) {
        map.clear()
        root.getCompoundList("items").forCompound { _, compound, _, _ ->
            val variant = ItemVariant.fromNbt(compound)
            map[variant] = Slot().also {
                it.variant = variant
                it.amount = 1
            }
        }
        space =
          if (!root.containsString("space")) capacity
          else root.getInt("space")
    }
    
    inner class Slot : SingleVariantStorage<ItemVariant>() {
        override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
        override fun getCapacity(variant: ItemVariant): Long = 1
        override fun insert(insertedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
            return super.insert(insertedVariant, maxAmount, transaction).also {
                if (it != 0L) {
                    pendings += insertedVariant
                    this@SetItemStorage.updateSnapshots(transaction)
                    space -= 1
                }
            }
        }
        
        override fun extract(extractedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
            return super.extract(extractedVariant, maxAmount, transaction).also {
                if (it != 0L) {
                    pendings += extractedVariant
                    this@SetItemStorage.updateSnapshots(transaction)
                    space += 1
                }
            }
        }
    }
    
    inner class Iter : Iterator<StorageView<ItemVariant>> {
        init {
            iterators += this
        }
        
        var ite = map.values.iterator()
        override fun hasNext(): Boolean = ite.hasNext()
        
        override fun next(): StorageView<ItemVariant> {
            return ite.next().also {
                if (it.empty && it.resource !in pendings)
                    ite.remove()
            }
        }
    }
}