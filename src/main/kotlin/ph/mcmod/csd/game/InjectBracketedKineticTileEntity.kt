package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import ph.mcmod.csd.MyRegistries
import ph.mcmod.csd.game.RoastingStorage.Companion.roastingDuration

interface InjectBracketedKineticTileEntity {
    val roastingStorage: RoastingStorage
    
    companion object {
        @JvmStatic
        fun writeRoastingNbt(te: BracketedKineticTileEntity, compound: NbtCompound, clientPacket: Boolean) {
            te as InjectBracketedKineticTileEntity
            compound.put("roastingItem", NbtCompound().also { te.roastingStorage.stack.writeNbt(it) })
        }
        
        @JvmStatic
        fun readRoastingNbt(te: BracketedKineticTileEntity, compound: NbtCompound, clientPacket: Boolean) {
            te as InjectBracketedKineticTileEntity
            te.roastingStorage.stack = ItemStack.fromNbt(compound.getCompound("roastingItem"))
        }
        
        @JvmStatic
        fun tick(te: BracketedKineticTileEntity) {
            te as InjectBracketedKineticTileEntity
            te.roastingStorage.tick()
        }
    }
    
}