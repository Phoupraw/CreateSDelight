package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.processing.BasinRenderer;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ph.mcmod.csd.MixinDelegates;
import ph.mcmod.csd.game.InjectBasinRenderer;
import ph.mcmod.csd.game.InjectBasinTileEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
@Mixin(value = BasinRenderer.class, remap = false)
public abstract class MixinBasinRenderer extends SmartTileEntityRenderer<BasinTileEntity> {
    @Shadow
    protected abstract float renderFluids(BasinTileEntity basin, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay);

    private float partialTicks;
    private float fluidLevel;
    private BasinTileEntity te;
    private ItemStack stack;
    private MatrixStack ms;
    private VertexConsumerProvider buffer;
    private int light;
    private int overlay;
    private float level;
    private BlockPos pos;
    private Random random;
    private Storage<ItemVariant> inv;
    private int stackCount;
    private List<ItemStack> stacks;
    private float anglePartition;

    public MixinBasinRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Redirect(method = "renderSafe(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
    private float changeLevel(float value, float min, float max, BasinTileEntity basin, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        return InjectBasinRenderer.changeLevel((BasinRenderer) (Object) this, value, min, max, basin, partialTicks, ms, buffer, light, overlay);
    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;rotate(Lnet/minecraft/util/math/Vec3d;DLnet/minecraft/util/math/Direction$Axis;)Lnet/minecraft/util/math/Vec3d;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void capture(BasinTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, CallbackInfo ci, float fluidLevel, float level, BlockPos pos, Random r, Vec3d baseVector, Storage<ItemVariant> inv, int itemCount, List<ItemStack> stacks, float anglePartition, Iterator<ItemStack> stacksIterator, ItemStack stack) {
        this.te = te;
        this.partialTicks = partialTicks;
        this.ms = ms;
        this.buffer = buffer;
        this.light = light;
        this.overlay = overlay;
        this.fluidLevel = fluidLevel;
        this.level = level;
        this.pos = pos;
        this.random = r;
        this.inv = inv;
        this.stackCount = itemCount;
        this.stacks = stacks;
        this.anglePartition = anglePartition;
        this.stack = stack;
    }

    @ModifyArgs(method = "renderSafe(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;rotate(Lnet/minecraft/util/math/Vec3d;DLnet/minecraft/util/math/Direction$Axis;)Lnet/minecraft/util/math/Vec3d;"))
    private void changeHeight(Args args) {
        InjectBasinRenderer.changeHeight((BasinRenderer) (Object) this, args, te, partialTicks, ms, buffer, light, overlay, fluidLevel, level, pos, random, inv, stackCount, stacks, anglePartition, stack);
    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lcom/jozufozu/flywheel/util/transform/TransformStack;rotateX(D)Ljava/lang/Object;", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void rotate(BasinTileEntity basin, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, CallbackInfo ci, float fluidLevel, float level, BlockPos pos, Random r, Vec3d baseVector, Storage<ItemVariant> inv, int itemCount, List<ItemStack> stacks, float anglePartition, Iterator<ItemStack> stacksIterator, ItemStack stack, Vec3d itemPosition) {
        InjectBasinRenderer.rotate((BasinRenderer) (Object) this, te, partialTicks, ms, buffer, light, overlay, fluidLevel, level, pos, random, inv, stackCount, stacks, anglePartition, stack);
    }

    @Redirect(method = "renderSafe(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/processing/BasinRenderer;renderFluids(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)F"))
    private float calcFluidLevel(BasinRenderer renderer, BasinTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        if (!InjectBasinTileEntity.BOILING) return renderFluids(te, partialTicks, ms, buffer, light, overlay);
        return InjectBasinRenderer.calcFluidLevel(renderer, te, partialTicks, ms, buffer, light, overlay);
    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;getCachedState()Lnet/minecraft/block/BlockState;"))
    private void renderFluids(BasinTileEntity basin, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, CallbackInfo ci) {
//        MixinDelegates.renderFluids((BasinRenderer) (Object) this,  te, partialTicks, ms, buffer, light, overlay);
//        ms.push();
        renderFluids(basin, partialTicks, ms, buffer, light, overlay);
//        ms.pop();
    }
}
