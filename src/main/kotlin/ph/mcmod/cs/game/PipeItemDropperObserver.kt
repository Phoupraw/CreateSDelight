@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package ph.mcmod.cs.game

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.SimpleInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import ph.mcmod.cs.MyRegistries
import ph.mcmod.kum.*

object PipeItemDropperObserver {
    class Block(settings: Settings) : FacingBlock(settings), BlockEntityProvider {
        override fun createBlockEntity(pos: BlockPos, state: BlockState): net.minecraft.block.entity.BlockEntity {
            return BlockEntity(pos, state)
        }
        
        override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: net.minecraft.block.Block, fromPos: BlockPos, notify: Boolean) {
            super.neighborUpdate(state, world, pos, block, fromPos, notify)
            (world.getBlockEntity(pos) as? BlockEntity)?.apply {
                tryDrop()
            }
        }
        
        override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
            if (!newState.isOf(this)) {
                (world.getBlockEntity(pos) as? BlockEntity)?.apply {
                    ItemStorable.scatter(this)
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }
    
    class BlockEntity(pos: BlockPos, state: BlockState) : net.minecraft.block.entity.BlockEntity(MyRegistries.MyBlockEntityTypes.OBSERVER_DROPPER, pos, state), ItemStorable<InventoryStorage> {
        override val itemStorage: InventoryStorage = InventoryStorage.of(SimpleInventory(9).apply { addListener { this@BlockEntity.markDirty() } }, null)
        val facing: Direction
            get() = cachedState.facing
        val targetPos: BlockPos
            get() = pos.offset(facing)
    
        fun tryDrop(): Boolean {
            val world = world ?: return false
            val target = ItemStorage.SIDED.find(world, targetPos, facing.opposite)
            return StorageUtil.move(itemStorage, target, { true }, 1, null) >= 1
        }
        
        override fun writeNbt(root: NbtCompound) {
            super.writeNbt(root)
            root.put("items", NbtList().append(itemStorage))
        }
        
        override fun readNbt(root: NbtCompound) {
            super.readNbt(root)
            itemStorage.read(root.getCompoundList("items"))
        }
    }
}