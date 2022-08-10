package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileRenderer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.MixinDelegates;
@Mixin(SafeTileEntityRenderer.class)
public abstract class MixinSafeTileEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    @Inject(method = "render", at = @At("HEAD"))
    private void testSafeRender(T te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, CallbackInfo ci) {
        //noinspection unchecked
        MixinDelegates.testSafeRender((SafeTileEntityRenderer<T>) (Object) this, te, partialTicks, ms, buffer, light, overlay);
    }
}
