package ph.mcmod.csd.game

import com.simibubi.create.foundation.item.SmartInventory
import com.simibubi.create.foundation.tileEntity.SmartTileEntity
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import ph.mcmod.csd.MyRegistries
import ph.mcmod.kum.forEach
import ph.mcmod.kum.getOrNull
import ph.mcmod.kum.putOrRemove
import ph.mcmod.kum.removeNbtIfEmpty

class DryingRackBlockEntity(pos: BlockPos?, state: BlockState?) : SmartTileEntity(MyRegistries.MyBlockEntityTypes.DRYING_RACK, pos, state) {
    val inventory = SmartInventory(2, this, 1, false)
    val filtering = object : FilteringStorage<ItemVariant>(inventory) {
        override fun canInsert(resource: ItemVariant): Boolean {
            return canInsert(resource.toStack())
        }
    }
    
    override fun addBehaviours(behaviours: MutableList<TileEntityBehaviour>?) {
    
    }
    
    override fun tick() {
        super.tick()
        val world = world ?: return
        inventory.forEach { index, stack, remove, set ->
            if (stack.isEmpty) return@forEach
            getRecipe(stack)?.also { recipe ->
                val duration = stack.dryingDuration ?: recipe.duration
                if (duration > 0) {
                    stack.dryingDuration = duration - 1
                } else {
                    set(recipe.output.copy())
                }
            }
        }
    }
    
    override fun write(tag: NbtCompound, clientPacket: Boolean) {
        super.write(tag, clientPacket)
        tag.put("items", inventory.serializeNBT())
    }
    
    override fun read(tag: NbtCompound, clientPacket: Boolean) {
        super.read(tag, clientPacket)
        inventory.deserializeNBT(tag.getCompound("items"))
    }
    
    fun getRecipe(stack: ItemStack): DryingRecipe? {
        return InjectItemDrainTileEntity.getRecipe(world ?: return null, stack, MyRegistries.MyRecipeTypes.DRYING)
    }
    
    fun canInsert(stack: ItemStack): Boolean {
        return true//getRecipe(stack) != null
    }
    
    companion object {
        var ItemStack.dryingDuration: Double?
            get() = nbt?.getOrNull("dryingDuration", NbtCompound::getDouble)
            set(value) {
                orCreateNbt.putOrRemove("dryingDuration", value, NbtCompound::putDouble)
                removeNbtIfEmpty()
            }
    }
}