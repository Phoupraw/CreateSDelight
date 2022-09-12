@file:Suppress("DEPRECATION")

package ph.mcmod.csd.game

import com.simibubi.create.content.contraptions.base.KineticTileEntity
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileRenderer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f
import ph.mcmod.csd.api.customModelData
import ph.mcmod.csd.api.modelHasDepth
import ph.mcmod.kum.get
import ph.mcmod.kum.isEmpty

interface InjectBracketedKineticTileRenderer {
    companion object {
        @JvmStatic
        fun renderRoastingItem(renderer: BracketedKineticTileRenderer, te: KineticTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int) {
            if (te !is InjectBracketedKineticTileEntity) return
            val roastingStorage = te.roastingStorage
            if (roastingStorage.isEmpty) return
            val itemRenderer = MinecraftClient.getInstance().itemRenderer
            val modelStack = roastingStorage.stack.apply {
                customModelData = 123
            }
            ms.push()
            ms.translate(0.5, 0.5, 0.5)
            val axis = KineticTileEntityRenderer.getRotationAxisOf(te) ?: return
            val offset = BracketedKineticTileRenderer.getShaftAngleOffset(axis, te.pos)
            val time = AnimationTickHolder.getRenderTime(te.world)
            val angle = (time * te.speed * 3f / 10 + offset) % 360 / 180 * Math.PI.toFloat()
            ms.multiply(Quaternion(axis[true].unitVector, angle, false))
            when (axis) {
                Direction.Axis.X -> ms.multiply(Quaternion(Vec3f(0f, 1f, 0f), 90f, true))
                Direction.Axis.Y -> ms.multiply(Quaternion(Vec3f(1f, 0f, 0f), 90f, true))
                Direction.Axis.Z -> {}
            }
            val model3d = modelStack.modelHasDepth(te.world)
            val scale = if (model3d) 1f else 1f
            ms.scale(scale, scale, scale)
            val count =
              if (model3d) 1
              else when (val c = modelStack.count) {
                  1 -> 1
                  in 2..8 -> 2
                  in 9..32 -> 3
                  in 33..63 -> 4
                  in 64..Int.MAX_VALUE -> 5
                  else -> error(c)
              }
            val gap = 0.16
            val start = 0.5 - (count - 1) * gap / 2
            ms.translate(0.0, 0.0, -0.5)
            for (i in 0 until count) {
                ms.push()
                ms.translate(0.0, 0.0, start + gap * i)
                itemRenderer.renderItem(modelStack, ModelTransformation.Mode.FIXED, light, overlay, ms, buffer, 0)
                ms.pop()
            }
            ms.pop()
        }
    }
}