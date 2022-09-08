package ph.mcmod.csd.mixin;

import com.simibubi.create.content.logistics.block.depot.DepotBlock;
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
import ph.mcmod.csd.game.InjectDepotBlock;
@Mixin(DepotBlock.class)
public class MixinDepotBlock {
    @Inject(method = "onUse",at = @At("HEAD"),cancellable = true)
    private void flip(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        InjectDepotBlock.flip((DepotBlock)(Object)this,state,world,pos,player,hand,hitResult,cir);
    }
}
