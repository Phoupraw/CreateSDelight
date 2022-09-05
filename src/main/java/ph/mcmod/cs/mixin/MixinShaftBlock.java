package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ph.mcmod.cs.game.InjectShaftBlock;
@Mixin(ShaftBlock.class)
public class MixinShaftBlock {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void putRoastOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray, CallbackInfoReturnable<ActionResult> cir) {
        InjectShaftBlock.putRoastOnUse((ShaftBlock) (Object) this, state, world, pos, player, hand, ray, cir);
    }
}
