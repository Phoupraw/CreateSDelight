package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.relays.elementary.AbstractSimpleShaftBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ph.mcmod.csd.game.InjectShaftBlock;
@Mixin(ShaftBlock.class)
public abstract class MixinShaftBlock extends AbstractSimpleShaftBlock {
    public MixinShaftBlock(Settings properties) {
        super(properties);
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void putRoastOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray, CallbackInfoReturnable<ActionResult> cir) {
        InjectShaftBlock.putRoastOnUse((ShaftBlock) (Object) this, state, world, pos, player, hand, ray, cir);
    }

    @Override
    public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
        ActionResult result = super.onWrenched(state, context);
        if (result != ActionResult.PASS) return result;
        return InjectShaftBlock.onWrenched((ShaftBlock) (Object) this, state, context);
    }
}
