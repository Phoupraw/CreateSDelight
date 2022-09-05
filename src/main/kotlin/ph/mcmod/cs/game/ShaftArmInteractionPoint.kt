package ph.mcmod.cs.game

import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPointType
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ShaftArmInteractionPoint(type: ArmInteractionPointType?, level: World?, pos: BlockPos?, state: BlockState?) : ArmInteractionPoint(type, level, pos, state) {
    override fun getHandler(): Storage<ItemVariant>? {
        return (level.getBlockEntity(pos) as? InjectBracketedKineticTileEntity)?.roastingStorage
    }
    
    override fun extract(amount: Int, ctx: TransactionContext?): ItemStack {
        (handler as? RoastingStorage)?.also { storage ->
            if (!storage.finished) return ItemStack.EMPTY
        }
        return super.extract(amount, ctx)
    }
}