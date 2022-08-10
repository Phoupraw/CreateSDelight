package ph.mcmod.cs.game

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.math.BlockPos
import ph.mcmod.kum.ItemStorable

abstract class ItemPortsBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state), NamedScreenHandlerFactory, ItemStorable<QueueItemStorage> {
    override fun writeNbt(root: NbtCompound) {
        super.writeNbt(root)
        itemStorage.toNbt(root)
    }
    
    override fun readNbt(root: NbtCompound) {
        super.readNbt(root)
        itemStorage.fromNbt(root)
    }
    
    abstract override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler
    
}