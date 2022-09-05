package ph.mcmod.cs.game

import com.simibubi.create.content.contraptions.processing.BasinTileEntity
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import ph.mcmod.kum.containsString

interface InjectBasinTileEntity {
   
    companion object {
        @JvmStatic
        fun modifyOutput(te: BasinTileEntity, targetInv: Storage<ItemVariant>, itemVariant: ItemVariant, amount: Long, nested: TransactionContext): Long {
            val nbt = itemVariant.nbt
            if (nbt?.containsString("needItem") == true) {
                val need = Registry.ITEM.get(Identifier(nbt.getString("needItem")))
                if (need != Items.AIR) {
                    var result = 0L
                    StorageUtil.findExtractableContent(targetInv, { it.item == need }, nested)?.also { content ->
                        if (amount == content.amount) {
                            Transaction.openNested(nested).use { transaction ->
                                val newNbt = nbt.copy()
                                newNbt.remove("needItem")
                                newNbt.remove("CustomModelData")
                                newNbt.putInt("render", BasinTileEntity.OUTPUT_ANIMATION_TIME - 4)
                                val newVariant = ItemVariant.of(itemVariant.item, newNbt)
                                targetInv.extract(content.resource, amount, transaction)
                                if (targetInv.insert(newVariant, amount, transaction) == amount) {
                                    result = amount
                                    transaction.commit()
                                }
                            }
                        }
                    }
                    return result
                }
            }
            return targetInv.insert(itemVariant, amount, nested)
        }
        
    }
}