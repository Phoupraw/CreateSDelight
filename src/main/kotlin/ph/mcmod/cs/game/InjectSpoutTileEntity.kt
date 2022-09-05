package ph.mcmod.cs.game

import com.simibubi.create.content.contraptions.fluids.actors.SpoutTileEntity
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.kum.always
import kotlin.math.pow

interface InjectSpoutTileEntity {
    var shouldParticle: Boolean
    @ApiStatus.Internal
    companion object {
        
        @JvmStatic
        fun isOil(te: SpoutTileEntity): Boolean {
            if (te.pos == BlockPos(14, 3, 34) && te.customProcess != null) {
                te.world//.printS()
//                te.customProcess.printS()
            }
            return (te.customProcess is SpoutingOil)//.printS()
        }
        
        @JvmStatic
        fun tickOil(te: SpoutTileEntity) {
            if (!isOil(te)) {
                return
            }
            val world = te.world ?: return
            val target = (world.getBlockEntity(te.pos.down(2)) as? FluidTransferable)?.getFluidStorage(null) ?: return
            val source = te.getFluidStorage(null) ?: return
            
            val radius = 1 - (2 * MathHelper.clamp(1 - (te.processingTicks /*- 5.0*/) / 10.0, 0.0, 1.0) - 1).pow(2)
//            print("$radius ")
            val amount = FluidConstants.INGOT / 13//(radius * FluidConstants.INGOT / 6.2).toLong()
            if (amount > 0)
                StorageUtil.move(source, target, always(), amount, null)
        }
        
        @JvmStatic
        fun read(te: SpoutTileEntity, compound: NbtCompound, clientPacket: Boolean) {
            te as InjectSpoutTileEntity
            te.shouldParticle = compound.getBoolean("shouldParticle")
        }
        
        @JvmStatic
        fun write(te: SpoutTileEntity, compound: NbtCompound, clientPacket: Boolean) {
            te as InjectSpoutTileEntity
            compound.putBoolean("shouldParticle", te.shouldParticle)
        }
        
        @JvmStatic
        fun onSetCustomProcess(te: SpoutTileEntity) {
            te as InjectSpoutTileEntity
            te.shouldParticle = te.customProcess !is SpoutingOil
//            if (te.customProcess is SpoutingOil) {
//                te.shouldParticle = false
//            }
        }
    }
}