package ph.mcmod.csd.game

import com.jozufozu.flywheel.util.transform.TransformStack
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour
import com.simibubi.create.content.logistics.block.depot.DepotRenderer
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity
import com.simibubi.create.foundation.fluid.FluidRenderer
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.csd.game.InjectDepotBehaviour.Companion.FLIPPING_TIME
import ph.mcmod.csd.game.InjectDepotBehaviour.Companion.isFlipping
import ph.mcmod.kum.containsInt
import kotlin.math.PI
import kotlin.math.sin

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
            val fluidLevel = MathHelper.clamp(totalUnits / (FluidConstants.INGOT * 1), 0f, 1f).let { 1 - (1 - it) * (1 - it) }
            val breadth = fluidLevel * 10 / 16f
            val xMin = 1 / 2f - breadth / 2
            val xMax = 1 / 2f + breadth / 2
            val yMin = 0.8125f
            val yMax = yMin + 0.02f //* fluidLevel
            val zMin = 1 / 2f - breadth / 2
            val zMax = 1 / 2f + breadth / 2
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
        
        @JvmStatic
        fun renderItems(renderer: DepotRenderer, te: DepotTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int, behaviour: DepotBehaviour) {

//            DepotRenderer.renderItemsOf(te, partialTicks, ms, buffer, light, overlay, behaviour)
//            return
            behaviour as InjectDepotBehaviour
            te as InjectDepotTileEntity
            val heldItem = behaviour.heldItem ?: return
            val heldItemStack = behaviour.heldItemStack
            if (behaviour.isFlipping) {
                val world = te.world ?: return
                val ticks = FLIPPING_TIME - behaviour.flippingCountdown + partialTicks.toDouble()
                val progress = MathHelper.clamp(ticks / FLIPPING_TIME, 0.0, 1.0)
                val rotation = heldItem.angle.toDouble()
                val height = sin(progress * PI) * 0.32
                val angle = progress * 180
                ms.push()
                //            ms.translate(0.5, 1.0, 0.5)
                //            ms.translate(0.0, 3.0, 0.0)
                TransformStack.cast(ms)
                  .centre()
                  .translateY(0.3125 + height)
                  .rotateX(90.0)
                  .rotateX(angle)
                  .rotateZ(-rotation)
                  .scale(0.5f)
                MinecraftClient.getInstance().itemRenderer.renderItem(heldItemStack, ModelTransformation.Mode.FIXED, light, overlay, ms, buffer, 0)
                ms.pop()
            } else if (heldItemStack.nbt?.containsInt("render") == true) {
                val render = heldItemStack.orCreateNbt.getInt("render")
                if (render > 0) {
                    heldItemStack.orCreateNbt.putInt("render", render - 1)
                } else {
                    heldItemStack.orCreateNbt.remove("render")
                    if (heldItemStack.orCreateNbt.isEmpty) {
                        heldItemStack.nbt = null
                    }
                }
                heldItem.stack = Items.BOWL.defaultStack
                DepotRenderer.renderItemsOf(te, partialTicks, ms, buffer, light, overlay, behaviour)
                heldItem.stack = heldItemStack
            } else {
                DepotRenderer.renderItemsOf(te, partialTicks, ms, buffer, light, overlay, behaviour)
            }
        }
    }
}
