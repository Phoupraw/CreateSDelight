package ph.mcmod.csd.mixin;

import com.simibubi.create.content.logistics.block.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.block.depot.DepotRenderer;
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.csd.game.InjectDepotRenderer;
@Mixin(DepotRenderer.class)
public abstract class MixinDepotRenderer extends SafeTileEntityRenderer<DepotTileEntity> {
    @Redirect(method = "renderSafe(Lcom/simibubi/create/content/logistics/block/depot/DepotTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/block/depot/DepotRenderer;renderItemsOf(Lcom/simibubi/create/foundation/tileEntity/SmartTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILcom/simibubi/create/content/logistics/block/depot/DepotBehaviour;)V"))
    private void renderItems(SmartTileEntity offsetVec, float alongX, MatrixStack offset, VertexConsumerProvider sideOffset, int itemStack, int angle, DepotBehaviour behaviour, DepotTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        InjectDepotRenderer.renderItems((DepotRenderer) (Object) this, te, partialTicks, ms, buffer, light, overlay, behaviour);
    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/logistics/block/depot/DepotTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("RETURN"))
    private void renderFluids(DepotTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, CallbackInfo ci) {
        InjectDepotRenderer.renderFluids((DepotRenderer) (Object) this, te, partialTicks, ms, buffer, light, overlay);
    }
}
