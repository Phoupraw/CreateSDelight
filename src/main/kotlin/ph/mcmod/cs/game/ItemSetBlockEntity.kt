package ph.mcmod.cs.game

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import ph.mcmod.cs.MyRegistries
import ph.mcmod.kum.ItemStorable

class ItemSetBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(MyRegistries.MyBlockEntityTypes.ITEM_SET, pos, state), ItemStorable<SetItemStorage> {
    override val itemStorage = SetItemStorage(this)
    override fun writeNbt(root: NbtCompound) {
        super.writeNbt(root)
        root.copyFrom(itemStorage.toNbt())
    }
    
    override fun readNbt(root: NbtCompound) {
        super.readNbt(root)
        itemStorage.fromNbt(root)
    }
}