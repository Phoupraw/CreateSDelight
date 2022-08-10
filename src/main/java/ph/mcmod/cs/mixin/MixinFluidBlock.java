package ph.mcmod.cs.mixin;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import ph.mcmod.cs.MixinDelegates;
@SuppressWarnings("deprecation")
@Mixin(FluidBlock.class)
public class MixinFluidBlock extends Block {
//static ItemVariant copyOf(ItemStack stack) {
//    return ItemVariant.of(stack.getItem(), stack.getNbt() != null ? stack.getNbt().copy() : null);
//}

    public MixinFluidBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return MixinDelegates.useBowlOnWater((FluidBlock) (Object) this, state, world, pos, player, hand, hit);
    }

}
