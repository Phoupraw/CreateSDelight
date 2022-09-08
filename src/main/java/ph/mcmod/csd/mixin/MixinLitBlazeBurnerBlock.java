package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.processing.burner.LitBlazeBurnerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.csd.game.InjectLitBlazeBurnerBlock;

import java.util.Random;
@Mixin(value = LitBlazeBurnerBlock.class, remap = false)
public class MixinLitBlazeBurnerBlock {
    private final ThreadLocal<BlockState> state = new ThreadLocal<>();
    private final ThreadLocal<World> world = new ThreadLocal<>();

    private final ThreadLocal<BlockPos> pos = new ThreadLocal<>();
    private final ThreadLocal<Random> random = new ThreadLocal<>();

    @Inject(method = "randomDisplayTick", at = @At("HEAD"))
    private void captureParas(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        this.state.set(state);
        this.world.set(world);
        this.pos.set(pos);
        this.random.set(random);
    }

    @ModifyArg(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addImportantParticle(Lnet/minecraft/particle/ParticleEffect;ZDDDDDD)V"), index = 3)
    private double modifyY(double y) {
        return InjectLitBlazeBurnerBlock.modifyY(y,state.get(),world.get(), pos.get(), random.get());
    }
}
