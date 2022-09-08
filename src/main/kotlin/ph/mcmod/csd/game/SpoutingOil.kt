package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour
import com.simibubi.create.content.contraptions.fluids.actors.SpoutTileEntity
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline.result
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable
import io.github.fabricators_of_create.porting_lib.util.FluidStack
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object SpoutingOil : BlockSpoutingBehaviour() {
    override fun fillBlock(world: World, pos: BlockPos, te: SpoutTileEntity, availableFluid: FluidStack, simulate: Boolean): Long {
        val depot = world.getBlockEntity(pos) as? DepotTileEntity ?: return 0
        val fire = world.getBlockState(pos.down())
        if (!fire.isOf(AllBlocks.LIT_BLAZE_BURNER.get())) {
            return 0
        }
        val fluidStorage = (depot as FluidTransferable).getFluidStorage(null) ?: return 0
        return Transaction.openOuter().use {
            (
              if (simulate)
                  fluidStorage.simulateInsert(availableFluid.type, 1, it)
              else
                  fluidStorage.insert(availableFluid.type, 1, it)
              ).apply {
                  it.commit()
              }
        }
    }
}