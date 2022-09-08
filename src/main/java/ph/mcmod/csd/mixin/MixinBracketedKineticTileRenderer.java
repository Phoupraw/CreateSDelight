package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.csd.game.InjectBracketedKineticTileRenderer;
@Mixin(BracketedKineticTileRenderer.class)
public class MixinBracketedKineticTileRenderer extends KineticTileEntityRenderer {

    public MixinBracketedKineticTileRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/contraptions/base/KineticTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"))
    private void renderRoastingItem(KineticTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, CallbackInfo ci) {
        InjectBracketedKineticTileRenderer.renderRoastingItem((BracketedKineticTileRenderer) (Object) this, te, partialTicks, ms, buffer, light, overlay);
    }
}
