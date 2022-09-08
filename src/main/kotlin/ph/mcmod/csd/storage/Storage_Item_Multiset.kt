package ph.mcmod.csd.storage

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import ph.mcmod.csd.api.*
import ph.mcmod.csd.stackSpace
import ph.mcmod.kum.*

open class Storage_Item_Multiset : SnapshotParticipant<Fraction>(), CommitListenable.TStorage<ItemVariant>, NbtSerializable {
    val map: MutableMap<ItemVariant, SingleSlotStorage<ItemVariant>> = LinkedHashMap()
    val pendings: MutableSet<ItemVariant> = hashSetOf()
    open val capacity = 27 f 1
    var space by LazyVar { capacity }
    val committer = CommitListenable.Helper()
    
    override fun createSnapshot() = space
    
    override fun readSnapshot(snapshot: Fraction) {
        space = snapshot
    }
    
    override fun onFinalCommit() {
        pendings.clear()
        committer.finalCommit()
    }
    
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long = map.getOrPut(resource) { newSlot() }.insert(resource, maxAmount, transaction)
    
    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long = exactView(transaction, resource).extract(resource, maxAmount, transaction)
    
    override fun iterator(transaction: TransactionContext?): Iterator<StorageView<ItemVariant>> {
        return object : Iterator<StorageView<ItemVariant>> {
            val ite = map.values.iterator()
            override fun hasNext(): Boolean = ite.hasNext()
    
            override fun next(): StorageView<ItemVariant> {
                return ite.next().also {
                    if (it.empty && it.resource !in pendings)
                        ite.remove()
                }
            }
        }
    }
    
    override fun exactView(transaction: TransactionContext, resource: ItemVariant): StorageView<ItemVariant> = map.getOrDefault(resource, ITEM_BLANK_VIEW)
    override fun listenFinalCommit(callback: KRunnable) {
        committer.listenFinalCommit(callback)
    }
    
    override fun toNbt(root: NbtCompound): NbtCompound {
        root.put("items", NbtList().also { it += map.map { (k, v) -> NbtCompound().from(k, v.amount) } })
        root.put("space", space.toNbt())
        return root
    }
    
    override fun fromNbt(root: NbtCompound) {
        map.clear()
        root.getCompoundList("items").forCompound { _, compound, _, _ ->
            val (variant, amount) = compound.toItem()
            map[variant] = newSlot().also {
                it.variant = variant
                it.amount = amount
            }
        }
        space =
          if (!root.containsString("space")) capacity
          else root.getStringList("space").toFraction()
    }
    
    open fun newSlot(): SingleVariantStorage<ItemVariant> {
        return Slot()
    }
    
    open inner class Slot : SingleVariantStorage<ItemVariant>() {
        override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
        override fun getCapacity(variant: ItemVariant): Long = ((amount * resource.stackSpace + space) / variant.stackSpace).toLong()
        override fun insert(insertedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
            return super.insert(insertedVariant, maxAmount, transaction).also {
                if (it != 0L) {
                    pendings += insertedVariant
                    this@Storage_Item_Multiset.updateSnapshots(transaction)
                    space -= it * insertedVariant.stackSpace
                }
            }
        }
        
        override fun extract(extractedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
            return super.extract(extractedVariant, maxAmount, transaction).also {
                if (it != 0L) {
                    pendings += extractedVariant
                    this@Storage_Item_Multiset.updateSnapshots(transaction)
                    space += it * extractedVariant.stackSpace
                }
            }
        }
    }
}
