package ph.mcmod.csd.game

import com.jozufozu.flywheel.util.transform.TransformStack
import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import ph.mcmod.kum.forEach
import kotlin.math.PI
import kotlin.math.sin

class DryingRackRenderer(context: BlockEntityRendererFactory.Context?) : SmartTileEntityRenderer<DryingRackBlockEntity>(context) {
    override fun renderSafe(te: DryingRackBlockEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay)
        val world = te.world ?: return
        ms.push()
        ms.translate(0.5, 0.5, 0.5)
        val x = te.cachedState.get(DryingRackBlock.AXIS_X)
        if (!x) {
            TransformStack.cast(ms).rotateY(90.0)
        }
        ms.scale(0.5f, 0.5f, 0.5f)
        ms.translate(-0.5, 0.0, 0.0)
        val tick = world.time + partialTicks
        te.inventory.forEach { index, stack, remove, set ->
            if (stack.isEmpty) return@forEach
            val itemRenderer = MinecraftClient.getInstance().itemRenderer
            ms.push()
            ms.translate(1.0 * index, 0.0, 0.0)
            val period = 289
            val angle = sin(((tick + te.pos.hashCode() * (index + 1) / 100.0) % period) / period * 2 * PI) * 5
            TransformStack.cast(ms)
              .translate(0.0,0.5,0.0)
              .rotateX(angle)
              .translate(0.0,-0.5,0.0)
            itemRenderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, ms, buffer, 0)
            ms.pop()
        }
        ms.pop()
    }
}