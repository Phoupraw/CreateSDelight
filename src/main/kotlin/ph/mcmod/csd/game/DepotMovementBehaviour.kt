package ph.mcmod.csd.game

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.jozufozu.flywheel.util.transform.TransformStack
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionMatrices
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.model.json.ModelTransformation

object DepotMovementBehaviour : MovementBehaviour {
    private var MovementContext.startTime: Long
        get() = tileData.getLong("startTime")
        set(value) {
            tileData.putLong("startTime", value)
        }
    /**
     * 只在服务端调用
     */
    override fun startMoving(context: MovementContext) {
        
        context.world.time.also {
            (context.contraption.presentTileEntities[context.localPos] as? DepotTileEntity)?.apply {
                (getBehaviour(DepotBehaviour.TYPE) as InjectDepotBehaviour).startTime = it
                
            }
        }//.printS()
    }
    
    override fun stopMoving(context: MovementContext) {
        (context.contraption.presentTileEntities[context.localPos] as? DepotTileEntity)?.apply {
            notifyUpdate()
        }
    }
    override fun renderAsNormalTileEntity(): Boolean {
        return true
    }
    
    override fun renderInContraption(context: MovementContext, renderWorld: VirtualRenderWorld, matrices: ContraptionMatrices, buffer: VertexConsumerProvider) {
        val te = context.contraption.presentTileEntities[context.localPos] as? DepotTileEntity ?: return
        te as InjectDepotTileEntity
        val partialTicks = AnimationTickHolder.getPartialTicks(renderWorld)
        val ms = matrices.viewProjection
        val behaviour = te.getBehaviour(DepotBehaviour.TYPE)
        behaviour as InjectDepotBehaviour
        if (behaviour.startTime == -1L) {
            return
        }
        val itemStack = behaviour.heldItemStack
        val pos = context.localPos
        val light = WorldRenderer.getLightmapCoordinates(te.world, context.state, te.pos)//FIXME 如果使用这个亮度的话，物品是全黑的。
        
        val ticks = (renderWorld.time - behaviour.startTime) + partialTicks
        val angle = (ticks % 10) / 10.0 * 180
        ms.push()
        ms.translate(0.5, 0.85, 0.5)
        ms.translate(0.0, 3.0, 0.0)
        TransformStack.cast(ms)
          .rotateX(angle.toDouble())
        MinecraftClient.getInstance().itemRenderer.renderItem(itemStack, ModelTransformation.Mode.FIXED, 15728880/*全亮亮度，就像岩浆块那样*/, OverlayTexture.DEFAULT_UV, ms, buffer, 0)
        ms.pop()
        if (renderWorld.time - behaviour.startTime > 10) {
//            behaviour.startTime = -1
        }
    }
    
}