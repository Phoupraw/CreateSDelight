package ph.mcmod.cs.game

import com.simibubi.create.content.contraptions.fluids.actors.SpoutTileEntity
import com.simibubi.create.content.contraptions.processing.BasinTileEntity
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline.result
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.registry.Registry
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.cs.api.printS
import ph.mcmod.kum.always
import ph.mcmod.kum.containsString
import ph.mcmod.kum.id
import kotlin.math.pow

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