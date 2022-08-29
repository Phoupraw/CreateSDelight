package ph.mcmod.cs.game

import com.jozufozu.flywheel.backend.Backend
import com.jozufozu.flywheel.util.transform.TransformStack
import com.simibubi.create.AllBlockPartials
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelTileEntity
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer
import com.simibubi.create.foundation.utility.AngleHelper
import com.simibubi.create.foundation.utility.Iterate
import com.simibubi.create.foundation.utility.VecHelper
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import ph.mcmod.cs.api.getOrThrow
import kotlin.math.abs

class CopperTunnelRenderer(context: BlockEntityRendererFactory.Context) : SmartTileEntityRenderer<CopperTunnelBlockEntity>(context) {
    override fun renderSafe(te: CopperTunnelBlockEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay)
        if (Backend.canUseInstancing(te.world)) return
        val flapBuffer = CachedBufferer.partial(AllBlockPartials.BELT_TUNNEL_FLAP, te.cachedState)
        val vb = buffer.getBuffer(RenderLayer.getSolid())
        val pivot = VecHelper.voxelSpace(0.0, 10.0, 1.0)
        val msr = TransformStack.cast(ms)
        for (direction in Iterate.directions) {
            if (!te.flaps.containsKey(direction)) continue
            val horizontalAngle = AngleHelper.horizontalAngle(direction.opposite)
            val f = te.flaps.getOrThrow(direction).getValue(partialTicks)
            ms.push()
            msr.centre().rotateY(horizontalAngle.toDouble()).unCentre()
            for (segment in 0..3) {
                ms.push()
                val intensity = if (segment == 3) 1.5f else (segment + 1).toFloat()
                val abs = abs(f)
                var flapAngle = (MathHelper.sin(((1 - abs) * Math.PI * intensity).toFloat()) * 30 * f * if (direction.axis === Direction.Axis.X) 1 else -1)
                if (f > 0) flapAngle *= .5f
                msr.translate(pivot).rotateX(flapAngle.toDouble()).translateBack(pivot)
                flapBuffer.light(light).renderInto(ms, vb)
                ms.pop()
                ms.translate((-3 / 16f).toDouble(), 0.0, 0.0)
            }
            ms.pop()
        }
    }
    
}