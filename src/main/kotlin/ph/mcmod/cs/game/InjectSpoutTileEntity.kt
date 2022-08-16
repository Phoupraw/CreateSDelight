package ph.mcmod.cs.game

import com.simibubi.create.content.contraptions.fluids.actors.SpoutTileEntity
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.util.math.MathHelper
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.kum.always
import kotlin.math.pow

interface InjectSpoutTileEntity {
    @ApiStatus.Internal
    companion object {
        
        @JvmStatic
        fun isNotOil(te: SpoutTileEntity): Boolean {
            return te.customProcess !is SpoutingOil
        }
        
        @JvmStatic
        fun tickOil(te: SpoutTileEntity) {
            if (isNotOil(te)) {
                return
            }
            val world = te.world ?: return
            val target = (world.getBlockEntity(te.pos.down(2)) as? FluidTransferable)?.getFluidStorage(null) ?: return
            val source = te.getFluidStorage(null) ?: return
            
            val radius =1- (2 * MathHelper.clamp(1 - (te.processingTicks - 5.0) / 10, 0.0, 1.0) - 1).pow(2)
//            print("$radius ")
            val amount = (radius * FluidConstants.INGOT).toLong()
            if (amount > 0)
                StorageUtil.move(source, target, always(), amount, null)
        }
    }
}