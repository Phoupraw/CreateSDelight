package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainRenderer;
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.game.InjectItemDrainRenderer;
@Mixin(ItemDrainRenderer.class)
public class MixinItemDrainRenderer {
    private ItemDrainTileEntity te;
    private float partialTicks;

    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(D)Lnet/minecraft/util/math/Vec3d;"))
    private void capture(ItemDrainTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, CallbackInfo ci) {
        this.te = te;
        this.partialTicks = partialTicks;
    }

    @ModifyArg(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(D)Lnet/minecraft/util/math/Vec3d;"))
    private double modifyOffset(double value) {
        return InjectItemDrainRenderer.modifyOffset(te, partialTicks, value);
    }

    @ModifyVariable(method = "renderItem",  name = "offset", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction$AxisDirection;offset()I"))
    private float modifyAngle(float value) {
        return InjectItemDrainRenderer.modifyAngle(te, partialTicks, value);
    }
//    @ModifyVariable(method = "renderItem", print = true, name = "offset", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction$AxisDirection;offset()I")), at = @At(value = "LOAD", opcode = Opcodes.FLOAD, ordinal = 0,shift = At.Shift.BEFORE))
//    private double modifyAngle(double value) {
//        return InjectItemDrainRenderer.modifyOffset(te, partialTicks, value);
//    }
}
