package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllTags.AllBlockTags
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileEntity
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import ph.mcmod.csd.MyRegistries
import ph.mcmod.csd.game.RoastingStorage.Companion.roastingDuration
import ph.mcmod.kum.getOrNull
import ph.mcmod.kum.putOrRemove
import ph.mcmod.kum.removeNbtIfEmpty
import kotlin.math.pow

class RoastingStorage(var te: BracketedKineticTileEntity) : SingleStackStorage() {
    private var stack = ItemStack.EMPTY
    val finished
        get() = findRecipe() == null
    
    override fun onFinalCommit() {
        super.onFinalCommit()
        findRecipe()?.also { recipe ->
            stack.roastingDuration ?: run {
                stack.roastingDuration = recipe.duration
            }
        }
        te.notifyUpdate()
    }
    
    public override fun getStack(): ItemStack {
        return stack
    }
    
    public override fun setStack(stack: ItemStack) {
        this.stack = stack
    }
    
    override fun canInsert(itemVariant: ItemVariant): Boolean {
        return findRecipe(itemVariant.toStack()) != null
    }
    
    override fun getCapacity(itemVariant: ItemVariant?): Int {
        return 1
    }
//    override fun canExtract(itemVariant: ItemVariant): Boolean {
//        return findRecipe(itemVariant.toStack()) == null
//    }
    
    fun findRecipe(itemStack: ItemStack = this.stack): SingleRecipe? {
        te.world?.also { world ->
            for (direction in Direction.values()) {
                if (world.getBlockState(te.pos.offset(direction)).isIn(AllBlockTags.PASSIVE_BOILER_HEATERS.tag)) {
                    return InjectItemDrainTileEntity.getRecipe(world, itemStack, MyRegistries.MyRecipeTypes.ROASTING)
                }
            }
        }
        return null
    }
    
    fun tick() {
//        return
        findRecipe()?.also { recipe ->
            val duration = stack.roastingDuration ?: recipe.duration
            if (duration > 0) {
                val step = amount.toDouble().pow(-1 / 3.0)
                stack.roastingDuration = duration - step
            } else {
                stack = recipe.output.copy().apply { count = stack.count }
                te.notifyUpdate()
            }
        }
    }
    
    companion object {
        var ItemStack.roastingDuration: Double?
            get() = nbt?.getOrNull("roastingDuration", NbtCompound::getDouble)
            set(value) {
                orCreateNbt.putOrRemove("roastingDuration", value, NbtCompound::putDouble)
                removeNbtIfEmpty()
            }
    }
}

