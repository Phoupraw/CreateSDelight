package ph.mcmod.cs.game

import com.simibubi.create.AllTileEntities
import com.simibubi.create.content.logistics.block.depot.DepotRenderer
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity
import com.simibubi.create.foundation.fluid.FluidRenderer
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import org.jetbrains.annotations.ApiStatus

interface InjectDepotRenderer {
    @ApiStatus.Internal
    companion object {
        @JvmStatic
        fun renderFluids(renderer: DepotRenderer, te: DepotTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int) {
            te as InjectDepotTileEntity
            val totalUnits: Float = run {
                var renderedFluids = 0
                var totalUnits = 0f
                for (tankSegment in te.tank.tanks) {
                    if (tankSegment.renderedFluid.isEmpty) continue
                    val units = tankSegment.getTotalUnits(partialTicks)
                    if (units < 1) continue
                    totalUnits += units
                    renderedFluids++
                }
                if (renderedFluids == 0) 0f
                else if (totalUnits < 1) 0f
                else totalUnits
            }
            if (totalUnits < 1) return
            
            var fluidLevel = MathHelper.clamp(totalUnits / (FluidConstants.INGOT * 1), 0f, 1f)
            
            fluidLevel = 1 - (1 - fluidLevel) * (1 - fluidLevel)
            
            val breadth = fluidLevel * 10 / 16f
            var xMin = 1/2f - breadth/2
            var xMax = 1/2f + breadth/2
            val yMin = 0.8125f
            val yMax = yMin + 0.02f //* fluidLevel
            val zMin = 1/2f - breadth/2
            val zMax = 1/2f + breadth/2
            
            for (tankSegment in te.tank.tanks) {
                val renderedFluid = tankSegment.renderedFluid
                if (renderedFluid.isEmpty) continue
                val units = tankSegment.getTotalUnits(partialTicks)
                if (units < 1) continue
                val partial = MathHelper.clamp(units / totalUnits, 0f, 1f)
//                xMax += partial * 10 / 16f
                FluidRenderer.renderFluidBox(renderedFluid, xMin, yMin, zMin, xMax, yMax, zMax, buffer, ms, light, false)
//                xMin = xMax
            }
        }
    }
}
