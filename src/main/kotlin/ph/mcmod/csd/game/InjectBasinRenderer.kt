package ph.mcmod.csd.game

import com.jozufozu.flywheel.util.transform.TransformStack
import com.simibubi.create.content.contraptions.processing.BasinRenderer
import com.simibubi.create.content.contraptions.processing.BasinTileEntity
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.injection.invoke.arg.Args
import ph.mcmod.csd.MixinDelegates
import java.util.*
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

interface InjectBasinRenderer {
    companion object {
        @JvmStatic
        fun changeHeight(renderer: BasinRenderer, args: Args, te: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int, fluidLevel: Float, level: Float, pos: BlockPos, random: Random, inv: Storage<ItemVariant>, stackCount: Int, stacks: MutableList<ItemStack>, anglePartition: Float, stack: ItemStack) {
            if (!InjectBasinTileEntity.BOILING)return
            te as InjectBasinTileEntity
            val t1 = InjectBasinTileEntity.mapTemperature(te)
            val world = te.world ?: return
            val hashCode = stack.item.hashCode()
            val hashOffset = hashCode.toDouble() / Int.MAX_VALUE
            val amplitude = min(min(fluidLevel, 0.15f), 1 - fluidLevel) * t1.pow(3)
            val baseVector = args.get<Vec3d>(0)
            val degree = args.get<Double>(1)
            val cycle0 = 229
            val ticks = te.animationTicks + partialTicks * t1.pow(3)
            val partial0 = (ticks % cycle0 / cycle0 + hashOffset) * PI * 2
            val sin0 = sin(sin(partial0) * PI * 2)
            val floating = (sin0 * amplitude)
            val value = baseVector.y
            val min = 0.125
            val max = 0.6
            val offset =
              if (value - amplitude < min)
                  min - (value - amplitude)
              else if (value + amplitude > max)
                  max - (value + amplitude)
              else 0.0
            args[0] = Vec3d(baseVector.x/* (1 + sin0 * 0.5) */, value + floating + offset, baseVector.z)
            val cycle1 = 1397
            val partial1 = ((world.time + partialTicks) % cycle1 / cycle1 + hashOffset) * PI * 2
            val sin1 = sin(partial1)
//        args[1] = degree + sin1 * 360 * hashOffset.sign
    
        }    @JvmStatic
        fun changeLevel(renderer: BasinRenderer, value: Float, min: Float, max: Float, te: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int): Float {
//        println(partialTicks)
            if (te.pos.x == 23) {
//            println(partialTicks)
            }
            val origin = MathHelper.clamp(value, min, max)
            return origin
        }
        @Environment(EnvType.CLIENT)
        @JvmStatic
        fun rotate(renderer: BasinRenderer, te: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int, fluidLevel: Float, level: Float, pos: BlockPos, random: Random, inv: Storage<ItemVariant>, stackCount: Int, stacks: MutableList<ItemStack>, anglePartition: Float, stack: ItemStack) {
            if (!InjectBasinTileEntity.BOILING)return
            te as InjectBasinTileEntity
            val world = te.world ?: return
            val hashCode = stack.item.hashCode()
            val hashOffset = hashCode.toDouble() / Int.MAX_VALUE
            val t1 = InjectBasinTileEntity.mapTemperature(te)
            val ticks = te.animationTicks + partialTicks * t1.pow(3)
            fun cycle(period: Int) = sin((ticks % period / period + hashOffset) * PI * 2) * 360
//        infix fun Float.cycle(cycle:Int) = this modAndDiv cycle
            val amplitude = min(min(fluidLevel, 0.15f), 1 - fluidLevel)

//        val cycle1 = 697
//        val partial1 = ((world.time + partialTicks) % cycle1 / cycle1 + hashOffset) * PI * 2
//        val sin1 = sin(partial1)
            val translation = 4.0 / 30
//        ms.translate(0.0,0.0,0.0)
//        val degree = sin(partial1) * 360
//        val radian = degree / 180 * PI
//        ms.translate(0.0,2.0,0.0)
            ms.translate(0.0, translation, 0.0)
            TransformStack.cast(ms)
              .rotateX(cycle(397))
              .rotateY(cycle(597))
              .rotateZ(cycle(797))
            ms.translate(0.0, -translation, 0.0)

//        ms.translate(-translation,-translation,-translation)
        }
        @JvmStatic
        fun calcFluidLevel(renderer: BasinRenderer, basin: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int): Float {
            val inputFluids = basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
            val outputFluids = basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
            val tanks = arrayOf(inputFluids, outputFluids)
            val totalUnits: Float = basin.getTotalFluidUnits(partialTicks)
            if (totalUnits < 1)
                return 0f
        
            var fluidLevel = MathHelper.clamp(totalUnits / (FluidConstants.BUCKET * 2), 0f, 1f)
        
            fluidLevel = 1 - (1 - fluidLevel) * (1 - fluidLevel)
        
            val xMin = 2 / 16f
            val xMax = 2 / 16f
            val yMin = 2 / 16f
            val yMax = yMin + 12 / 16f * fluidLevel
            val zMin = 2 / 16f
            val zMax = 14 / 16f
            return yMax
        }
    
    }
}