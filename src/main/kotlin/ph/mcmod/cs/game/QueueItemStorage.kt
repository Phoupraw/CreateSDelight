package ph.mcmod.cs.game

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
import ph.mcmod.kum.*
import java.util.*

open class QueueItemStorage(val blockEntity: BlockEntity) : SnapshotParticipant<Fraction>(), Storage<ItemVariant>, NbtSerializable {
    open val queue: Queue<SingleSlotStorage<ItemVariant>> = ArrayDeque()
    val pendings: MutableSet<StorageView<ItemVariant>> = hashSetOf()
    val capacity = 27 f 1
    var space = capacity
    val peek: StorageView<ItemVariant>
        get() {
            queue.forEach { slot, remove ->
                if (!slot.empty) return slot
                if (slot !in pendings) remove()
            }
            return ITEM_BLANK_VIEW
        }
    val isEmpty: Boolean
        get() = queue.isEmpty()
    var runnable: Runnable? = null
    override fun createSnapshot(): Fraction {
        return space
    }
    
    override fun readSnapshot(snapshot: Fraction) {
        space = snapshot
    }
    
    override fun onFinalCommit() {
        pendings.clear()
        blockEntity.markDirty()
        runnable?.run()
    }
    
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        if (queue.isEmpty())
            addSlot(Slot())
        val result = queue.peek().insert(resource, maxAmount, transaction)
        if (result != 0L)
            return result
        val empty = Slot()
        addSlot(empty)
        return empty.insert(resource, maxAmount, transaction)
    }
    
    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        return peek.extract(resource, maxAmount, transaction)
    }
    
    override fun iterator(transaction: TransactionContext): Iterator<StorageView<ItemVariant>> {
        if (queue.isEmpty())
            return queue.iterator()
        return object : Iterator<StorageView<ItemVariant>> {
            var last: StorageView<ItemVariant> = ITEM_BLANK_VIEW
            override fun next(): StorageView<ItemVariant> = peek.also { last = it }
            override fun hasNext(): Boolean = last !== peek
        }
    }
    
    override fun exactView(transaction: TransactionContext, resource: ItemVariant): StorageView<ItemVariant>? {
        return if (resource == peek.resource) peek else null
    }
    
    override fun toNbt(root: NbtCompound): NbtCompound {
        root.put("items", NbtList().apply { this += queue.filterNot { it.empty }.map { NbtCompound().from(it.resource, it.amount) } })
        root.put("space", space.toNbt())
        return root
    }
    
    override fun fromNbt(root: NbtCompound) {
        queue.clear()
        root.getCompoundList("items").forCompound { _, compound, _, _ ->
            val (variant, amount) = compound.toItem()
            queue.offer(Slot().also {
                it.variant = variant
                it.amount = amount
            })
        }
        space =
          if (!root.containsString("space")) capacity
          else root.getStringList("space").toFraction()
    }
    
    open fun addSlot(slot: Slot) {
        queue.offer(slot)
    }
    
    inner class Slot : SingleVariantStorage<ItemVariant>() {
        override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
        override fun getCapacity(variant: ItemVariant): Long = ((amount * resource.stackSpace + space) / variant.stackSpace).toLong()
        override fun insert(insertedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
            return super.insert(insertedVariant, maxAmount, transaction).also {
                if (it != 0L) {
                    pendings += this
                    this@QueueItemStorage.updateSnapshots(transaction)
                    space -= it * insertedVariant.stackSpace
                }
            }
        }
        
        override fun extract(extractedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
            return super.extract(extractedVariant, maxAmount, transaction).also {
                if (it != 0L) {
                    pendings += this
                    this@QueueItemStorage.updateSnapshots(transaction)
                    space += it * extractedVariant.stackSpace
                }
            }
        }
    }
}